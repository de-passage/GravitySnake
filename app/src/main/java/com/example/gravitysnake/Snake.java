package com.example.gravitysnake;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Snake {

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private Direction direction;

    // The snake is represented as a list of x and y coordinates.
    // The head is at index 0.
    final List<Integer> xs = new ArrayList<>();
    final List<Integer> ys = new ArrayList<>();

    int velocity = 1;

    int distanceTraveled = 0;
    final int bodySize;

    Deque<Coordinate> path = new ArrayDeque<>();

    public Snake(int x, int y, Direction direction, int bodySize) {
        xs.add(x);
        ys.add(y);
        this.direction = direction;
        this.bodySize = bodySize;
    }

    // Grow the snake by one unit.
    public void grow() {
        xs.add(xs.get(xs.size() - 1));
        ys.add(ys.get(ys.size() - 1));
    }

    // Move the snake one step in the current direction.
    public void move() {
        for (int i = xs.size() - 1; i > 0; i--) {
            xs.set(i, xs.get(i - 1));
            ys.set(i, ys.get(i - 1));
        }
        switch (direction) {
            case UP:
                ys.set(0, ys.get(0) - velocity);
                break;
            case DOWN:
                ys.set(0, ys.get(0) + velocity);
                break;
            case LEFT:
                xs.set(0, xs.get(0) - velocity);
                break;
            case RIGHT:
                xs.set(0, xs.get(0) + velocity);
                break;
        }

        distanceTraveled += velocity;
        path.addFirst(head());
    }

    // Define the direction of the snake.
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    // Define the velocity of the snake.
    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    // Return the coordinates of the snake.
    public Stream<Coordinate> coordinates() {
        return Stream.iterate(0, i -> i + 1)
            .limit(xs.size())
            .map(i -> new Coordinate(xs.get(i), ys.get(i)));
    }

    public Coordinate head() {
        return new Coordinate(xs.get(0), ys.get(0));
    }

    public boolean bitesItself() {
        return coordinates().anyMatch(this::checkCollision);
    }

    private boolean checkCollision(Coordinate bodyPart) {
        while (distanceTraveled > bodySize && !path.isEmpty()) {
            distanceTraveled -= distance(Objects.requireNonNull(path.peekLast()),
                                         Objects.requireNonNull(path.peekFirst()));
            path.removeLast();
        }

        return !path.contains(bodyPart);
    }

    private int distance(Coordinate p1, Coordinate p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    public int size() {
        return xs.size();
    }
}
