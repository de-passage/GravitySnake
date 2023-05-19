package com.example.gravitysnake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Handle the drawing of the snake and the food.
 */
public class GravitySnakeView extends View {

    private final Paint snakePaint = new Paint();
    private final Paint foodPaint = new Paint();
    private List<Coordinate> snakeCoordinates = new ArrayList<>();
    private List<Coordinate> foodCoordinates = new ArrayList<>();
    private int cellSize;

    public GravitySnakeView(Context context) {
        super(context);
        init();
    }

    public GravitySnakeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GravitySnakeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        snakePaint.setColor(Color.GREEN);
        snakePaint.setStyle(Paint.Style.FILL);

        foodPaint.setColor(Color.RED);
        foodPaint.setStyle(Paint.Style.FILL);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Coordinate c : snakeCoordinates) {
            canvas.drawRect(c.x, c.y, c.x +  cellSize,
                            c.y + cellSize, snakePaint);
        }

        for (Coordinate c : foodCoordinates) {
            canvas.drawRect(c.x, c.y, c.x + cellSize,
                            c.y + cellSize, foodPaint);
        }
    }

    public void render(@NonNull final Game game) {

        this.cellSize = game.getCellSize();
        this.snakeCoordinates = game.snakeCoordinates();
        this.foodCoordinates = game.foodCoordinates();
        invalidate();
    }
}