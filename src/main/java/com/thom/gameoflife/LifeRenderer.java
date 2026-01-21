package com.thom.gameoflife;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class LifeRenderer {

    private int cellSize;
    private Canvas canvas;

    public LifeRenderer(int columns, int rows, int cellSize) {
        this.cellSize = cellSize;
        this.canvas = new Canvas(columns * cellSize, rows * cellSize);
    }

    public void render(GameOfLife model) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.web("#0b0f14"));
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        g.setFill(Color.web("#e6f1ff"));
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getColumns(); c++) {
                if (model.getCell(r, c) == model.ALIVE) {
                    g.fillRect(c * cellSize, r * cellSize, cellSize, cellSize);
                }
            }
        }
    }

    public Canvas getCanvas() {
        return canvas;
    }

}
