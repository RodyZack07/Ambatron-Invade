package com.example.savesthekunti.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class GameActivity extends AppCompatActivity implements GameView.OnPlayerHpChangeListener, GameView.OnBossHpChangeListener {
    private GameView gameView;
    private VideoView gameplayBg;
    private TextView scoreTxt, defeatedTxt;
    private ImageView explosionView;
    private Level levelData;
    private int initialHealth;
    private int maxBossHealth;
    private ImageView oneBarLeft, twoBarLeft, threeBarLeft, fourBarLeft, fiveBarLeft;
    private ImageView bossBar1, bossBar2, bossBar3, bossBar4, bossBar5, bossBar6, bossBar7, bossBar8, bossBar9, bossBar10;
    private Level currentLevelData;
    private ImageButton pausemenu;
    private LinearLayout bossHealthBar;
    private String username;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private MediaPlayer battleBgm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        currentLevelData = (Level) getIntent().getSerializableExtra("levelData");

        username = getIntent().getStringExtra("username");
        if (username == null || username.isEmpty()) {
            Log.e("GameActivity", "Username is null or empty. Cannot update Firestore.");
            return;
        }

        // Inisialisasi komponen UI
        explosionView = new ImageView(this);
        explosionView.setLayoutParams(new RelativeLayout.LayoutParams(300, 230)); // Atur ukuran sesuai kebutuhan
        explosionView.setVisibility(View.GONE);

//        ================================================= PAUSE ======================================================================================
        pausemenu = findViewById(R.id.pausebtn);
        pausemenu.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, PauseMenu.class);
            intent.putExtra("username", username); // Contoh pengiriman usern
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
        battleBgm = MediaPlayer.create(this, R.raw.battle_bgm);

        if (gameView.getBossAmba() != null) {
            bossHealthBar.setVisibility(View.VISIBLE);
        } else {
            bossHealthBar.setVisibility(View.GONE);
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


        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.level_bg); // Ganti dengan nama file video yang benar

        gameplayBg.setVideoURI(videoUri); // Menggunakan gameplayBg
        gameplayBg.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING); // Menyesuaikan skala video
            }
        });
        gameplayBg.start();
        battleBgm.setLooping(true);
        battleBgm.start();

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

    private int loadVolumePreference() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        return prefs.getInt("volume", 50); // default 50
    }

    private void setVolume(MediaPlayer mediaPlayer, float volume) {
        mediaPlayer.setVolume(volume, volume);
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
            battleBgm.release();
        }}

    @Override
    public void onBossHpChange(int newHp) {
        if (gameView.getBossAmba() != null) {
            Log.d("GameActivity", "Boss HP changed to: " + newHp);
            if (maxBossHealth == 0) {
                maxBossHealth = newHp;
                Log.d("GameActivity", "Max Boss HP set to: " + maxBossHealth);
            }
            if (bossHealthBar.getVisibility() == View.GONE) {
                bossHealthBar.setVisibility(View.INVISIBLE);
                bossHealthBar.postDelayed(() -> {
                    bossHealthBar.setVisibility(View.VISIBLE);
                    bossHealthBar.setAlpha(0f);
                    bossHealthBar.animate()
                            .alpha(1f)
                            .setDuration(500)
                            .setListener(null);
                }, 500); // Delay 500ms
            }

            updateBossBars(newHp);

            if (newHp <= 0) {
                showGameWin(findViewById(R.id.gameContent));
                updateLevelCompletionStatus(true);

                if (currentLevelData.getLevelNumber() >= 10 && currentLevelData.getLevelNumber() <= 15){
                    updatePlayerCurrency(3);}
                battleBgm.release();
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

        bossBar1.setVisibility(percentageHp >= 1 ? View.VISIBLE : View.GONE);
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
        intent.putExtra("selectedSkin", getIntent().getStringExtra("selectedSkin")); // Tambahkan data skin yang dipilih
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

    private void updateLevelCompletionStatus(boolean isWin) {
        String levelField = "isLevelCompleted" + currentLevelData.getLevelNumber();
        Map<String, Object> levelUpdate = new HashMap<>();
        levelUpdate.put(levelField, isWin); // true jika menang, false jika kalah

        firestore.collection("Akun").document(username).collection("Levels")
                .document(username)
                .update(levelUpdate)
                .addOnSuccessListener(aVoid -> {
                    Log.d("GameActivity", "Level completion status updated successfully for level " + currentLevelData.getLevelNumber());
                })
                .addOnFailureListener(e -> {
                    Log.e("GameActivity", "Failed to update level completion status: ", e);
                });
    }

    private void updatePlayerCurrency(int amount){
        firestore.collection("Akun").document(username).get().
                addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        Long currentKoin = documentSnapshot.getLong("currency");
                        if(currentKoin == null){
                            currentKoin = 0L;
                        }
                        Long updateCurrency = currentKoin + amount;

                        firestore.collection("Akun").document(username).
                                update("currency", updateCurrency).
                                addOnSuccessListener(aVoid -> {
                                    Log.d("GameActivity", "Currency added" + amount);
                        }).addOnFailureListener( e ->
                                        Log.d("GameActivity", "Currency failed to add"));
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (battleBgm != null) {
            battleBgm.start();
            setVolume(battleBgm, loadVolumePreference() / 100f);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (battleBgm != null) {
            try {
                if (battleBgm.isPlaying()) {
                    battleBgm.pause();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace(); // Untuk debugging
            }
        }
    }

    private void initializeBattleBgm() {
        if (battleBgm == null) {
            battleBgm = MediaPlayer.create(this, R.raw.battle_bgm); // Ganti `R.raw.battle_bgm` sesuai file Anda
            battleBgm.setLooping(true);
        }
    }

    @Override
    public void onBackPressed (){}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameView != null) {
            gameView.destroy();
            gameView = null; // Bebaskan memori
        }
        if (battleBgm != null) {
            battleBgm.release();
            battleBgm = null;
        }
    }
}

