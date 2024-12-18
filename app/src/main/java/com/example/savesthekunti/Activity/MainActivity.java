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
import android.util.Log;
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
import com.google.firebase.firestore.DocumentReference;
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
    private TextView warningText,currencyTextView;; // Declare warning TextView
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

        // Inisialisasi TextView
        currencyTextView = findViewById(R.id.currencyTextView);


/// ============================= AMBIL DATA DARI LOGIN =====================================
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // Retrieve username and admin status from SharedPreferences
            user = sharedPreferences.getString("username", "id_user");
            boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);
            String email = getIntent().getStringExtra("email"); // Retrieve the email

            // Update UI based on login status
            if (user != null) {
                welcomeText.setText(user);

                // Fetch and display currency data
                getCurrencyData(user); // Call the function to get currency

                if (isAdmin) {
                    adminButton.setVisibility(View.VISIBLE);
                }
            }
        } else {
            // Display warning message if user is not logged in
            warningText.setVisibility(View.VISIBLE);
            borderText.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Silahkan Regist dan Login untuk memainkan Ambatron Invades", Toast.LENGTH_SHORT).show();
        }


        // Initialize VideoView for background
        videoViewBackground = findViewById(R.id.videoViewBackground);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.manu_bg);
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
            Toast.makeText(this, "Silahkan Regist dan Login untuk memainkan Ambatron Invades", Toast.LENGTH_SHORT).show();
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


//   =========================== INISIALISASI TEXT VIEW MATA UANG =============================

    // Fungsi untuk mengambil data currency dari Firestore
    private void getCurrencyData(String username) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Menemukan dokumen berdasarkan username
        db.collection("Akun")
                .whereEqualTo("username", username) // Menyesuaikan dengan username yang login
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Ambil nilai currency dari Firestore
                            Long currencyLong = document.getLong("currency");
                            if (currencyLong != null) {
                                int userCurrency = currencyLong.intValue();  // Mengubah nilai currency menjadi integer
                                Log.d("SelectFighterActivity", "User currency: " + userCurrency); // Debug log
                                currencyTextView.setText(String.valueOf(userCurrency)); // Update display currency
                            } else {
                                Log.d("SelectFighterActivity", "Currency not found in user data.");
                            }
                        }
                    } else {
                        Log.d("SelectFighterActivity", "Error getting documents: ", task.getException());
                    }
                });
    }


    private void getUserData(String userId) {
        if (isInternetAvailable()) {
            db.collection("Akun").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String username = document.getString("username");

                                welcomeText.setText(username);
                                getSkinData(userId);

                                Boolean isAdmin = document.getBoolean("isAdmin");
                                if (isAdmin != null && isAdmin) {
                                    adminButton.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Sepertinya anda belum login", Toast.LENGTH_SHORT).show();
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
                                    skins.append(skinId).append(" - ").append(isLocked ? "Locked" : "Unlocked").append("\n");
                                }
                            }

                        } else {
                            // Tidak ada data ditemukan, tampilkan pesan di UI atau log
                            Log.d("SkinData", "Tidak ada skin yang ditemukan untuk pengguna ini.");
                        }
                    } else {
                        // Error saat mengambil data, tampilkan pesan di log
                        Log.e("SkinData", "Error getting documents: ", task.getException());
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

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void directSelectFighter() {
        // Ambil username dan currency dari SharedPreferences
        String username = sharedPreferences.getString("username", null);
        int userCurrency = sharedPreferences.getInt("currency", 0); // Ambil currency dari SharedPreferences (atau dari Firestore)

        // Pastikan username tidak null sebelum melanjutkan
        if (username != null) {
            // Buat intent ke SelectFighterActivity
            Intent intent = new Intent(MainActivity.this, SelectFighterActivity.class);

            // Tambahkan username dan currency ke dalam intent
            intent.putExtra("username", username);
            intent.putExtra("currency", userCurrency); // Kirimkan currency

            // Mulai aktivitas SelectFighterActivity
            startActivity(intent);

            // Tambahkan animasi fade transition jika diinginkan
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            // Jika username tidak tersedia, tampilkan pesan peringatan
            Toast.makeText(this, "Kesalahan: Username tidak ditemukan. Harap login ulang.", Toast.LENGTH_SHORT).show();
        }
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
