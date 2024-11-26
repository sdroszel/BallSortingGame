package com.example.ballsortinggame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button easyButton = findViewById(R.id.easy_button);
        Button hardButton = findViewById(R.id.hard_button);
        Button howToPlayButton = findViewById(R.id.how_button);

        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EasyGame.class);
                startActivity(intent);
            }
        });

        hardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HardGame.class);
                startActivity(intent);
            }
        });

        howToPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHowToPlayDialog();
            }
        });
    }

    private void showHowToPlayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("How to Play")
                .setMessage("The goal of the game is to sort all the colors into individual columns where each color matches." +
                        "\n\nYou can only move one element at a time, either into an empty column or a column with the same color element at the top.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss()) // Restart the game
                .setCancelable(false) // Prevent dismissing the dialog with back button
                .show();
    }
}