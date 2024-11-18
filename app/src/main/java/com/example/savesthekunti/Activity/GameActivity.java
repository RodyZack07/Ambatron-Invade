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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.os.Looper;
import android.animation.ObjectAnimator;
import android.view.animation.DecelerateInterpolator;

import com.example.savesthekunti.Level.Showgamelose1;
import com.example.savesthekunti.Level.Showgamewin1;
import com.example.savesthekunti.Model.Score;
import com.example.savesthekunti.R;


public class GameActivity extends AppCompatActivity implements GameView.OnPlayerHpChangeListener, GameView.OnBossHpChangeListener {
    private GameView gameView;
    private VideoView gameplayBg;
    private TextView scoreTxt, defeatedTxt;
    private ImageView explosionView;
    private Level levelData;
    public PopupWindow gameOverWindow;
    public PopupWindow gameWinWindow;
    private int initialHealth;
    private int initalBossHealth;
    private int maxBossHealth;
    private ImageView oneBarLeft, twoBarLeft, threeBarLeft, fourBarLeft, fiveBarLeft;
    private ImageView bossBar1, bossBar2, bossBar3, bossBar4, bossBar5, bossBar6, bossBar7, bossBar8, bossBar9, bossBar10;
    private Level currentLevelData;
    private ImageButton pausemenu;
    private int levelIndex;
    private LinearLayout bossHealthBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        currentLevelData = (Level) getIntent().getSerializableExtra("levelData");
        levelIndex = getIntent().getIntExtra("levelIndex", 0);

        // Inisialisasi komponen UI
        explosionView = new ImageView(this);
        explosionView.setLayoutParams(new RelativeLayout.LayoutParams(300, 230)); // Atur ukuran sesuai kebutuhan
        explosionView.setVisibility(View.GONE);  // Sembunyikan awalnya

//        ================================================= PAUSE ======================================================================================
        pausemenu = findViewById(R.id.pausebtn);
        pausemenu.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, PauseMenu.class);
            intent.putExtra("username", "shinoa"); // Contoh pengiriman usern
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);// ame

            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            startActivity(intent);
        });



        // Tambahkan explosionView ke layout
        ((ViewGroup) findViewById(R.id.gameContent)).addView(explosionView);

        gameView = findViewById(R.id.gameView);
        gameplayBg = findViewById(R.id.videoView); // Menggunakan gameplayBg
        scoreTxt = findViewById(R.id.scoreText);
        defeatedTxt = findViewById(R.id.MonsterDefeated);
        gameView.setOnPlayerHpChangeListener(this);
        gameView.setOnBossHpChangeListener(this);
        initialHealth = gameView.getPlayerShipHp();
        maxBossHealth = gameView.getBossAmbaMaxHp();
        bossHealthBar = findViewById(R.id.bossHealthBar);

        if (gameView.getBossAmba() != null) {
            bossHealthBar.setVisibility(View.VISIBLE); // Tampilkan jika BossAmba ada
        } else {
            bossHealthBar.setVisibility(View.GONE); // Sembunyikan jika BossAmba tidak ada
        }



        //HEALTH BAR
        oneBarLeft = findViewById(R.id.OneBarLeft);
        twoBarLeft = findViewById(R.id.TwoBarLeft);
        threeBarLeft = findViewById(R.id.ThreebarLeft);
        fourBarLeft = findViewById(R.id.FourbarLeft);
        fiveBarLeft = findViewById(R.id.FiveBarLeft);

        bossBar1 = findViewById(R.id.bossBar1);
        bossBar2 = findViewById(R.id.bossBar2);
        bossBar3 = findViewById(R.id.bossBar3);
        bossBar4 = findViewById(R.id.bossBar4);
        bossBar5 = findViewById(R.id.bossBar5);
        bossBar6 = findViewById(R.id.bossBar6);
        bossBar7 = findViewById(R.id.bossBar7);
        bossBar8 = findViewById(R.id.bossBar8);
        bossBar9 = findViewById(R.id.bossBar9);
        bossBar10 = findViewById(R.id.bossBar10);


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

        oneBarLeft.setVisibility(newHp >= 1 ? View.VISIBLE : View.GONE);
        twoBarLeft.setVisibility(newHp >= 200 ? View.VISIBLE : View.GONE);
        threeBarLeft.setVisibility(newHp >= 400 ? View.VISIBLE : View.GONE);
        fourBarLeft.setVisibility(newHp >= 600 ? View.VISIBLE : View.GONE);
        fiveBarLeft.setVisibility(newHp >= 800? View.VISIBLE : View.GONE);

        if (newHp <= 0) {
            showGameOver(findViewById(R.id.gameContent));
        }
    }

    @Override
    public void onBossHpChange(int newHp) {
        if (gameView.getBossAmba() != null) {
            Log.d("GameActivity", "Boss HP changed to: " + newHp);
            bossHealthBar.setVisibility(View.VISIBLE);
            if (maxBossHealth == 0) {
                maxBossHealth = newHp;
                Log.d("GameActivity", "Max Boss HP set to: " + maxBossHealth);
            }

            updateBossBars(newHp);

            if (newHp <= 0) {
                showGameWin(findViewById(R.id.gameContent));
            }
        } else {
            Log.d("GameActivity", "BossAmba belum terinisialisasi");
        }
    }

    private void updateBossBars(int currentHp) {
        if (maxBossHealth <= 0) {
            Log.e("GameActivity", "Boss max HP is zero. Skipping boss bar update.");
            return;
        }

        // Hitung persentase HP
        int percentageHp = (int) ((double) currentHp / maxBossHealth * 100);
        Log.d("GameActivity", "Current Boss HP: " + currentHp + ", Percentage: " + percentageHp + "%");

        bossBar1.setVisibility(percentageHp >= 10 ? View.VISIBLE : View.GONE);
        bossBar2.setVisibility(percentageHp >= 20 ? View.VISIBLE : View.GONE);
        bossBar3.setVisibility(percentageHp >= 30 ? View.VISIBLE : View.GONE);
        bossBar4.setVisibility(percentageHp >= 40 ? View.VISIBLE : View.GONE);
        bossBar5.setVisibility(percentageHp >= 50 ? View.VISIBLE : View.GONE);
        bossBar6.setVisibility(percentageHp >= 60 ? View.VISIBLE : View.GONE);
        bossBar7.setVisibility(percentageHp >= 70 ? View.VISIBLE : View.GONE);
        bossBar8.setVisibility(percentageHp >= 80 ? View.VISIBLE : View.GONE);
        bossBar9.setVisibility(percentageHp >= 90 ? View.VISIBLE : View.GONE);
        bossBar10.setVisibility(percentageHp >= 100 ? View.VISIBLE : View.GONE);
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

    public void showGameOver(View anchorView) {
        Intent intent = new Intent(GameActivity.this, Showgamelose1.class);
        intent.putExtra("levelData", currentLevelData);
        startActivity(intent);
        // Tambahkan animasi fade transition jika diinginkan
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finishAffinity(); // Menutup semua aktivitas di tumpukan yang sama
    }


    public void showGameWin(View anchorView){
        anchorView.postDelayed(() -> {
            gameView.destroy();
            Intent intent = new Intent(GameActivity.this, Showgamewin1.class);
            intent.putExtra("levelData", currentLevelData);
            intent.putExtra("selectedSkin", getIntent().getStringExtra("selectedSkin"));
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, 700);
    }

    @Override
    public void onBackPressed (){}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release any remaining resources
        if (gameView != null) {
            gameView.destroy();
        }
    }
}
