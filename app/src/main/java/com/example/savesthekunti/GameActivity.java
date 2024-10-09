    package com.example.savesthekunti;

    import android.os.Bundle;
    import android.os.Handler;
    import android.view.MotionEvent;
    import android.view.View;
    import android.widget.Button;
    import android.widget.FrameLayout;
    import android.widget.ImageView;
    import androidx.appcompat.app.AppCompatActivity;

    import java.util.ArrayList;

    public class GameActivity extends AppCompatActivity {
        private GameView gameView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            int selectedShipIndex = getIntent().getIntExtra("selectedShipIndex", 0);

            //INISIALISASI GameView
            gameView = new GameView(this, selectedShipIndex);
            setContentView(gameView); // Mengatur layout ke game_activity.xml

        }
    }

