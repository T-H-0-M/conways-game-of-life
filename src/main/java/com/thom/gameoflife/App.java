package com.thom.gameoflife;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import java.util.Random;

public class App extends Application {
    private record Preset(String label, Runnable seed) {
        @Override
        public String toString() {
            return label;
        }
    }

    private static final String[] SNARK_VARIANTS = new String[] {
            ".............................OO....................",
            "............................O.O....................",
            "......................OO....O......................",
            "....................O..O..OO.OOOO..................",
            "....................OO.O.O.O.O..O..................",
            ".......................O.O.O.O.....................",
            ".......................O.O.OO......................",
            "........................O..........................",
            "...................................................",
            ".....................................OO............",
            "............................OO.......O.............",
            "............................OO.....O.O.............",
            ".........O.........................OO..............",
            ".........OOO.......................................",
            "............O........O.............................",
            "...........OO.......O..............................",
            "....................OOO............................",
            "...................................................",
            "...OO..............................................",
            "...O.....................OO........................",
            "OO.O......................O........................",
            "O..OOO....OO...........OOO.........................",
            ".OO...O...OO...........O......................O....",
            "...OOOO.....................OO..............OOOOO..",
            "...O...............OO........O.............O.....O.",
            "....OOO............O.O.......O.O............OOO..O.",
            ".......O.............O........OO...............O.OO",
            "..OOOOO..............OO.....................OOOO..O",
            ".O..O......................O...........OO...O...OO.",
            ".OO......................OOO...........OO....OOO...",
            "........................O......................O...",
            "........................OO.....................O.OO",
            "..............................................OO.OO",
            "...................................................",
            "...................................................",
            "......................................OO...........",
            "......................................O............",
            ".......................................OOO.........",
            "..............OO.........................O.........",
            ".............O.O.....OO............................",
            ".............O.......OO............................",
            "............OO.....................................",
            "...................................................",
            "..........................O........................",
            "................OO....OO.O.O.......................",
            "...............O..O..O.O.O.O.......................",
            "................OO...O.O.O.OO......................",
            "..................OOOO.OO..O.......................",
            "..................O...O....O.......................",
            "...................O..O.OOO........................",
            "....................O.O.O..........................",
            ".....................O.............................",
    };

    private GameOfLife model;
    private LifeRenderer view;
    private Timeline timeline;
    private boolean isRunning = false;

    @Override
    public void start(Stage stage) {
        int rows = 200;
        int cols = 200;
        int cellSize = 4;
        model = new GameOfLife(rows, cols);
        view = new LifeRenderer(rows, cols, cellSize);

        ComboBox<Preset> presetBox = new ComboBox<>(FXCollections.observableArrayList(
                new Preset("Random Seed", this::randomSeed),
                new Preset("Kite Clusters", this::seedKiteClusters),
                new Preset("Gemini", this::seedGemini),
                new Preset("Spaceship Takeoff", this::seedSpaceshipTakeoff),
                new Preset("Snark", this::seedSnark)));
        presetBox.getSelectionModel().select(1);

        Preset initialPreset = presetBox.getValue();
        if (initialPreset != null) {
            initialPreset.seed().run();
        }
        view.render(model);

        Button playPause = new Button("Play");
        Button step = new Button("Step");
        Button reset = new Button("Reset");
        playPause.setOnAction(e -> toggleRun(playPause));
        step.setOnAction(e -> {
            model.step();
            view.render(model);
        });
        reset.setOnAction(e -> {
            pause(playPause);
            Preset selectedPreset = presetBox.getValue();
            if (selectedPreset != null) {
                selectedPreset.seed().run();
            }
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
        HBox controls = new HBox(20, playPause, step, reset, presetBox);
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

    private void pause(Button playPause) {
        isRunning = false;
        timeline.pause();
        playPause.setText("Play");
    }

    private void seedGlider(int topRow, int leftCol) {
        model.setCell(topRow + 0, leftCol + 1, model.ALIVE);
        model.setCell(topRow + 1, leftCol + 2, model.ALIVE);
        model.setCell(topRow + 2, leftCol + 0, model.ALIVE);
        model.setCell(topRow + 2, leftCol + 1, model.ALIVE);
        model.setCell(topRow + 2, leftCol + 2, model.ALIVE);
    }

    private void setAliveWrapped(int row, int col) {
        int rr = Math.floorMod(row, model.getRows());
        int cc = Math.floorMod(col, model.getColumns());
        model.setCell(rr, cc, model.ALIVE);
    }

    private void seedGosperGliderGun(int topRow, int leftCol, boolean flipHorizontally) {
        final int width = 36;
        final int height = 9;

        final int[][] cells = new int[][] {
                { 0, 4 }, { 0, 5 }, { 1, 4 }, { 1, 5 },
                { 10, 4 }, { 10, 5 }, { 10, 6 },
                { 11, 3 }, { 11, 7 },
                { 12, 2 }, { 12, 8 },
                { 13, 2 }, { 13, 8 },
                { 14, 5 },
                { 15, 3 }, { 15, 7 },
                { 16, 4 }, { 16, 5 }, { 16, 6 },
                { 17, 5 },
                { 20, 2 }, { 20, 3 }, { 20, 4 },
                { 21, 2 }, { 21, 3 }, { 21, 4 },
                { 22, 1 }, { 22, 5 },
                { 24, 0 }, { 24, 1 }, { 24, 5 }, { 24, 6 },
                { 34, 2 }, { 34, 3 },
                { 35, 2 }, { 35, 3 },
        };

        for (int[] cell : cells) {
            int x = cell[0];
            int y = cell[1];
            int xx = flipHorizontally ? (width - 1 - x) : x;
            setAliveWrapped(topRow + y, leftCol + xx);
        }
    }

    private void seedKiteClusters() {
        model.clear();
        int rows = model.getRows();
        int cols = model.getColumns();
        final int gunWidth = 36;
        final int gunHeight = 9;
        int left = Math.max(20, cols / 10);
        int right = Math.max(0, cols - Math.max(20, cols / 10) - gunWidth);
        int topA = Math.max(20, rows / 3);
        int topB = Math.max(20, (rows * 2) / 3);
        int topC = Math.max(20, rows / 2);
        seedGosperGliderGun(topA, left, false);
        seedGosperGliderGun(topB, left, false);
        seedGosperGliderGun(Math.max(20, topC - gunHeight / 2), right, true);
    }

    private void seedGemini() {
        // Two mirrored Gosper glider guns ("twins") shooting toward each other.
        model.clear();

        int rows = model.getRows();
        int cols = model.getColumns();

        final int gunWidth = 36;
        final int gunHeight = 9;

        int margin = Math.max(20, cols / 10);
        int left = margin;
        int right = Math.max(0, cols - margin - gunWidth);

        int top = Math.max(20, (rows / 2) - (gunHeight / 2));

        seedGosperGliderGun(top, left, false);
        seedGosperGliderGun(top, right, true);
    }

    private void seedSpaceshipTakeoff() {
        model.clear();

        int rows = model.getRows();
        int cols = model.getColumns();

        int top = Math.max(10, (rows / 2) - 2);
        int left = Math.max(10, (cols / 2) - 4);
        seedDiehard(top, left);
    }

    private void seedDiehard(int topRow, int leftCol) {
        final int[][] cells = new int[][] {
                { 6, 0 },
                { 0, 1 }, { 1, 1 },
                { 1, 2 }, { 5, 2 }, { 6, 2 }, { 7, 2 },
        };

        for (int[] cell : cells) {
            int x = cell[0];
            int y = cell[1];
            setAliveWrapped(topRow + y, leftCol + x);
        }
    }

    private void seedSnark() {
        model.clear();

        int rows = model.getRows();
        int cols = model.getColumns();

        int patternHeight = SNARK_VARIANTS.length;
        int patternWidth = 0;
        for (String line : SNARK_VARIANTS) {
            patternWidth = Math.max(patternWidth, line.length());
        }

        int top = Math.max(0, (rows - patternHeight) / 2);
        int left = Math.max(0, (cols - patternWidth) / 2);
        seedFromAscii(top, left, SNARK_VARIANTS);
    }

    private void seedFromAscii(int topRow, int leftCol, String[] lines) {
        for (int y = 0; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == 'O') {
                    setAliveWrapped(topRow + y, leftCol + x);
                }
            }
        }
    }

    private void randomSeed() {
        model.clear();
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
