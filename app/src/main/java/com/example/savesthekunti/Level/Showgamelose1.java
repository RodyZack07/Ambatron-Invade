package com.example.savesthekunti.Level;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
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

public class Showgamelose1 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_game_win1); // Sesuaikan dengan layout yang benar

        // Ambil data level dari Intent
        Level levelData = (Level) getIntent().getSerializableExtra("levelData");

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

        homeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Showgamelose1.this, SelectFighterActivity.class);
            intent.putExtra("username", "shinoa"); // Contoh pengiriman username
            startActivity(intent);
            // Tambahkan animasi fade transition jika diinginkan
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });


        nextBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Showgamelose1.this, GameActivity.class);
            intent.putExtra("levelIndex", 2); // Lanjutkan ke level berikutnya
            startActivity(intent);
            finish();
        });
    }
}
