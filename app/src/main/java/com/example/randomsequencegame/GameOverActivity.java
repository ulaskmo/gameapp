package com.example.randomsequencegame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// This activity is triggered when the player loses the game
public class GameOverActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper; // Database helper for saving scores
    private int playerScore; // Score of the current game

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        dbHelper = new DatabaseHelper(this);
        playerScore = getIntent().getIntExtra("score", 0);

        // UI Elements
        TextView scoreDisplay = findViewById(R.id.scoreView);
        EditText playerNameInput = findViewById(R.id.nameInput);
        Button saveScoreButton = findViewById(R.id.saveButton);

        // Show the player's score
        scoreDisplay.setText("Final Score: " + playerScore);

        // Save button listener
        saveScoreButton.setOnClickListener(v -> {
            String playerName = playerNameInput.getText().toString().trim();
            if (!playerName.isEmpty()) {
                dbHelper.savePlayerScore(playerName, playerScore);
                Toast.makeText(this, "Your score has been saved!", Toast.LENGTH_SHORT).show();

                // Redirect to the high scores screen
                Intent intent = new Intent(GameOverActivity.this, HighScoreActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Please enter a valid name!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
