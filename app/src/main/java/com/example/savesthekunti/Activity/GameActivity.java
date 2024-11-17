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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class GameActivity extends AppCompatActivity implements GameView.OnPlayerHpChangeListener, GameView.OnBossHpChangeListener {
    private GameView gameView;
    private VideoView gameplayBg;
    private TextView scoreTxt, defeatedTxt;
    private ImageView explosionView;
    private Level levelData;
    public PopupWindow gameOverWindow;
    public PopupWindow gameWinWindow;
    private ProgressBar bossHealthBar;
    private int initialHealth;
    private int initalBossHealth;
    private ImageView oneBarLeft, twoBarLeft, threeBarLeft, fourBarLeft, fiveBarLeft;
    private Level currentLevelData;
    private ImageButton pausemenu;
    private String Username;


//    score
    private FirebaseFirestore db; // Instance Firestore
    private int Score;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);



        Username = getIntent().getStringExtra("username");


//        Database
        db = FirebaseFirestore.getInstance();

        currentLevelData = (Level) getIntent().getSerializableExtra("levelData");

        // Inisialisasi komponen UI
        explosionView = new ImageView(this);
        explosionView.setLayoutParams(new RelativeLayout.LayoutParams(300, 230)); // Atur ukuran sesuai kebutuhan
        explosionView.setVisibility(View.GONE);  // Sembunyikan awalnya

//        ================================================= PAUSE ======================================================================================
        pausemenu = findViewById(R.id.pausebtn);
        pausemenu.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, PauseMenu.class);
            intent.putExtra("username", Username);

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
        if (gameView.getBossAmba() != null) {
            initalBossHealth = gameView.getBossAmbaHp();
        } else {
            Log.e("GameActivity", "BossAmba is still null!");
            initalBossHealth = 0;  // Set default value
        }

        oneBarLeft = findViewById(R.id.OneBarLeft);
        twoBarLeft = findViewById(R.id.TwoBarLeft);
        threeBarLeft = findViewById(R.id.ThreebarLeft);
        fourBarLeft = findViewById(R.id.FourbarLeft);
        fiveBarLeft = findViewById(R.id.FiveBarLeft);

        bossHealthBar = findViewById(R.id.bossHealthBar);

        int bossMaxHp = gameView.getBossAmbaHp();  // Ambil nilai HP awal dari gameView
        bossHealthBar.setMax(bossMaxHp);           // Atur nilai max ProgressBar
        bossHealthBar.setProgress(bossMaxHp);      // Set progress sesuai HP saat ini


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
            showGameOver(findViewById(R.id.gameContent));
        }
    }

    @Override
    public void onBossHpChange(int newHp) {
        if (gameView.getBossAmba() != null) {
            initalBossHealth = newHp;

            Log.d("GameActivity", "Boss HP changed to: " + newHp);
            updateBossHealthBar(newHp);

            if (newHp <= 0) {
                showGameWin(findViewById(R.id.gameContent));
            }
        } else {
            Log.d("GameActivity", "BossAmba belum terinisialisasi");
        }
    }

    private void updateBossHealthBar(int newHp) {
        if (newHp >= 0 && initalBossHealth > 0) {
            float healthPercentage = (float) newHp / (float) initalBossHealth;

            if (Float.isNaN(healthPercentage)) {
                healthPercentage = 0f;
            }
            ObjectAnimator healthBarAnimation = ObjectAnimator.ofFloat(bossHealthBar, "scaleX", healthPercentage);
            healthBarAnimation.setDuration(500); // Animation duration
            healthBarAnimation.start();}
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


    private void saveScoreToFirestore(String level, int score, int stars) {
        // Ambil username dari SharedPreferences (atau sumber lain jika berbeda)
        SharedPreferences sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        if (username == null || username.isEmpty()) {
            Log.e("GameActivity", "Username tidak ditemukan dalam sesi pengguna");
            return;
        }

        if (level == null || level.isEmpty()) {
            Log.e("GameActivity", "Level tidak valid untuk menyimpan skor");
            return;
        }

        if (score < 0 || stars < 0 || stars > 3) { // Validasi tambahan untuk score dan stars
            Log.e("GameActivity", "Score atau stars tidak valid");
            return;
        }

        if (db != null) {
            // Cari user berdasarkan username
            db.collection("Akun")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Ambil userId dari hasil query
                            String userId = queryDocumentSnapshots.getDocuments().get(0).getId();
                            Map<String, Object> scoreData = new HashMap<>();
                            scoreData.put("score", score);
                            scoreData.put("stars", stars);
                            scoreData.put("level", level);
                            scoreData.put("updated_at", FieldValue.serverTimestamp()); // Use server timestamp

                            // Store the score in the "Score" collection under the user's document
                            db.collection("Akun").document(userId)
                                    .collection("Score").document(level)
                                    .set(scoreData)
                                    .addOnSuccessListener(aVoid -> Log.d("GameActivity", "Score berhasil disimpan untuk level: " + level))
                                    .addOnFailureListener(e -> Log.e("GameActivity", "Gagal menyimpan skor: ", e));
                        } else {
                            Log.e("GameActivity", "Username tidak ditemukan di Firestore: " + username);
                        }
                    })
                    .addOnFailureListener(e -> Log.e("GameActivity", "Gagal mengambil data akun dari Firestore: ", e));
        } else {
            Log.e("GameActivity", "Firestore instance tidak diinisialisasi");
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
        int score = gameView.getScore(); // Ambil skor dari GameView
        int stars = calculateStars(score); // Hitung jumlah bintang
        SharedPreferences sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        if (username == null) {
            username = "default"; // Gunakan default jika username tidak ditemukan
        }

        saveScoreToFirestore(username, score, stars);




        Intent intent = new Intent(GameActivity.this, Showgamelose1.class);
        intent.putExtra("score", score); // Kirim skor ke activity berikutnya
        intent.putExtra("stars", stars); // **Corrected: stars instead of starts**
        intent.putExtra("levelData", currentLevelData);
        startActivity(intent);

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finishAffinity(); // Menutup semua aktivitas di tumpukan yang sama
    }





    public void showGameWin(View anchorView) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        Level levelData = (Level) getIntent().getSerializableExtra("levelData");
        String username = sharedPreferences.getString("username", null);
        int score = gameView.getScore(); // Ambil skor dari GameView
        int stars = calculateStars(score); // Hitung jumlah bintang
        // Fetch the level from SharedPreferences
        int level = sharedPreferences.getInt("level", 1); // Default to level 1 if not found

        if (username == null) {
            username = "default"; // Gunakan default jika username tidak ditemukan
        }

        saveScoreToFirestore(username, score, stars);

        Intent intent = new Intent(GameActivity.this, Showgamewin1.class);
        intent.putExtra("score", score > 0 ? score : 0);
        intent.putExtra("stars", stars >= 0 && stars <= 3 ? stars : 1);
        intent.putExtra("levelData", currentLevelData);
        startActivity(intent);

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finishAffinity(); // Menutup semua aktivitas di tumpukan yang sama
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
