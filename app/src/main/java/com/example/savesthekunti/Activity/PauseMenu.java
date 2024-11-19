package com.example.savesthekunti.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savesthekunti.R;

public class PauseMenu extends AppCompatActivity {

    private Button resumeButton, quitButton, retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameactivity_pause); // Sesuaikan dengan layout yang benar

        // Inisialisasi tombol-tombol
        resumeButton = findViewById(R.id.resumeButton);
        quitButton = findViewById(R.id.quitButton);
        retryButton = findViewById(R.id.retryButton);

        // Menangani klik tombol resume
        resumeButton.setOnClickListener(v -> {
            Intent intent = new Intent(PauseMenu.this, GameActivity.class);
            intent.putExtra("levelData", getIntent().getSerializableExtra("levelData")); // Mengirim data level yang sama
            intent.putExtra("username", getIntent().getStringExtra("username")); // Mengirim username
            startActivity(intent); // Kembali ke GameActivity
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish(); // Menutup PauseMenu
        });

        // Menangani klik tombol retry
        retryButton.setOnClickListener(v -> {
            Intent intent = new Intent(PauseMenu.this, GameActivity.class);
            intent.putExtra("levelData", getIntent().getSerializableExtra("levelData")); // Mengulang level yang sama
            intent.putExtra("username", getIntent().getStringExtra("username"));
            startActivity(intent); // Mulai ulang permainan dari awal
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish(); // Menutup PauseMenu
        });

        // Menangani klik tombol quit
        quitButton.setOnClickListener(v -> {
            Intent intent = new Intent(PauseMenu.this, MainActivity.class); // Ganti dengan aktivitas utama
            startActivity(intent); // Kembali ke layar utama
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finishAffinity(); // Menutup PauseMenu
        });
    }
}
