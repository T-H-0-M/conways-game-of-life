package com.thom.gameoflife;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameOfLifeTest {

    private GameOfLife gameOfLife;

    @BeforeEach
    void setUp() {
        gameOfLife = new GameOfLife(10, 10);
    }

    @Test
    void countsNoActiveNeighbors() {
        int activeNeighbors = gameOfLife.countActiveNeighbors(5, 5);
        assertEquals(0, activeNeighbors);
    }

    @Test
    void countsActiveNeighbors() {
        gameOfLife.setCell(5, 6, gameOfLife.ALIVE);
        gameOfLife.setCell(6, 6, gameOfLife.ALIVE);
        gameOfLife.setCell(4, 4, gameOfLife.ALIVE);
        gameOfLife.setCell(4, 5, gameOfLife.ALIVE);

        int activeNeighbors = gameOfLife.countActiveNeighbors(5, 5);
        assertEquals(4, activeNeighbors);
    }

    @Test
    void stepKillsLonelyCell() {
        gameOfLife = new GameOfLife(5, 5);
        gameOfLife.setCell(2, 2, gameOfLife.ALIVE);

        gameOfLife.step();

        assertEquals(gameOfLife.DEAD, gameOfLife.getCell(2, 2));
    }

    @Test
    void stepKeepsBlockStable() {
        gameOfLife = new GameOfLife(6, 6);

        // 2x2 block (still life)
        gameOfLife.setCell(2, 2, gameOfLife.ALIVE);
        gameOfLife.setCell(2, 3, gameOfLife.ALIVE);
        gameOfLife.setCell(3, 2, gameOfLife.ALIVE);
        gameOfLife.setCell(3, 3, gameOfLife.ALIVE);

        gameOfLife.step();

        assertEquals(gameOfLife.ALIVE, gameOfLife.getCell(2, 2));
        assertEquals(gameOfLife.ALIVE, gameOfLife.getCell(2, 3));
        assertEquals(gameOfLife.ALIVE, gameOfLife.getCell(3, 2));
        assertEquals(gameOfLife.ALIVE, gameOfLife.getCell(3, 3));
        assertEquals(gameOfLife.DEAD, gameOfLife.getCell(1, 1));
    }

    @Test
    void stepBlinksOscillator() {
        gameOfLife = new GameOfLife(7, 7);

        // Vertical blinker centered at (3,3)
        gameOfLife.setCell(2, 3, gameOfLife.ALIVE);
        gameOfLife.setCell(3, 3, gameOfLife.ALIVE);
        gameOfLife.setCell(4, 3, gameOfLife.ALIVE);

        gameOfLife.step();

        // Should become horizontal blinker
        assertEquals(gameOfLife.ALIVE, gameOfLife.getCell(3, 2));
        assertEquals(gameOfLife.ALIVE, gameOfLife.getCell(3, 3));
        assertEquals(gameOfLife.ALIVE, gameOfLife.getCell(3, 4));
        assertEquals(gameOfLife.DEAD, gameOfLife.getCell(2, 3));
        assertEquals(gameOfLife.DEAD, gameOfLife.getCell(4, 3));

        gameOfLife.step();

        // And back to vertical
        assertEquals(gameOfLife.ALIVE, gameOfLife.getCell(2, 3));
        assertEquals(gameOfLife.ALIVE, gameOfLife.getCell(3, 3));
        assertEquals(gameOfLife.ALIVE, gameOfLife.getCell(4, 3));
        assertEquals(gameOfLife.DEAD, gameOfLife.getCell(3, 2));
        assertEquals(gameOfLife.DEAD, gameOfLife.getCell(3, 4));
    }
}
