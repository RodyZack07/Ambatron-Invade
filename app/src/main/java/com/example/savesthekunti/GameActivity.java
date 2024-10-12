    package com.example.savesthekunti;

    import android.net.Uri;
    import android.os.Bundle;
    import android.os.Handler;
    import android.view.MotionEvent;
    import android.view.View;
    import android.widget.Button;
    import android.widget.FrameLayout;
    import android.widget.ImageView;
    import android.widget.VideoView;

    import androidx.appcompat.app.AppCompatActivity;

    import java.util.ArrayList;

    public class GameActivity extends AppCompatActivity {
        private GameView gameView;
        VideoView gameplayBg;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            int selectedShipIndex = getIntent().getIntExtra("selectedShipIndex", 0);

            //INISIALISASI GameView
            gameView = new GameView(this, selectedShipIndex);

            setContentView(R.layout.game_activity);
            // Mengatur layout ke game_activity.xml

            gameplayBg = findViewById(R.id.videoView);

            Uri gameplayBgUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.raw_gameplay);
            gameplayBg.setVideoURI(gameplayBgUri);



        }
    }

