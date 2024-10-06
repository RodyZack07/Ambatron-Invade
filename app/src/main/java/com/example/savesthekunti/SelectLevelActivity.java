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

        bgLevel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Beralih ke GameActivity dan menghancurkan SelectLevelActivity
                Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
                startActivity(intent);
                finish(); // Menghancurkan SelectLevelActivity untuk menghemat memori
            }
        });

        bgLevel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Beralih ke GameActivity2 dan menghancurkan SelectLevelActivity
                Intent intent = new Intent(SelectLevelActivity.this, GameActivity2.class);
                startActivity(intent);
                finish(); // Menghancurkan SelectLevelActivity untuk menghemat memori
            }
        });

        // Uncomment jika diperlukan untuk level 3
        /*
        bgLevel3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectLevelActivity.this, GameActivity3.class);
                startActivity(intent);
                finish(); // Menghancurkan SelectLevelActivity untuk menghemat memori
            }
        });
        */
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
