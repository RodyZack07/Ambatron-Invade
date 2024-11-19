package com.example.savesthekunti.UI;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.savesthekunti.Activity.GameActivity;
import com.example.savesthekunti.Activity.Level;
import com.example.savesthekunti.R;

public class LoadingScreenGame2 extends AppCompatActivity {
    private String selectedSkin;
    private Level levelData;
    private String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);

        // Ambil data skin yang dipilih, level, dan username dari Intent
        selectedSkin = getIntent().getStringExtra("selectedSkin");
        levelData = (Level) getIntent().getSerializableExtra("levelData");
        username = getIntent().getStringExtra("username");

        // Mengatur background abu-abu untuk FrameLayout
        FrameLayout mainLayout = findViewById(R.id.main);
        mainLayout.setBackgroundColor(Color.parseColor("#000000")); // Warna abu-abu muda

        // Durasi splash screen dalam milidetik
        int splashScreenDuration = 2500;

        // Menggunakan Handler untuk menunda perpindahan ke GameActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Pindah ke GameActivity dan kirim data level, skin, dan username
                Intent intent = new Intent(LoadingScreenGame2.this, GameActivity.class);
                intent.putExtra("selectedSkin", selectedSkin);
                intent.putExtra("levelData", levelData); // Mengirim level yang dipilih
                intent.putExtra("username", username);  // Mengirim username pengguna
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(intent);
                finish(); // Tutup activity ini agar tidak bisa kembali ke splash screen
            }
        }, splashScreenDuration);
    }
}
