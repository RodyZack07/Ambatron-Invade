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

        setContentView(R.layout.level_activity);

        ImageButton prevButton = findViewById(R.id.prevsBtn1);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Kembali ke Activity sebelumnya dan menghancurkannya
            }
        });

        // Inisialisasi VideoView dan komponen lainnya
        backgroundVideo = findViewById(R.id.backgroundVideo);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.raw_gameplay);
        backgroundVideo.setVideoURI(videoUri);

        backgroundVideo.setOnPreparedListener(mp -> {
            mp.setVolume(0f, 0f);  // Volume di-mute
            mp.setLooping(true);    // Set looping
        });

        backgroundVideo.start();

        ImageView bgLevel1 = findViewById(R.id.bg_level_1_1);
        ImageView bgLevel2 = findViewById(R.id.bg_level_2_1);
        ImageView bgLevel3 = findViewById(R.id.bg_level_3_1);
        ImageView bgLevel4 = findViewById(R.id.bg_level_4_1);
        ImageView bgLevel5 = findViewById(R.id.bg_level_5_1);
        ImageView bgLevel6 = findViewById(R.id.bg_level_6_1);
        ImageView bgLevel7 = findViewById(R.id.bg_level_7_1);
        ImageView bgLevel8 = findViewById(R.id.bg_level_8_1);
        ImageView bgLevel9 = findViewById(R.id.bg_level_9_1);
        ImageView bgLevel10 = findViewById(R.id.bg_level_10_1);


        bgLevel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Beralih ke GameActivity dan menghancurkan SelectLevelActivity
                Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
                intent.putExtra("selectedShipIndex", getIntent().getIntExtra("selectedShipIndex", 0));
                startActivity(intent);
                finish(); // Menghancurkan SelectLevelActivity untuk menghemat memori
            }
        });

        bgLevel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Beralih ke GameActivity2 dan menghancurkan SelectLevelActivity
                Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
                startActivity(intent);
                finish(); // Menghancurkan SelectLevelActivity untuk menghemat memori
            }
        });

        // Uncomment jika diperlukan untuk level 3

        bgLevel3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
                startActivity(intent);
                finish(); // Menghancurkan SelectLevelActivity untuk menghemat memori
            }
        });

        bgLevel4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
                startActivity(intent);
                finish();
            }
        });

        bgLevel5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
                startActivity(intent);
                finish();
            }
        });

        bgLevel6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
                startActivity(intent);
                finish();
            }
        });

        bgLevel7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
                startActivity(intent);
                finish();
            }
        });

        bgLevel8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
                startActivity(intent);
                finish();
            }
        });

        bgLevel9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
                startActivity(intent);
                finish();
            }
        });

        bgLevel10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        backgroundVideo.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        backgroundVideo.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backgroundVideo != null) {
            backgroundVideo.stopPlayback(); // Menghentikan video playback
            backgroundVideo = null;        // Membersihkan referensi
        }
    }
}
