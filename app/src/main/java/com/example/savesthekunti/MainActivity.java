// ============ Packages ====================
package com.example.savesthekunti;

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
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

// ============== Main Code ==================
public class MainActivity extends AppCompatActivity {

    // Class audio
    private AudioPlayer audioPlayer;
    private VideoView videoViewBackground;
    private PopupWindow popupWindow;
    private PopupWindow exitPopupWindow;
    private int videoPosition;

    // =========== OnCreate ===============
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        playButton.setOnClickListener(View ->{

        Intent intent = new Intent(MainActivity.this, SelectFighterActivity.class);
        startActivity(intent);
        });



        //
    }

    @Override
    protected void onResume(){
        super.onResume();
        videoViewBackground.seekTo(videoPosition);
        videoViewBackground.start();
    }

    @Override
    protected void onPause(){
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

    private void swapSelect(View anchorView){

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioPlayer != null) {
            audioPlayer.stopMusik();
        }
    }
}
