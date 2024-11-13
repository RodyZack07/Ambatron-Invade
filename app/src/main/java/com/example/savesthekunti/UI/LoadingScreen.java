package com.example.savesthekunti.UI;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.savesthekunti.Activity.SelectFighterActivity;
import com.example.savesthekunti.Activity.SelectLevelActivity;
import com.example.savesthekunti.R;

public class LoadingScreen extends AppCompatActivity {
    private String[] fighterIDs = {"blue_cosmos", "retro_sky", "wing_of_justice"};
    private int currentSkinIndex = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);

        // Mengatur background abu-abu untuk FrameLayout
        FrameLayout mainLayout = findViewById(R.id.main);
        mainLayout.setBackgroundColor(Color.parseColor("#000000")); // Warna abu-abu muda

        // Durasi splash screen dalam milidetik
        int splashScreenDuration = 2500;

        // Menggunakan Handler untuk menunda perpindahan ke SelectYourFighter.java
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Pindah ke SelectYourFighter
                Intent intent = new Intent(LoadingScreen.this, SelectLevelActivity.class);
                intent.putExtra("selectedSkin", fighterIDs[currentSkinIndex]);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(intent);
                finish(); // Tutup activity ini agar tidak bisa kembali ke splash screen
            }
        }, splashScreenDuration);
    }
}
