package com.example.savesthekunti.Activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.os.Looper;

import com.example.savesthekunti.Model.Score;
import com.example.savesthekunti.R;


public class GameActivity extends AppCompatActivity implements GameView.OnPlayerHpChangeListener {
    private GameView gameView;
    private VideoView gameplayBg;
    private TextView scoreTxt, defeatedTxt;
    private ImageView explosionView;
    private Level levelData;
    public PopupWindow gameOverWindow;
    public PopupWindow gameWinWindow;
    private int initialHealth; private ImageView oneBarLeft, twoBarLeft, threeBarLeft, fourBarLeft, fiveBarLeft;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        // Inisialisasi komponen UI
        explosionView = new ImageView(this);
        explosionView.setLayoutParams(new RelativeLayout.LayoutParams(300, 230)); // Atur ukuran sesuai kebutuhan
        explosionView.setVisibility(View.GONE);  // Sembunyikan awalnya

        // Tambahkan explosionView ke layout
        ((ViewGroup) findViewById(R.id.gameContent)).addView(explosionView);

        gameView = findViewById(R.id.gameView);
        gameplayBg = findViewById(R.id.videoView); // Menggunakan gameplayBg
        scoreTxt = findViewById(R.id.scoreText);
        defeatedTxt = findViewById(R.id.MonsterDefeated);
        gameView.setOnPlayerHpChangeListener(this);
        initialHealth = gameView.getPlayerShipHp();

        oneBarLeft = findViewById(R.id.OneBarLeft);
        twoBarLeft = findViewById(R.id.TwoBarLeft);
        threeBarLeft = findViewById(R.id.ThreebarLeft);
        fourBarLeft = findViewById(R.id.FourbarLeft);
        fiveBarLeft = findViewById(R.id.FiveBarLeft);


        // Memuat video dari sumber yang diinginkan
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.raw_gameplay); // Ganti dengan nama file video yang benar

        gameplayBg.setVideoURI(videoUri); // Menggunakan gameplayBg
        gameplayBg.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING); // Menyesuaikan skala video
            }
        });
        gameplayBg.start();


        // ============================ FUNCTION SELECT PESAWAT =================================
        String selectedSkin = getIntent().getStringExtra("selectedSkin");
        gameView.setSelectedShipIndex(selectedSkin);

        levelData = (Level) getIntent().getSerializableExtra("levelData");
        gameView.setLevelData(levelData);
        // ============================ FUNCTION SCORE =================================
        gameView.setOnChangeScoreListener(new GameView.OnChangeScoreListener() {
            @Override
            public void onScoreChange(int score, int defeatedCount) {
                scoreTxt.setText("Score: " + score);
                defeatedTxt.setText("Monster Defeated: " + defeatedCount);
            }
        });
    }

    @Override
    public void onPlayerHpChange(int newHp) {
        initialHealth = newHp;
        Log.d("GameActivity", "Player HP changed to: " + newHp);// Update initialHealth
        // ...

        oneBarLeft.setVisibility(newHp >= 100 ? View.VISIBLE : View.GONE);
        twoBarLeft.setVisibility(newHp >= 200 ? View.VISIBLE : View.GONE);
        threeBarLeft.setVisibility(newHp >= 400 ? View.VISIBLE : View.GONE);
        fourBarLeft.setVisibility(newHp >= 600 ? View.VISIBLE : View.GONE);
        fiveBarLeft.setVisibility(newHp >= 800? View.VISIBLE : View.GONE);

        if (newHp <= 0) {
            showGameOver(findViewById(R.id.gameContent)); // Panggil popup game over
        }


    }

    private void onGameOver(int score) {
        int stars = calculateStars(score); // Calculate stars based on score

        // Send score and stars to Score.java for storage in Firestore
        Intent intent = new Intent(GameActivity.this, Score.class);
        intent.putExtra("score", score);
        intent.putExtra("stars", stars);
        startActivity(intent); // Move to Score Activity
    }


    private int calculateStars(int score) {
        if (score > 1000) {
            return 3; // 3 stars for scores over 1000
        } else if (score > 500) {
            return 2; // 2 stars for scores over 500
        } else {
            return 1; // 1 star for scores under 500
        }
    }


    public void triggerExplosion(float x, float y) {
        explosionView.setX(x);
        explosionView.setY(y - 50); // Menempatkan animasi di atas monster
        explosionView.setBackgroundResource(R.drawable.explosion_animation);
        explosionView.setVisibility(View.VISIBLE);

        AnimationDrawable explosionAnimation = (AnimationDrawable) explosionView.getBackground();
        explosionAnimation.start();

        explosionView.postDelayed(() -> explosionView.setVisibility(View.GONE),
                explosionAnimation.getNumberOfFrames() * 7);
    }

    public void showGameOver(View anchorView){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.game_over, null);

        ImageButton homeBtn = popupView.findViewById(R.id.homebtn);
        ImageButton replayBtn = popupView.findViewById(R.id.replayBtn);
        ImageButton nextBtn = popupView.findViewById(R.id.nextBtn);

        int popupWidth = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int popupHeight = getResources().getDimensionPixelSize(R.dimen.popup_height);

        gameOverWindow = new PopupWindow(popupView, popupWidth, popupHeight, true);
        gameOverWindow.setAnimationStyle(R.style.PopupAnimation);
        gameOverWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameActivity.this, SelectFighterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        replayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameOverWindow.dismiss();
                recreate();
            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                gameView.destroy();
            }
        }, 1000);
    }

    public void showGameWin(View anchorView){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.game_win, null);

        ImageButton homeBtn = popupView.findViewById(R.id.homebtn);
        ImageButton replayBtn = popupView.findViewById(R.id.replayBtn);
        ImageButton nextBtn = popupView.findViewById(R.id.nextBtn);

        int popupWidth = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int popupHeight = getResources().getDimensionPixelSize(R.dimen.popup_height);

        gameWinWindow = new PopupWindow(popupView, popupWidth, popupHeight, true);
        gameWinWindow.setAnimationStyle(R.style.PopupAnimation);
        gameWinWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameActivity.this, SelectFighterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        replayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameWinWindow.dismiss();
                recreate();
            }
        });


    }

    @Override
    public void onBackPressed (){

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release any remaining resources
        if (gameView != null) {
            gameView.destroy();
        }
    }
}
