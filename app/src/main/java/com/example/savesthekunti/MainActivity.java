package com.example.savesthekunti;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    private VideoView videoViewBackground;
    private PopupWindow popupWindow;
    private PopupWindow exitPopupWindow;
    private int videoPosition;
    private MediaPlayer mediaPlayer;
    private TextView welcomeText;
    private FirebaseFirestore db;
    private String user; // ID pengguna untuk mengambil data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Mengambil user ID dari intent
        user = getIntent().getStringExtra("username");

        // Inisialisasi TextView untuk menyambut pengguna
        welcomeText = findViewById(R.id.welcomeText);
        if (user != null) {
            getUserData(user);
        } else {
            Toast.makeText(this, "Pengguna tidak dikenali.", Toast.LENGTH_SHORT).show();
        }

        // Inisialisasi VideoView untuk background
        videoViewBackground = findViewById(R.id.videoViewBackground);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.background_loop);
        videoViewBackground.setVideoURI(videoUri);
        videoViewBackground.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            setVolume(mp, 0f);
        });
        videoViewBackground.start();

        // Inisialisasi MediaPlayer untuk audio
        mediaPlayer = MediaPlayer.create(this, R.raw.galatic_idle);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // Inisialisasi tombol
        ImageButton settingsBtn = findViewById(R.id.setting_button);
        ImageButton quitButton = findViewById(R.id.btn_quit);
        ImageButton playButton = findViewById(R.id.play_button);
        ImageButton profilMenu = findViewById(R.id.profile);
        ImageButton achievementMenu = findViewById(R.id.achievement);

        // Set onClickListener untuk masing-masing tombol
        settingsBtn.setOnClickListener(view -> showSettingsPopup(view));
        quitButton.setOnClickListener(view -> showExitPopup(view));
        playButton.setOnClickListener(v -> directSelectFighter());
        profilMenu.setOnClickListener(view -> openLoginActivity());
        achievementMenu.setOnClickListener(view -> showAchievement());
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoViewBackground.seekTo(videoPosition);
        videoViewBackground.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPosition = videoViewBackground.getCurrentPosition();
        videoViewBackground.pause();
    }

    // Mengambil data pengguna dari Firestore
    private void getUserData(String userId) {
        db.collection("Akun").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String username = document.getString("username");
                            welcomeText.setText("Selamat datang, " + username + "!");
                            getSkinData(userId);
                        } else {
                            Toast.makeText(MainActivity.this, "Data pengguna tidak ditemukan.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Mengambil data skin dari Firestore
    private void getSkinData(String userId) {
        db.collection("Akun").document(userId).collection("Koleksi_Skin")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot result = task.getResult();
                        if (result != null && !result.isEmpty()) {
                            StringBuilder skins = new StringBuilder("Skin yang dimiliki:\n");
                            for (QueryDocumentSnapshot document : result) {
                                String skinId = document.getString("id_skin");
                                Boolean isLocked = document.getBoolean("status_terkunci");

                                if (skinId != null) {
                                    skins.append("Skin ID: ").append(skinId).append(" - ").append(isLocked ? "Terkunci" : "Terbuka").append("\n");
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

    // Mengatur volume video
    private void setVolume(MediaPlayer mediaPlayer, float volume) {
        mediaPlayer.setVolume(volume, volume);
    }

    // Menampilkan popup pengaturan
    private void showSettingsPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        // Popup size and animation
        int popupWidth = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int popupHeight = getResources().getDimensionPixelSize(R.dimen.popup_height);
        popupWindow = new PopupWindow(popupView, popupWidth, popupHeight, true);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);

        // Show popup
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        // Handle the Info button inside the popup
        ImageButton closeBtn = popupView.findViewById(R.id.closebtn);

        // Menangani klik tombol close untuk menutup popup
        closeBtn.setOnClickListener(view -> popupWindow.dismiss());

        // Inisialisasi SeekBar untuk mengatur volume musik
        SeekBar seekBarVol = popupView.findViewById(R.id.seekBarVol);
        seekBarVol.setMax(100);
        seekBarVol.setProgress(50);
        setVolume(mediaPlayer, 0.5f);

        // Listener untuk mengubah volume saat SeekBar berubah
        seekBarVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100f;
                setVolume(mediaPlayer, volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    // Menampilkan popup keluar
    private void showExitPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.exit_layout, null);

        int popupWidth = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int popupHeight = getResources().getDimensionPixelSize(R.dimen.popup_height);

        exitPopupWindow = new PopupWindow(popupView, popupWidth, popupHeight, true);
        exitPopupWindow.setAnimationStyle(R.style.PopupAnimation);
        exitPopupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        ImageButton noBtn = popupView.findViewById(R.id.imageYes);
        ImageButton yesBtn = popupView.findViewById(R.id.imageExit);

        yesBtn.setOnClickListener(view -> finish());
        noBtn.setOnClickListener(view -> exitPopupWindow.dismiss());
    }

    private void showAchievement() {
        Intent showAchievement = new Intent(MainActivity.this, ProfilActivity.class);
        startActivity(showAchievement);
    }

    // Membuka activity Login
    private void openLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, Login.class);
        startActivity(loginIntent);
    }

    // Direct ke Select Fighter
    private void directSelectFighter() {
        Intent intent = new Intent(MainActivity.this, SelectFighterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
