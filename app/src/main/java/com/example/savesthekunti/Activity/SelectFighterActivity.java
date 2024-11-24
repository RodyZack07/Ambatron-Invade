package com.example.savesthekunti.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.savesthekunti.R;
import com.example.savesthekunti.UI.LoadingScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.view.animation.AnimationUtils;
import java.util.ArrayList;

public class SelectFighterActivity extends AppCompatActivity {

    private VideoView videoBackground;
    private int videoPosition;
    private MediaPlayer buttonSFX;

    private ImageView spaceShip;
    private ImageView lockOverlay; // Overlay untuk gembok
    private String[] fighterIDs = {"blue_cosmos", "retro_sky", "wing_of_justice", "x56_core", "shak_magma",
    "liz_blue", "nusei_15"};
    private ArrayList<String> ownedSkins = new ArrayList<>(); // ArrayList untuk menyimpan skin yang dimiliki
    private int currentSkinIndex = 0;
    private Button unlockSkin;
    private int userCurrency;
    private String Username;
    private SharedPreferences sharedPreferences;

    // AUDIO
    private MediaPlayer mediaPlayer;

    // DATABASE
    private FirebaseFirestore firestore;
    private String username;
    private TextView currencyTextView; // Add TextView for currency display

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_fighter);

        username = getIntent().getStringExtra("username");

//        ambil currecy
        int userCurrency = getIntent().getIntExtra("currency", 0); // Ambil nilai currency yang diteruskan

        // Pastikan Firebase diinisialisasi sebelum digunakan
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }

        // Sekarang FirebaseFirestore bisa digunakan
        firestore = FirebaseFirestore.getInstance();

        // Ambil data uang pengguna dari Firestore
        getUserCurrency();

        videoBackground = findViewById(R.id.selectBg);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.skin_selector);
        videoBackground.setVideoURI(videoUri);
        videoBackground.setOnPreparedListener(mp -> mp.setLooping(true));
        videoBackground.start();

        // ====================================== Audio ======================================
        mediaPlayer = MediaPlayer.create(this, R.raw.skin_idle);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        ImageButton prevsBtn1 = findViewById(R.id.prevsBtn2);
        spaceShip = findViewById(R.id.ship_img);
        lockOverlay = findViewById(R.id.lockOverlay);
        ImageButton prevsBtn = findViewById(R.id.prevsBtn);
        ImageButton nextBtn = findViewById(R.id.nextBtn);
        ImageButton selectBtn = findViewById(R.id.selectBtn);

        buttonSFX = MediaPlayer.create(this, R.raw.button_sfx);

        // Inisialisasi tombol unlockSkin
        unlockSkin = findViewById(R.id.skinUnlock); // Pastikan ID ini ada di layout XML

        // Inisialisasi TextView untuk menampilkan mata uang
        currencyTextView = findViewById(R.id.currencyTextView);



        String koleksiSkin = username != null ? username : "default_user"; // Menggunakan username sebagai ID koleksi skin

        firestore = FirebaseFirestore.getInstance();

        fetchUserSkins();

        prevsBtn.setOnClickListener(v -> prevsFighter());
        nextBtn.setOnClickListener(v -> nextFighter());
        selectBtn.setOnClickListener(v -> {
            selectGame();
            finish();
        });
        prevsBtn1.setOnClickListener(view -> prevsbutton());

        unlockSkin.setOnClickListener(v -> {
            unlockSkin.setEnabled(false); // Disable the button while unlocking
            unlockCurrentSkin();
        });
    }

    // ======================= LOAD VOLUME AND SET VOLUME ===================================
    private int loadVolumePreference() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        return prefs.getInt("volume", 50); // default 50
    }

    private void setVolume(MediaPlayer mediaPlayer, float volume) {
        mediaPlayer.setVolume(volume, volume);
    }

    private void prevsbutton() {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void getUserCurrency() {
        if (username == null || username.isEmpty()) {
            Log.d("SelectFighterActivity", "Username is null or empty.");
            return;
        }

        if (firestore == null) {
            Log.d("SelectFighterActivity", "Firestore instance is null.");
            return;
        }

        DocumentReference userRef = firestore.collection("Akun").document(username);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Mengambil nilai currency dari Firestore
                Long currencyLong = documentSnapshot.getLong("currency");
                if (currencyLong != null) {
                    userCurrency = currencyLong.intValue();  // Mengubah nilai currency menjadi integer
                    Log.d("SelectFighterActivity", "User currency: " + userCurrency); // Debug log

                } else {
                    Log.d("SelectFighterActivity", "Currency not found in user data.");
                }
            } else {
                Log.d("SelectFighterActivity", "User data not found.");
            }
        }).addOnFailureListener(e -> {
            Log.d("SelectFighterActivity", "Error getting user data: ", e);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoBackground.seekTo(videoPosition);
        videoBackground.start();

        if (mediaPlayer != null) {
            mediaPlayer.start();
            setVolume(mediaPlayer, loadVolumePreference() / 100f);
        }
        fetchUserSkins();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPosition = videoBackground.getCurrentPosition();
        videoBackground.pause();

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void fetchUserSkins() {
        if (username == null || username.isEmpty()) {
            Log.e("FetchUserSkins", "Username is null or empty");
            return;
        }

        CollectionReference skinRef = firestore.collection("Akun").document(username).collection("Koleksi_Skin");
        skinRef.get().addOnCompleteListener(task -> {
            ownedSkins.clear();
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String skinId = document.getString("id_skin");
                    Boolean isLocked = document.getBoolean("status_terkunci");
                    Boolean isUnlocked = document.getBoolean("is_unlocked");

                    if (skinId != null && isLocked != null && isUnlocked != null) {
                        if (!isLocked || isUnlocked) {
                            ownedSkins.add(skinId); // Menambahkan skin yang sudah dibuka
                        }}}
            } else {
                Log.d("FetchUserSkins", "Error getting documents: ", task.getException());
            }
            updateFighterView(); // Perbarui tampilan berdasarkan skin yang dimiliki
        });
    }


    private void updateFighterView() {
        String currentSkinID = fighterIDs[currentSkinIndex]; // Ambil ID skin saat ini
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.lock_fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.lock_fade_out);

        int skinDrawableId = getResources().getIdentifier(currentSkinID, "drawable", getPackageName());
        spaceShip.setImageResource(skinDrawableId);

        DocumentReference skinRef = firestore.collection("Akun").document(username)
                .collection("Koleksi_Skin").document(currentSkinID);
        skinRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Boolean isUnlocked = document.getBoolean("is_unlocked");
                    Boolean isLocked = document.getBoolean("status_terkunci");

                    if (isUnlocked != null && isUnlocked) {
                        lockOverlay.setVisibility(View.GONE); // Sembunyikan gembok
                        lockOverlay.startAnimation(fadeOut); // Jalankan animasi
                    } else if (isLocked != null && isLocked) {
                        lockOverlay.setVisibility(View.VISIBLE); // Tampilkan gembok
                        lockOverlay.startAnimation(fadeIn); // Jalankan animasi
                    }
                } else {
                    lockOverlay.setVisibility(View.VISIBLE); // Tampilkan gembok jika data tidak ditemukan

                }
            } else {
                lockOverlay.setVisibility(View.VISIBLE); // Tampilkan gembok jika ada error


            }
        });
    }


    private void nextFighter() {
        if (fighterIDs.length > 0) {
            currentSkinIndex = (currentSkinIndex + 1) % fighterIDs.length;
            FighterSwitchAnimation();
        }
    }

    private void prevsFighter() {
        if (fighterIDs.length > 0) {
            currentSkinIndex = (currentSkinIndex - 1 + fighterIDs.length) % fighterIDs.length;
            FighterSwitchAnimation();
        }
    }

    private void selectGame() {
        if (ownedSkins.contains(fighterIDs[currentSkinIndex])) {
            Intent intent = new Intent(SelectFighterActivity.this, LoadingScreen.class);
            intent.putExtra("selectedSkin", fighterIDs[currentSkinIndex]); // Kirim ID skin yang dipilih
            intent.putExtra("username", username);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            Toast.makeText(this, "Skin ini terkunci!", Toast.LENGTH_SHORT).show();
        }
        buttonSFX.start();
    }


    private void FighterSwitchAnimation() {
        AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(200);

        AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(200);

        spaceShip.startAnimation(fadeOut);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                updateFighterView();
                spaceShip.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void unlockCurrentSkin() {
        Log.d("UnlockSkin", "Memulai proses unlock skin");
        DocumentReference skinRef = firestore.collection("Akun").document(username)
                .collection("Koleksi_Skin").document(fighterIDs[currentSkinIndex]);

        DocumentReference userRef = firestore.collection("Akun").document(username);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long currencyLong = documentSnapshot.getLong("currency");
                if (currencyLong != null) {
                    int userCurrency = currencyLong.intValue();
                    if (userCurrency >= 10) {
                        skinRef.update("status_terkunci", false, "is_unlocked", true)
                                .addOnSuccessListener(aVoid -> {
                                    int updatedCurrency = userCurrency - 10;
                                    userRef.update("currency", updatedCurrency)
                                            .addOnSuccessListener(aVoid1 -> {
                                                fetchUserSkins();
                                                Toast.makeText(this, "Skin berhasil dibuka!", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> Log.e("UnlockSkin", "Gagal mengupdate currency", e));
                                })
                                .addOnFailureListener(e -> Log.e("UnlockSkin", "Gagal mengupdate status skin", e));
                    } else {
                        Toast.makeText(this, "Kointron tidak cukup", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(e -> Log.e("UnlockSkin", "Gagal mendapatkan data user", e));
    }








}
