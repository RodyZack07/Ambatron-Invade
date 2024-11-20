package com.example.savesthekunti.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.savesthekunti.Activity.GameActivity;
import com.example.savesthekunti.Activity.Level;
import com.example.savesthekunti.R;

public class LoadingScreenGame extends AppCompatActivity {
    private String selectedSkin;
    private Level levelData;

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);


        /// Ambil data skin yang dipilih, level, dan username dari Intent
        selectedSkin = getIntent().getStringExtra("selectedSkin");
        levelData = (Level) getIntent().getSerializableExtra("levelData");
        String username = sharedPreferences.getString("username", null);

        // Validasi data Intent dan SharedPreferences
        if (selectedSkin == null || levelData == null || username == null) {
            Log.e("LoadingScreenGame", "Intent data or username is null. Cannot proceed.");
            // Tambahkan logika untuk menangani kesalahan, seperti kembali ke layar sebelumnya
            finish();
            return;
        }


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
                Intent intent = new Intent(LoadingScreenGame.this, GameActivity.class);
                intent.putExtra("selectedSkin", selectedSkin);
                intent.putExtra("levelData", levelData); // Mengirim level yang dipilih
                intent.putExtra("username", username);  // Mengirim username pengguna

                // Error handling for username
                if (username != null) {
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    startActivity(intent);
                    finish(); // Tutup activity ini agar tidak bisa kembali ke splash screen
                } else {
                    Log.e("LoadingScreenGame", "Username is null. Cannot start GameActivity.");
                    // Handle the case where username is null, e.g., show an error message
                }
            }

        }, splashScreenDuration);
    }
}
