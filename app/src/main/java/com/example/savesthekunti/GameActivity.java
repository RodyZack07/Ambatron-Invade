    package com.example.savesthekunti;

    import android.net.Uri;
    import android.os.Bundle;
    import android.widget.TextView;
    import android.widget.VideoView;
    import androidx.appcompat.app.AppCompatActivity;


    public class GameActivity extends AppCompatActivity {
        private GameView gameView;
        VideoView gameplayBg;
        private TextView scoreTxt, defeatedTxt;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.game_activity);

            //INISIALISASI GameView
            gameView = findViewById(R.id.gameView);
            gameplayBg = findViewById(R.id.videoView);

            // Mengatur layout ke game_activity.xml

            gameplayBg.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.raw_gameplay));
            gameplayBg.setOnPreparedListener(mp ->{
                mp.setLooping(true);
                gameplayBg.start();
            });

            int selectedShipIndex = getIntent().getIntExtra("selectedShipIndex", 0);
            gameView.setSelectedShipIndex(selectedShipIndex);
        }
    }

