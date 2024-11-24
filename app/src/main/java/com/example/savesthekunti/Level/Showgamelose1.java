package com.example.savesthekunti.Level;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
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
    private MediaPlayer loseSFX ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_game_lose1); // Sesuaikan dengan layout yang benar

        // Ambil data level dari Intent
        Level levelData = (Level) getIntent().getSerializableExtra("levelData");
        if (levelData != null) {
            Log.d("Showgamelose1", "Level data received: " + levelData.toString());
        } else {
            Log.e("Showgamelose1", "Level data is null");
        }


        String selectedSkin = getIntent().getStringExtra("selectedSkin");

        loseSFX = MediaPlayer.create(this, R.raw.game_lose);
        loseSFX.start();

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
                if (levelData != null) {
                    Log.d("Showgamelose1", "Level data received: " + levelData.toString());
                } else {
                    Log.e("Showgamelose1", "Level data is null");
                }

                finish();
            }
        });
    }
}
