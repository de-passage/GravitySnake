package com.example.gravitysnake;

import androidx.annotation.NonNull;

public abstract class Utility {
    // Compute whether the two squares of size cellSize centered on left and right overlap.
    public static boolean collides(@NonNull Coordinate left, @NonNull Coordinate right,
                                 int cellSize) {
        final int leftSquareLeft = left.x - cellSize / 2;
        final int leftSquareRight = left.x + cellSize / 2;
        final int leftSquareTop = left.y - cellSize / 2;
        final int leftSquareBottom = left.y + cellSize / 2;

        final int rightSquareLeft = right.x - cellSize / 2;
        final int rightSquareRight = right.x + cellSize / 2;
        final int rightSquareTop = right.y - cellSize / 2;
        final int rightSquareBottom = right.y + cellSize / 2;

        boolean intersect = !(leftSquareLeft > rightSquareRight ||
                leftSquareRight < rightSquareLeft ||
                leftSquareTop > rightSquareBottom ||
                leftSquareBottom < rightSquareTop);
        return intersect;
    }

    public static boolean collides(@NonNull final Snake snake, final Coordinate coordinate,
                                 int cellSize) {
        return snake.coordinates().anyMatch(c -> collides(c, coordinate, cellSize));
    }

}
