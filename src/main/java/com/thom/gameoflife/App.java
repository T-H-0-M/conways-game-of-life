package com.thom.gameoflife;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.animation.KeyFrame;

public class App extends Application {
    private GameOfLife model;
    private LifeRenderer view;
    private Timeline timeline;
    private boolean isRunning = false;

    @Override
    public void start(Stage stage) {
        int rows = 60;
        int cols = 80;
        int cellSize = 10;
        model = new GameOfLife(rows, cols);
        view = new LifeRenderer(rows, cols, cellSize);
        seed();
        view.render(model);
        Button playPause = new Button("Play");
        Button step = new Button("Step");
        playPause.setOnAction(e -> toggleRun(playPause));
        step.setOnAction(e -> {
            model.step();
            view.render(model);
        });
        view.getCanvas().setOnMouseClicked(e -> {
            int col = (int) (e.getX() / cellSize);
            int row = (int) (e.getY() / cellSize);
            if (row >= 0 && row < rows && col >= 0 && col < cols) {
                model.toggleCell(row, col);
                view.render(model);
            }
        });
        timeline = new Timeline(new KeyFrame(Duration.millis(120), e -> {
            model.step();
            view.render(model);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        HBox controls = new HBox(10, playPause, step);
        VBox root = new VBox(10, controls, view.getCanvas());
        Scene scene = new Scene(root);
        stage.setTitle("Conway's Game of Life");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    private void toggleRun(Button playPause) {
        isRunning = !isRunning;
        if (isRunning) {
            timeline.play();
            playPause.setText("Pause");
        } else {
            timeline.pause();
            playPause.setText("Play");
        }
    }

    private void seed() {
        model.setCell(1, 2, model.ALIVE);
        model.setCell(2, 3, model.ALIVE);
        model.setCell(3, 1, model.ALIVE);
        model.setCell(3, 2, model.ALIVE);
        model.setCell(3, 3, model.ALIVE);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
