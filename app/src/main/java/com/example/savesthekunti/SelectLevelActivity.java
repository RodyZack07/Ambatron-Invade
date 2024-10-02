package com.example.savesthekunti;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

public class SelectLevelActivity extends AppCompatActivity {

    private VideoView backgroundVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hubungkan layout XML level_activity.xml dengan Activity ini
        setContentView(R.layout.level_activity);

        // Inisialisasi ImageButton prevButton setelah setContentView
        ImageButton prevButton = findViewById(R.id.prevsBtn1);

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Kembali ke Activity sebelumnya
            }
        });

        // Inisialisasi komponen VideoView
        backgroundVideo = findViewById(R.id.backgroundVideo);

        // Set path video dari folder raw
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.raw_gameplay);
        backgroundVideo.setVideoURI(videoUri);

        // Matikan volume dan looping otomatis
        backgroundVideo.setOnPreparedListener(mp -> {
            mp.setVolume(0f, 0f);  // Volume di-mute
            mp.setLooping(true);    // Set looping
        });

        // Mulai video
        backgroundVideo.start();

        // Inisialisasi komponen lain dari XML
        ImageView bgLevel1 = findViewById(R.id.bg_level_1_1);
        ImageView bgLevel2 = findViewById(R.id.bg_level_2_1);
        ImageView bgLevel3 = findViewById(R.id.bg_level_3_1);

        TextView tvLevel1 = findViewById(R.id.tv_level_1_2);
        TextView tvLevel2 = findViewById(R.id.tv_level_2_2);
        TextView tvLevel3 = findViewById(R.id.tv_level_3_2);

        // Mengatur listener untuk ImageButton bgLevel1
        bgLevel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gunakan Intent untuk berpindah ke GameplayActivity (ganti dengan Activity yang benar)
                Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
                startActivity(intent); // Memulai Activity baru
            }
        });

        // Jika kamu ingin menambahkan event untuk level lainnya
        bgLevel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectLevelActivity.this, GameActivity2.class);
                startActivity(intent);
            }
        });
//
//        bgLevel3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SelectLevelActivity.this, GameplayActivity3.class);
//                startActivity(intent);
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Melanjutkan video jika Activity kembali ke foreground
        backgroundVideo.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause video ketika Activity masuk ke background
        backgroundVideo.pause();
    }
}
