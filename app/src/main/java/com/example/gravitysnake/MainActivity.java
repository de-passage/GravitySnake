package com.example.gravitysnake;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.ViewTreeObserver;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private final int cellSize = 50;
    private final int MIN_VELOCITY = 1;
    private final int MAX_VELOCITY = cellSize;
    private Thread thread;
    private Game game;
    private GravitySnakeView gravitySnakeView;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean accelerometerSet = false;
    private boolean magnetometerSet = false;
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

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

        int width = gravitySnakeView.getWidth() / cellSize;
        int height = gravitySnakeView.getHeight() / cellSize;
        final Game game = new Game(width, height, cellSize);

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        Thread.sleep(1000 / 60); // 60 FPS
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gravitySnakeView.render(game);
                        }
                    });
                }
                while (game.runOneTick());
            }
        });
        thread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, magnetometer);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (game == null) {
            return;
        }

        if (event.sensor == accelerometer) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
            accelerometerSet = true;
        } else if (event.sensor == magnetometer) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
            magnetometerSet = true;
        }

        if (accelerometerSet && magnetometerSet) {
            SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer,
                                            lastMagnetometer);
            SensorManager.getOrientation(rotationMatrix, orientation);
            float azimuthInRadians = orientation[0];
            float azimuthInDegrees = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;

            Snake.Direction direction;
            int value = computeValue(azimuthInDegrees, MIN_VELOCITY, MAX_VELOCITY);
            if (azimuthInDegrees >= 0 && azimuthInDegrees < 90) {
                direction = Snake.Direction.UP;
                game.setDirection(direction);
                game.setVelocity(value);
            } else if (azimuthInDegrees >= 90 && azimuthInDegrees < 180) {
                direction = Snake.Direction.RIGHT;
                game.setDirection(direction);
                game.setVelocity(value);
            } else if (azimuthInDegrees >= 180 && azimuthInDegrees < 270) {
                direction = Snake.Direction.DOWN;
                game.setDirection(direction);
                game.setVelocity(value);
            } else if (azimuthInDegrees >= 270 && azimuthInDegrees < 360) {
                direction = Snake.Direction.LEFT;
                game.setDirection(direction);
                game.setVelocity(value);
            }
        }
    }

    private int computeValue(float degree, int min, int max) {
        return (int) ((degree / 360) * (max - min) + min);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}