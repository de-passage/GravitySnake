package com.example.gravitysnake;

import static java.util.stream.Collectors.toList;

import static com.example.gravitysnake.Utility.collides;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

// Snake game logic. Unlike a classic snake game,
// parts of the snake can overlap. The snake parts
// are a list of coordinates with a size that is determined
// by this class. The snake has no knowledge of its own size
// and will happily be discontinued. It may also run out of
// the bounds of the game, it is up to the game to constrain
// the snake.
public class Game {

    // The frequency at which food is spawned. If there is no food,
    // a new food is spawned regardless of the frequency.
    private static final int FOOD_SPAWN_FREQUENCY = 10;
    private final int cellSize;
    private final Snake snake;
    private final int width;
    private final int height;

    private final List<Coordinate> food = new ArrayList<>();

    private int timeSinceLastFood = 0;

    public Game(int width, int height, int cellSize) {
        this.cellSize = cellSize;
        this.width = width;
        this.height = height;

        snake = new Snake(width / 2, height / 2, Snake.Direction.DOWN);
    }

    // Return true if the game is playing, false if the game is over.
    // The game is over when the snake eats itself.
    public synchronized boolean runOneTick() {
        if (timeSinceLastFood >= FOOD_SPAWN_FREQUENCY) {
            food.add(spawnFood());
            timeSinceLastFood = 0;
        } else {
            timeSinceLastFood++;
        }
        snake.move();
        boolean eatItself = collideWithSelf(snake);
        for (Coordinate c : food) {
            if (collides(snake, c, cellSize)) {
                snake.grow();
                food.remove(c);
                break;
            }
        }
        return !eatItself;
    }

    // Compute an appropriate spawn location for the food.
    // It should not overlap with an existing food or the snake.
    private Coordinate spawnFood() {
        Coordinate coordinate;
        do {
            coordinate = new Coordinate((int) (Math.random() * (width - cellSize)),
                                        (int) (Math.random() * (height - cellSize)));
        } while (collides(snake, coordinate, cellSize) || collideWithFood(coordinate));
        return coordinate;
    }

    private boolean collideWithFood(final Coordinate coordinate) {
        return food.stream().anyMatch(c -> collides(coordinate, c, cellSize));
    }

    // Returns the coordinates of the snake constrained to the bounds of the game.
    // The snake is allowed to move out of bounds, but it will wrap around.
    @NonNull
    @Contract(value = "_ -> new", pure = true)
    private Coordinate constrain(@NonNull Coordinate coordinate) {
        return new Coordinate((coordinate.x + width) % width, (coordinate.y + height) % height);
    }

    // Returns the coordinates of the snake constrained to the bounds of the game.
    public synchronized List<Coordinate> snakeCoordinates() {
        return snake.coordinates().map(this::constrain).collect(toList());
    }

    public int getCellSize() {
        return cellSize;
    }

    public synchronized List<Coordinate> foodCoordinates() {
        return new ArrayList<>(food);
    }

    public synchronized void setDirection(Snake.Direction direction) {
        snake.setDirection(direction);
    }

    public synchronized void setVelocity(int velocity) {
        snake.setVelocity(velocity);
    }

    private boolean collideWithSelf(Snake snake) {
        return snake.coordinates().anyMatch(c -> collides(snake.head(), c, cellSize));
    }
}
