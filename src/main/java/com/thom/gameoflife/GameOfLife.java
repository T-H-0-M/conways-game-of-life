package com.thom.gameoflife;

public class GameOfLife {
    public final byte ALIVE = 1;
    public final byte DEAD = 0;

    private int rows;
    private int columns;
    private byte[][] currentBoard; // readboard
    private byte[][] nextBoard;

    public GameOfLife(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.currentBoard = new byte[rows][columns];
        this.nextBoard = new byte[rows][columns];
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
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                int activeNeighbors = countActiveNeighbors(i, j);
                byte currentState = currentBoard[i][j];
                if (currentState == ALIVE && activeNeighbors < 2) {
                    nextBoard[i][j] = DEAD;
                } else if (currentState == ALIVE && activeNeighbors > 3) {
                    nextBoard[i][j] = DEAD;
                } else if (currentState == DEAD && activeNeighbors == 3) {
                    nextBoard[i][j] = ALIVE;
                } else {
                    nextBoard[i][j] = currentState;
                }
            }
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
