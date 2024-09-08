package com.example.savesthekunti;

import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private FrameLayout asteroidContainer;
    private Handler asteroidHandler = new Handler();
    private int delay = 50; // 0.05 detik dalam milidetik
    private int asteroidSizeStart = 30; // Ukuran awal asteroid
    private int asteroidSizeEnd = 150;  // Ukuran akhir asteroid (ketika mendekati pemain)
    private int screenHeight; // Tinggi layar untuk menentukan batas pergerakan asteroid

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        asteroidContainer = findViewById(R.id.asteroidContainer);

        // Dapatkan tinggi layar untuk membatasi pergerakan asteroid
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Memulai looping untuk menambahkan asteroid setiap 0.05 detik
        asteroidHandler.postDelayed(asteroidRunnable, delay);
    }

    private Runnable asteroidRunnable = new Runnable() {
        @Override
        public void run() {
            spawnAsteroid();
            asteroidHandler.postDelayed(this, delay); // Memanggil lagi setelah 0.05 detik
        }
    };

    private void spawnAsteroid() {
        // Membuat ImageView baru untuk asteroid
        ImageView asteroid = new ImageView(this);
        asteroid.setImageResource(R.drawable.asteroid_left); // Menggunakan gambar asteroid

        // Tentukan ukuran awal asteroid
        int size = asteroidSizeStart;
        asteroid.setLayoutParams(new FrameLayout.LayoutParams(size, size));

        // Tentukan posisi awal asteroid (sesuai dengan posisi asteroid terkecil di XML)
        asteroid.setY(0); // Mulai dari atas layar
        asteroid.setX(100); // Sesuaikan dengan posisi X yang diinginkan (contohnya 100)

        // Tambahkan asteroid ke container
        asteroidContainer.addView(asteroid);

        // Animasi pergerakan asteroid (bergerak ke bawah dan membesar)
        asteroid.animate()
                .translationY(screenHeight) // Bergerak sampai ke bawah layar
                .scaleX(asteroidSizeEnd / (float) asteroidSizeStart) // Membesar secara horizontal
                .scaleY(asteroidSizeEnd / (float) asteroidSizeStart) // Membesar secara vertikal
                .setDuration(3000) // Durasi pergerakan asteroid (3 detik untuk contoh ini)
                .withEndAction(() -> asteroidContainer.removeView(asteroid)) // Hapus asteroid saat keluar dari layar
                .start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asteroidHandler.removeCallbacks(asteroidRunnable); // Hentikan handler saat aktivitas dihancurkan
    }
}

