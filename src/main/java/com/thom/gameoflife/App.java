package com.thom.gameoflife;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws InterruptedException {
        GameOfLife game = new GameOfLife(10, 10);

        game.setCell(1, 2, game.ALIVE);
        game.setCell(2, 3, game.ALIVE);
        game.setCell(3, 1, game.ALIVE);
        game.setCell(3, 2, game.ALIVE);
        game.setCell(3, 3, game.ALIVE);

        // Run 5 generations
        for (int i = 0; i < 5; i++) {
            game.printGrid();
            game.step();
            Thread.sleep(1000); // Pause to see the animation
        }
    }
}
