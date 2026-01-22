package com.thom.gameoflife;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CyclicBarrier;

public class GameOfLife {
    public final byte ALIVE = 1;
    public final byte DEAD = 0;

    private int rows;
    private int columns;
    private byte[][] currentBoard; // readboard
    private byte[][] nextBoard;
    private ExecutorService gameEngine;
    private CyclicBarrier barrier;
    private int numThreads;

    public GameOfLife(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.currentBoard = new byte[rows][columns];
        this.nextBoard = new byte[rows][columns];
        this.numThreads = Runtime.getRuntime().availableProcessors();
        this.gameEngine = Executors.newFixedThreadPool(this.numThreads);
        // INFO: this needs to be expected threads + 1, always account for the main
        // thread or it will eat your lunch
        this.barrier = new CyclicBarrier(this.numThreads + 1);
    }

    public int countActiveNeighbors(int row, int col) {
        int activeNeighbors = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                if (currentBoard[Math.floorMod(row + i, this.rows)][Math.floorMod(col + j, this.columns)] == ALIVE) {
                    activeNeighbors++;
                }
            }
        }
        return activeNeighbors;
    }

    public void step() {
        // INFO: start col is inclusive

        int chunk = Math.round(this.columns / this.numThreads);
        for (int i = 0; i < this.numThreads; ++i) {
            final int startCol = i * chunk;
            final int endCol = i == this.numThreads ? this.columns : startCol + chunk;

            // just my personal memo - execute returns void, submit returns Future<T>
            gameEngine.execute(() -> {
                try {
                    for (int x = startCol; x < endCol; x++) {
                        for (int y = 0; y < this.columns; y++) {
                            int activeNeighbors = countActiveNeighbors(x, y);
                            byte currentState = currentBoard[x][y];
                            if (currentState == ALIVE && activeNeighbors < 2) {
                                nextBoard[x][y] = DEAD;
                            } else if (currentState == ALIVE && activeNeighbors > 3) {
                                nextBoard[x][y] = DEAD;
                            } else if (currentState == DEAD && activeNeighbors == 3) {
                                nextBoard[x][y] = ALIVE;
                            } else {
                                nextBoard[x][y] = currentState;
                            }
                        }
                    }
                    barrier.await();
                } catch (Exception e) {
                    System.out.println("yuh, thread broken");
                }
            });

        }

        try {
            barrier.await();
        } catch (Exception e) {
            System.out.println("yuh, broken");
        }

        // INFO: this is done because currentBoard = nextBoard will reference the same
        // obj after one iteration
        byte[][] tmp = currentBoard;
        currentBoard = nextBoard;
        nextBoard = tmp;
    }

    // helper methods for testing
    public void setCell(int row, int col, byte value) {
        currentBoard[row][col] = value;
    }

    public void clear() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                currentBoard[i][j] = DEAD;
            }
        }
    }

    public byte getCell(int row, int col) {
        return currentBoard[row][col];
    }

    public void toggleCell(int row, int col) {
        if (currentBoard[row][col] == ALIVE) {
            currentBoard[row][col] = DEAD;
            return;
        }
        currentBoard[row][col] = ALIVE;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public void printGrid() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                System.out.print(currentBoard[i][j] == 1 ? "■ " : ". ");
            }
            System.out.println();
        }
        System.out.println("---");
    }
}
