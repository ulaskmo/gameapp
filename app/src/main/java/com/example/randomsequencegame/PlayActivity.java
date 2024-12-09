package com.example.randomsequencegame;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ArrayList<Integer> sequence;
    private int currentStep = 0;
    private boolean inputEnabled = false; // Prevent accidental inputs
    private boolean movementDetected = false; // To prevent multiple detections
    private static final float THRESHOLD = 6.0f; // Minimum tilt to consider
    private static final float DOMINANCE_FACTOR = 1.5f; // Axis dominance threshold
    private TextView goTextView;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sequence = getIntent().getIntegerArrayListExtra("sequence");

        goTextView = findViewById(R.id.goTextView);
        handler = new Handler();

        Toast.makeText(this, "Match the sequence by tilting the phone!", Toast.LENGTH_SHORT).show();

        // Start the game with the first "GO!"
        showGoForNextColor();
    }

    private void showGoForNextColor() {
        if (currentStep < sequence.size()) {
            goTextView.setText("GO!");
            inputEnabled = false;

            handler.postDelayed(() -> {
                goTextView.setText(""); // Clear the "GO!" message
                inputEnabled = true;   // Enable input for the current step
                movementDetected = false; // Reset movement detection for this step
            }, 1000); // 1-second delay before enabling input
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!inputEnabled || movementDetected) return; // Ignore input until enabled or if movement already detected

        float x = event.values[0];
        float y = event.values[1];

        int direction = determineDirection(x, y);

        if (direction != -1 && direction == sequence.get(currentStep)) {
            movementDetected = true; // Lock input until the next "GO!"
            currentStep++;
            if (currentStep < sequence.size()) {
                showGoForNextColor(); // Show "GO" for the next step
            } else {
                // All steps completed
                Toast.makeText(this, "Round Complete!", Toast.LENGTH_SHORT).show();
                handler.postDelayed(() -> {
                    Intent intent = new Intent(this, SequenceActivity.class);
                    startActivity(intent);
                    finish();
                }, 1000);
            }
        } else if (direction != -1) {
            // Incorrect tilt
            Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show();
            inputEnabled = false;
            handler.postDelayed(() -> {
                Intent intent = new Intent(this, GameOverActivity.class);
                intent.putExtra("score", currentStep);
                startActivity(intent);
                finish();
            }, 1000);
        }
    }

    /**
     * Determine the direction of tilt based on accelerometer values.
     * Ensures one axis dominates over the others and avoids detecting diagonal movements.
     */
    private int determineDirection(float x, float y) {
        if (Math.abs(x) > THRESHOLD && Math.abs(x) > Math.abs(y) * DOMINANCE_FACTOR) {
            if (x > 0) return 3; // Down (Yellow)
            else return 2;       // Up (Green)
        }

        if (Math.abs(y) > THRESHOLD && Math.abs(y) > Math.abs(x) * DOMINANCE_FACTOR) {
            if (y > 0) return 0; // Right (Red)
            else return 1;       // Left (Blue)
        }

        return -1; // No significant movement
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
