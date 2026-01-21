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
import java.util.Random;

public class App extends Application {
    private GameOfLife model;
    private LifeRenderer view;
    private Timeline timeline;
    private boolean isRunning = false;

    @Override
    public void start(Stage stage) {
        int rows = 1000;
        int cols = 1000;
        int cellSize = 2;
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
        timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            model.step();
            view.render(model);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        HBox controls = new HBox(10, playPause, step);
        VBox root = new VBox(10, controls, view.getCanvas());
        Scene scene = new Scene(root);
        stage.setTitle("Conway's Game of Life");
        stage.setScene(scene);
        stage.show();
        stage.sizeToScene();
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

    private void seedGlider(int topRow, int leftCol) {
        model.setCell(topRow + 0, leftCol + 1, model.ALIVE);
        model.setCell(topRow + 1, leftCol + 2, model.ALIVE);
        model.setCell(topRow + 2, leftCol + 0, model.ALIVE);
        model.setCell(topRow + 2, leftCol + 1, model.ALIVE);
        model.setCell(topRow + 2, leftCol + 2, model.ALIVE);
    }

    private void seed() {
        model.clear();
        // seedGlider(0, 0);

        int rows = model.getRows();
        int cols = model.getColumns();
        Random rng = new Random(); // or new Random(1234) for repeatable runs
        int clusterCount = Math.max(3, (rows * cols) / 500); // ~9 clusters for 60x80
        int minRadius = 2;
        int maxRadius = 7;
        double baseDensity = 0.75; // higher = denser clusters
        for (int k = 0; k < clusterCount; k++) {
            int cr = rng.nextInt(rows);
            int cc = rng.nextInt(cols);
            int radius = rng.nextInt(maxRadius - minRadius + 1) + minRadius;
            for (int r = cr - radius; r <= cr + radius; r++) {
                for (int c = cc - radius; c <= cc + radius; c++) {
                    double d = Math.hypot(r - cr, c - cc);
                    if (d > radius)
                        continue;
                    // falloff: center is dense, edges sparse
                    double p = baseDensity * (1.0 - (d / (radius + 1.0)));
                    if (rng.nextDouble() < p) {
                        int rr = Math.floorMod(r, rows);
                        int cc2 = Math.floorMod(c, cols);
                        model.setCell(rr, cc2, model.ALIVE);
                    }
                }
            }
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
