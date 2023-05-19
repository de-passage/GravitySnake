package com.example.gravitysnake;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Thread thread;
    private Game game;
    private GravitySnakeView gravitySnakeView;
    private SensorManager sensorManager;
    private Sensor gravitySensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        gravitySnakeView = findViewById(R.id.snake_game_view);
        gravitySnakeView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                gravitySnakeView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                runGame();
            }
        });
    }

    private void runGame() {
        if (thread != null) {
            try {
                thread.join(10_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int cellSize = 50;
        int width = gravitySnakeView.getWidth() / cellSize;
        int height = gravitySnakeView.getHeight() / cellSize;
        game = new Game(width, height, cellSize);

        thread = new Thread(() -> {
            do {
                try {
                    Thread.sleep(1000 / 60); // 60 FPS
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> gravitySnakeView.render(game));
            }
            while (game.runOneTick());
        });
        thread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, gravitySensor);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (game == null) {
            return;
        }
        final int MAX_VELOCITY = 30;
        final int MIN_VELOCITY = 1;
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            float x = event.values[0]; // Gravity force along the x-axis
            float y = event.values[1]; // Gravity force along the y-axis

            // Get the absolute values to determine the direction of the greater force.
            float absX = Math.abs(x);
            float absY = Math.abs(y);

            if (absX > absY) {
                // The x-component of the gravity is stronger, so we set the direction to either LEFT or RIGHT.
                if (x > 0) {
                    game.setDirection(Snake.Direction.LEFT);
                } else {
                    game.setDirection(Snake.Direction.RIGHT);
                }

                // Scale the velocity based on the gravity
                game.setVelocity((int) (absX * (MAX_VELOCITY - MIN_VELOCITY) / SensorManager.GRAVITY_EARTH) +
                                         MIN_VELOCITY);
            } else {
                // The y-component of the gravity is stronger, so we set the direction to either UP or DOWN.
                if (y > 0) {
                    game.setDirection(Snake.Direction.DOWN);
                } else {
                    game.setDirection(Snake.Direction.UP);
                }

                // Scale the velocity based on the gravity
                game.setVelocity((int) (absY * (MAX_VELOCITY - MIN_VELOCITY) / SensorManager.GRAVITY_EARTH) +
                                         MIN_VELOCITY);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}