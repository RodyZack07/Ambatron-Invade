package com.example.savesthekunti.Level;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savesthekunti.Activity.GameActivity;
import com.example.savesthekunti.Activity.Level;
import com.example.savesthekunti.Activity.SelectFighterActivity;
import com.example.savesthekunti.R;

public class Showgamelose1 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_game_lose1); // Sesuaikan dengan layout yang benar

        // Ambil data level dari Intent
        Level levelData = (Level) getIntent().getSerializableExtra("levelData");
        String selectedSkin = getIntent().getStringExtra("selectedSkin");

        // Tampilkan nomor level di TextView jika levelData tidak null
        if (levelData != null) {
            int levelNumber = levelData.getLevelNumber();

            TextView levelTextView = findViewById(R.id.levelnumberTextView);
            levelTextView.setText("Level " + levelNumber);
        } else {
            Log.e("Showgamelose1", "Level data is null");
        }

        ImageButton homeBtn = findViewById(R.id.homebtn);
        ImageButton retryBtn = findViewById(R.id.retryBtn);

        // Ambil username dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        // Navigasi ke SelectFighterActivity
        homeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Showgamelose1.this, SelectFighterActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        // Fungsi Retry untuk mengulangi level
        retryBtn.setOnClickListener(view -> {
            if (levelData != null) {
                Intent intent = new Intent(Showgamelose1.this, GameActivity.class);
                intent.putExtra("levelData", levelData);
                intent.putExtra("selectedSkin", selectedSkin);
                startActivity(intent);

                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }
}
