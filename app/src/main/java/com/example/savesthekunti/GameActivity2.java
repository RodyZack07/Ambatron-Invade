package com.example.savesthekunti;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class GameActivity2  extends AppCompatActivity {
    private VideoView backgroundVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hubungkan layout XML level_activity.xml dengan Activity ini
        setContentView(R.layout.game_activity2);


        // Inisialisasi komponen VideoView
        backgroundVideo = findViewById(R.id.background1);

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

    }
}
