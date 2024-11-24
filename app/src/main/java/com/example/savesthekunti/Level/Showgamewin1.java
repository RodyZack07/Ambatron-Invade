package com.example.savesthekunti.Level;

import static androidx.core.content.ContextCompat.startActivity;

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
import com.example.savesthekunti.Activity.SelectLevelActivity;
import com.example.savesthekunti.R;
import com.example.savesthekunti.UI.LoadingScreenGame;

public class Showgamewin1 extends AppCompatActivity {
    private Level[] levels;
    private String selectedSkin;
    private MediaPlayer winSFX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_game_win1); // Sesuaikan dengan layout yang benar


        // Ambil data level dari Intent
        Level levelData = (Level) getIntent().getSerializableExtra("levelData");

        SelectLevelActivity selectLevelActivity = new SelectLevelActivity();
        levels = selectLevelActivity.getLevels();
        selectedSkin = getIntent().getStringExtra("selectedSkin");

        winSFX = MediaPlayer.create(this, R.raw.game_win);
        winSFX.start();

        if (levelData == null) {
            Log.e("Showgamewin1", "levelData is null");
            return;
        }


        // Tampilkan nomor level di TextView jika levelData tidak null
        if (levelData != null) {
            int levelNumber = levelData.getLevelNumber();

            TextView levelTextView = findViewById(R.id.levelnumberTextView);
            levelTextView.setText("Level " + levelNumber);
        } else {
            Log.e("Showgamelose1", "Level data is null");
        }

        ImageButton homeBtn = findViewById(R.id.homebtn);

        ImageButton nextBtn = findViewById(R.id.nextbtn);

//        SHARED PREFRENCE
        SharedPreferences sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);


        homeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Showgamewin1.this, SelectFighterActivity.class);
            intent.putExtra("username",username); // Contoh pengiriman username
            startActivity(intent);
            // Tambahkan animasi fade transition jika diinginkan
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });


        nextBtn.setOnClickListener(view -> {
            if (levelData == null) {
                Log.e("Showgamewin1", "levelData is null");
                return;
            }
            int nextLevelNumber = levelData.getLevelNumber() + 1;
            Level nextLevelData = getLevelByNumber(nextLevelNumber);



            if (nextLevelData != null) { // Pastikan level berikutnya ada
                Intent intent = new Intent(Showgamewin1.this, LoadingScreenGame.class);
                intent.putExtra("levelData", nextLevelData);
                intent.putExtra("selectedSkin", selectedSkin);// Kirim data level berikutnya
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }
    private Level getLevelByNumber(int levelNumber) {
        if (levels != null) {
            for (Level level : levels) {
                if (level.getLevelNumber() == levelNumber) {
                    return level;
                }
            }
        }
        return null;
    }
}
