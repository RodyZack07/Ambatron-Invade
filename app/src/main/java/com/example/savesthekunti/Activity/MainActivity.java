package com.example.savesthekunti.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.window.SplashScreen;

import com.airbnb.lottie.LottieAnimationView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savesthekunti.Database.AccountCenter;
import com.example.savesthekunti.Database.Admin;
import com.example.savesthekunti.Database.Login;
import com.example.savesthekunti.Model.EditAsAdminActivity;
import com.example.savesthekunti.R;
import com.example.savesthekunti.UI.LoadingScreen;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

//    ======== Library ==============
private LottieAnimationView lottieLoading;


    private VideoView videoViewBackground;
    private PopupWindow popupWindow;
    private PopupWindow exitPopupWindow;
    private int videoPosition;
    private MediaPlayer mediaPlayer;
    private MediaPlayer buttonSFX;
    private TextView welcomeText;
    private FirebaseFirestore db;
    private String user;
    private ImageButton adminButton;
    private TextView warningText; // Declare warning TextView
    private ImageView borderText;
    private SharedPreferences sharedPreferences;
    private ImageButton infomenu;
    private  ImageButton akunbtn;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements



        welcomeText = findViewById(R.id.welcomeText);
        warningText = findViewById(R.id.warning);
        borderText = findViewById(R.id.borderText);
        adminButton = findViewById(R.id.Admin);
        infomenu = findViewById(R.id.info_button);
        buttonSFX = MediaPlayer.create(this, R.raw.button_sfx);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);


        // Check if user is logged in
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // Retrieve username and admin status from SharedPreferences
            user = sharedPreferences.getString("username", "");
            boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);
            String email = getIntent().getStringExtra("email"); // Retrieve the email

            // Update UI based on login status
            if (user != null) {
                welcomeText.setText("Selamat datang, " + user + "!");
                if (isAdmin) {
                    adminButton.setVisibility(View.VISIBLE);
                }
            }
        } else {
            // Display warning message if user is not logged in
            warningText.setVisibility(View.VISIBLE);
            borderText.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Anda Harus Login Untuk Bermain", Toast.LENGTH_SHORT).show();
        }

        // Initialize VideoView for background
        videoViewBackground = findViewById(R.id.videoViewBackground);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.background_loop);
        videoViewBackground.setVideoURI(videoUri);
        videoViewBackground.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0, 0); // Pastikan volume video benar-benar mati
        });
        videoViewBackground.start();

        // Initialize MediaPlayer for audio
        mediaPlayer = MediaPlayer.create(this, R.raw.galatic_idle);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // Initialize buttons
        ImageButton settingsBtn = findViewById(R.id.setting_button);
        ImageButton quitButton = findViewById(R.id.btn_quit);
        ImageButton playButton = findViewById(R.id.play_button);
        ImageButton profilMenu = findViewById(R.id.profile);
        ImageButton achievementMenu = findViewById(R.id.achievement);

        // Set onClickListener for each button
        settingsBtn.setOnClickListener(view -> showSettingsPopup(view));
        quitButton.setOnClickListener(view -> showExitPopup(view));
        playButton.setOnClickListener(v -> {
            directSelectFighter();
            finish();
        });
        profilMenu.setOnClickListener(view -> openLoginActivity());
        achievementMenu.setOnClickListener(view -> showAchievement());

        // Set onClickListener for Admin button
        adminButton.setOnClickListener(view -> openAdminActivity());

        // Hide Admin button by default
        adminButton.setVisibility(View.GONE);

        // Check if user is logged in and enable/disable Play button accordingly
        if (user == null) {
            playButton.setEnabled(false);
            borderText.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.GONE);
            warningText.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Anda Harus Login Untuk Bermain", Toast.LENGTH_SHORT).show();
        } else {
            playButton.setVisibility(View.VISIBLE);
            playButton.setOnClickListener(v -> directSelectFighter());
            getUserData(user);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPosition = videoViewBackground.getCurrentPosition();
        videoViewBackground.pause();

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoViewBackground.seekTo(videoPosition);
        videoViewBackground.start();

        if (mediaPlayer != null) {
            mediaPlayer.start();
            setVolume(mediaPlayer, loadVolumePreference() / 100f);
        }
    }

    private void getUserData(String userId) {
        if (isInternetAvailable()) {
            db.collection("Akun").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String username = document.getString("username");

                                welcomeText.setText("Selamat datang, " + username + "!");
                                getSkinData(userId);

                                Boolean isAdmin = document.getBoolean("isAdmin");
                                if (isAdmin != null && isAdmin) {
                                    adminButton.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Data pengguna tidak ditemukan.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(MainActivity.this, "Tidak ada koneksi internet.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getSkinData(String userId) {
        db.collection("Akun").document(userId).collection("Koleksi_Skin")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot result = task.getResult();
                        if (result != null && !result.isEmpty()) {
                            StringBuilder skins = new StringBuilder("");
                            for (QueryDocumentSnapshot document : result) {
                                String skinId = document.getString("id_skin");
                                Boolean isLocked = document.getBoolean("status_terkunci");

                                if (skinId != null) {
                                    skins.append("").append(skinId).append(" - ").append(isLocked ? "" : "").append("");
                                }
                            }
                            Toast.makeText(MainActivity.this, skins.toString(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Tidak ada skin yang ditemukan untuk pengguna ini.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setVolume(MediaPlayer mediaPlayer, float volume) {
        mediaPlayer.setVolume(volume, volume);
    }

    private void saveVolumePreference(int volume) {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("volume", volume);
        editor.apply();
    }

    private int loadVolumePreference() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        return prefs.getInt("volume", 50); // default 50
    }

    private void showSettingsPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);
        buttonSFX.start();

        int popupWidth = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int popupHeight = getResources().getDimensionPixelSize(R.dimen.popup_height);
        popupWindow = new PopupWindow(popupView, popupWidth, popupHeight, true);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        ImageButton closeBtn = popupView.findViewById(R.id.closebtn);
        closeBtn.setOnClickListener(view -> popupWindow.dismiss());

        SeekBar seekBarVol = popupView.findViewById(R.id.seekBarVol);
        seekBarVol.setMax(100);
        seekBarVol.setProgress(loadVolumePreference());
        setVolume(mediaPlayer, loadVolumePreference() / 100f);

        // Inisialisasi infomenu dari popupView
        ImageButton infomenu = popupView.findViewById(R.id.info_button);
        infomenu.setOnClickListener(view -> {
            Intent intent = new Intent(this, InfoMenuActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        // Inisialisasi akun dari popupView
        ImageButton akunbtn = popupView.findViewById(R.id.AkunCenter);
        akunbtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, AccountCenter.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        seekBarVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setVolume(mediaPlayer, progress / 100f);
                saveVolumePreference(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Tidak perlu diisi jika tidak diperlukan
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Tidak perlu diisi jika tidak diperlukan
            }
        });
    }


    private void showExitPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.exit_layout, null);
        buttonSFX.start();
        int popupWidth = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int popupHeight = getResources().getDimensionPixelSize(R.dimen.popup_height);

        exitPopupWindow = new PopupWindow(popupView, popupWidth, popupHeight, true);
        exitPopupWindow.setAnimationStyle(R.style.PopupAnimation);
        exitPopupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        ImageButton confirmExit = popupView.findViewById(R.id.imageExit);
        ImageButton cancelExit = popupView.findViewById(R.id.imageYes);

        confirmExit.setOnClickListener(v -> finishAffinity());
        cancelExit.setOnClickListener(v -> exitPopupWindow.dismiss());
    }

    private void openAdminActivity() {
        Intent intent = new Intent(this, EditAsAdminActivity.class);
        startActivity(intent);
    }

    private void directSelectFighter() {
//        // Mendapatkan referensi ke LottieAnimationView
//        LottieAnimationView lottieLoading = findViewById(R.id.lottieLoading);
//
//        // Memastikan animasi hanya dimainkan jika file animasi valid
//        if (lottieLoading != null) {
//            // Menampilkan animasi dan memulai pemutaran
//            lottieLoading.setVisibility(View.VISIBLE);
//            lottieLoading.playAnimation();
//
//            // Delay untuk menunjukkan animasi sebentar sebelum berpindah activity
//            new Handler().postDelayed(() -> {
                // Intent untuk berpindah ke SelectFighterActivity
                Intent intent = new Intent(MainActivity.this, SelectFighterActivity.class);
                intent.putExtra("username", "shinoa"); // Contoh pengiriman username
                startActivity(intent);

                // Tambahkan animasi fade transition jika diinginkan
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//
//                // Menghentikan animasi dan menyembunyikan animasi setelah berpindah activity
//                lottieLoading.cancelAnimation();
//                lottieLoading.setVisibility(View.GONE);
//                finish(); // Menutup MainActivity
//            }, 7000); // Delay 2 detik sebelum berpindah activity
//        }

    }





    private void openLoginActivity() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        buttonSFX.start();
    }

    private void showAchievement() {
        Intent intent = new Intent(this, ProfilActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Toast.makeText(this, "Menampilkan achievement...", Toast.LENGTH_SHORT).show();
        buttonSFX.start();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (buttonSFX != null) {
            buttonSFX.release();
            buttonSFX = null;
        }
    }
}
