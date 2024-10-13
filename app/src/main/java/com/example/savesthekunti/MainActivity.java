package com.example.savesthekunti;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private VideoView videoViewBackground;
    private PopupWindow popupWindow;
    private PopupWindow exitPopupWindow;
    private int videoPosition;
    private DBHelper dbHelper;
    private View settingsView;

    // Inisialisasi MediaPlayer
    private MediaPlayer mediaPlayer;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ambil username dari Intent
        String username = getIntent().getStringExtra("username");
        if (username != null) {
            Toast.makeText(this, "Selamat datang, " + username + "!", Toast.LENGTH_SHORT).show();
        }

        // Inisialisasi TextView untuk menyambut pengguna
        welcomeText = findViewById(R.id.welcomeText); // Pastikan ID ini ada di layout
        welcomeText.setText(username != null ? "Selamat datang, " + username + "!" : "Selamat datang!"); // Menampilkan pesan sambutan

        dbHelper = new DBHelper(this);  // Inisialisasi DBHelper

        videoViewBackground = findViewById(R.id.videoViewBackground);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.background_loop);
        videoViewBackground.setVideoURI(videoUri);

        videoViewBackground.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            setVolume(mp, 0f);
        });

        videoViewBackground.start();

        // Inisialisasi MediaPlayer untuk audio
        mediaPlayer = MediaPlayer.create(this, R.raw.galatic_idle); // Ganti dengan nama file audio kamu
        mediaPlayer.setLooping(true); // Memutar audio berulang
        mediaPlayer.start(); // Memulai pemutaran audio

        ImageButton settingsBtn = findViewById(R.id.setting_button);
        ImageButton quitButton = findViewById(R.id.btn_quit);
        ImageButton playButton = findViewById(R.id.play_button);
        ImageButton profilMenu = findViewById(R.id.profile);

        settingsBtn.setOnClickListener(view -> showSettingsPopup(view));
        quitButton.setOnClickListener(view -> showExitPopup(view));
        playButton.setOnClickListener(v -> directSelectFighter());
        profilMenu.setOnClickListener(view -> openLoginActivity());
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

    // Mengatur volume video
    private void setVolume(MediaPlayer mediaPlayer, float volume) {
        mediaPlayer.setVolume(volume, volume);
    }

    private void showSettingsPopup(View anchorView) {
        // Inflate layout popup Settings
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        // Popup size and animation
        int popupWidth = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int popupHeight = getResources().getDimensionPixelSize(R.dimen.popup_height);
        popupWindow = new PopupWindow(popupView, popupWidth, popupHeight, true);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);

        // Hide the Settings button to avoid overlap
        settingsView = findViewById(R.id.setting_button);
        settingsView.setVisibility(View.GONE);

        // Show popup
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        // Handle the Info button inside the popup
        ImageButton infoButton = popupView.findViewById(R.id.info_button);
        infoButton.setOnClickListener(v -> {
            // Show Info as a popup
            showInfoPopup();
            popupWindow.dismiss();
        });

        // Inisialisasi SeekBar untuk mengatur volume musik
        SeekBar seekBarVol = popupView.findViewById(R.id.seekBarVol);
        seekBarVol.setMax(100);
        seekBarVol.setProgress(50); // Set default volume to 50%
        setVolume(mediaPlayer, 0.5f); // Set initial volume to 50%

        // Listener untuk mengubah volume saat SeekBar berubah
        seekBarVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100f; // Mengubah progress menjadi nilai antara 0.0 dan 1.0
                setVolume(mediaPlayer, volume); // Mengatur volume MediaPlayer
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Tidak ada tindakan yang diperlukan di sini
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Tidak ada tindakan yang diperlukan di sini
            }
        });

        // Restore the Settings button when the popup is dismissed
        popupWindow.setOnDismissListener(() -> settingsView.setVisibility(View.VISIBLE));
    }

    private void showInfoPopup() {
        // Inflate the Info popup layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View infoPopupView = inflater.inflate(R.layout.info_menu, null);

        // Get width and height from dimens.xml
        int popupWidth = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int popupHeight = getResources().getDimensionPixelSize(R.dimen.popup_height);

        // Setup PopupWindow for Info menu with imported width and height
        PopupWindow infoPopupWindow = new PopupWindow(infoPopupView, popupWidth, popupHeight, true);

        infoPopupWindow.setAnimationStyle(R.style.PopupAnimation);
        infoPopupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
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

        int yesNoWidth = getResources().getDimensionPixelSize(R.dimen.yesno_width);
        int yesNoHeight = getResources().getDimensionPixelSize(R.dimen.yesno_height);

        ViewGroup.LayoutParams yesParams = yesBtn.getLayoutParams();
        yesParams.width = yesNoWidth;
        yesParams.height = yesNoHeight;
        yesBtn.setLayoutParams(yesParams);

        ViewGroup.LayoutParams noParams = noBtn.getLayoutParams();
        noParams.width = yesNoWidth;
        noParams.height = yesNoHeight;
        noBtn.setLayoutParams(noParams);

        yesBtn.setOnClickListener(view -> finish()); // Keluar dari aplikasi
        noBtn.setOnClickListener(view -> exitPopupWindow.dismiss()); // Menutup popup
    }

    // Menampilkan popup profile
    private void openLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, Login.class);
        startActivity(loginIntent);
    }

    // Direct to Select Fighter
    private void directSelectFighter() {
        Intent intent = new Intent(MainActivity.this, SelectFighterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Melepaskan sumber daya MediaPlayer saat aktivitas dihancurkan
            mediaPlayer = null; // Mengatur objek menjadi null
        }
    }
}
