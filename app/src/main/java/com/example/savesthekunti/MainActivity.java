// ============ Packages ====================
package com.example.savesthekunti;

import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import android.database.sqlite.SQLiteDatabase;

// ============== Main Code ==================
public class MainActivity extends AppCompatActivity {

    // Class audio
    private AudioPlayer audioPlayer;
    private VideoView videoViewBackground;
    private PopupWindow popupWindow;
    private PopupWindow exitPopupWindow;
    private int videoPosition;
    private DBHelper dbHelper;

    // =========== OnCreate ===============
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize DBHelper for database operations
        dbHelper = new DBHelper(this);

        // Inisialisasi VideoView
        videoViewBackground = findViewById(R.id.videoViewBackground);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.background_loop);
        videoViewBackground.setVideoURI(videoUri);

        // Mengatur volume ke 0
        videoViewBackground.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            setVolume(mp, 0f);  // Mengatur volume ke 0
        });

        // Mengubah ukuran VideoView untuk rasio 9:16
        adjustVideoViewSize();

        // Start Video
        videoViewBackground.start();

        // Inisialisasi Audio
        audioPlayer = new AudioPlayer(this, R.raw.galatic_idle);
        audioPlayer.playMusic();

        // Inisialisasi setting button
        ImageButton settingsBtn = findViewById(R.id.setting_button);
        ImageButton quitButton = findViewById(R.id.btn_quit);
        ImageButton playButton = findViewById(R.id.play_button);

        settingsBtn.setOnClickListener(view -> showSettingsPopup(view));

        quitButton.setOnClickListener(view -> showExitPopup(view));

        playButton.setOnClickListener(View -> {
            // Insert akun, profile, dan achievement ke database
            insertAkunData("JohnDoe", "john.doe@example.com", "securePassword123");

            Intent intent = new Intent(MainActivity.this, SelectFighterActivity.class);
            startActivity(intent);
        });
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

    private void setVolume(MediaPlayer mediaPlayer, float volume) {
        mediaPlayer.setVolume(volume, volume);
    }

    private void adjustVideoViewSize() {
        // Mendapatkan ukuran layar dalam dp
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidthDp = (int) (displayMetrics.widthPixels / displayMetrics.density);

        // Menghitung tinggi berdasarkan rasio 9:16
        int videoWidth = screenWidthDp;
        int videoHeight = (int) (videoWidth * 21.0 / 9.0);

        // Mengonversi dari dp ke piksel
        int videoWidthPx = (int) (videoWidth * displayMetrics.density);
        int videoHeightPx = (int) (videoHeight * displayMetrics.density);

        // Mengatur ukuran VideoView
        ViewGroup.LayoutParams params = videoViewBackground.getLayoutParams();
        params.width = videoWidthPx;
        params.height = videoHeightPx;
        videoViewBackground.setLayoutParams(params);
    }

    private void showSettingsPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        // Ambil dimensi dari resources
        int popupWidth = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int popupHeight = getResources().getDimensionPixelSize(R.dimen.popup_height);

        popupWindow = new PopupWindow(popupView, popupWidth, popupHeight, true);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
    }

    private void showExitPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.exit_layout, null);

        // Ambil dimensi dari resources
        int popupWidth = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int popupHeight = getResources().getDimensionPixelSize(R.dimen.popup_height);

        exitPopupWindow = new PopupWindow(popupView, popupWidth, popupHeight, true);
        exitPopupWindow.setAnimationStyle(R.style.PopupAnimation);
        exitPopupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        // Inisialisasi YES & NO button
        ImageButton noBtn = popupView.findViewById(R.id.imageYes);
        ImageButton yesBtn = popupView.findViewById(R.id.imageExit);

        // Set ukuran tombol
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

        yesBtn.setOnClickListener(view -> finish());

        noBtn.setOnClickListener(view -> exitPopupWindow.dismiss());
    }

    private void insertAkunData(String username, String email, String password) {
        // Inserting data into Akun table
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues akunValues = new ContentValues();
        akunValues.put("username", username);
        akunValues.put("email", email);
        akunValues.put("password", password);

        long akunId = db.insert("Akun", null, akunValues);
        if (akunId != -1) {
            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();

            // Insert profile and achievement after account is created
            insertProfileData((int) akunId, "https://example.com/profile.jpg");
            insertAchievementData((int) akunId, "First Achievement", "This is the first achievement.");
        } else {
            Toast.makeText(this, "Error creating account.", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

    private void insertProfileData(int akunId, String photoProfile) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues profileValues = new ContentValues();
        profileValues.put("id_akun", akunId);
        profileValues.put("photo_profile", photoProfile);

        long profileId = db.insert("Profile", null, profileValues);
        if (profileId != -1) {
            Toast.makeText(this, "Profile created successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error creating profile.", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

    private void insertAchievementData(int akunId, String namaAchievement, String deskripsi) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues achievementValues = new ContentValues();
        achievementValues.put("id_akun", akunId);
        achievementValues.put("nama_achievement", namaAchievement);
        achievementValues.put("deskripsi", deskripsi);

        long achievementId = db.insert("Achievement", null, achievementValues);
        if (achievementId != -1) {
            Toast.makeText(this, "Achievement added successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error adding achievement.", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioPlayer != null) {
            audioPlayer.stopMusik();
        }
    }
}
