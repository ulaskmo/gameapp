package com.example.randomsequencegame;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

// Activity to display the top 5 high scores
public class HighScoreActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper; // Helper for fetching scores

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        dbHelper = new DatabaseHelper(this);

        // Fetch top scores from the database
        ArrayList<String> highScores = dbHelper.fetchTopScores();

        // Populate the ListView with the top scores
        ListView highScoresListView = findViewById(R.id.highScoreList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, highScores);
        highScoresListView.setAdapter(adapter);
    }
}
