package com.example.savesthekunti.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.VideoView;

import com.example.savesthekunti.R;

public class SelectLevelActivity extends AppCompatActivity {

    private VideoView backgroundVideo;
    private PopupWindow popupWindow;
    private View settingsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_activity);

        ImageButton prevButton = findViewById(R.id.prevsBtn1);
        prevButton.setOnClickListener(v -> finish());

        backgroundVideo = findViewById(R.id.backgroundVideo);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.raw_gameplay);
        backgroundVideo.setVideoURI(videoUri);

        backgroundVideo.setOnPreparedListener(mp -> {
            mp.setVolume(0f, 0f);
            mp.setLooping(true);
        });
        backgroundVideo.start();

        ImageView bgLevel1 = findViewById(R.id.bg_level_1_1);
        bgLevel1.setOnClickListener(this::showLevelPopup);  // Memanggil showLevelPopup saat level 1 dipilih

        // Implementasi lainnya untuk level yang berbeda
    }

    private void showLevelPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.game_info_1, null);

        int popupWidth = getResources().getDimensionPixelSize(R.dimen.level_width);
        int popupHeight = getResources().getDimensionPixelSize(R.dimen.level_height);
        popupWindow = new PopupWindow(popupView, popupWidth, popupHeight, true);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);

        settingsView = findViewById(R.id.setting_button);
        if (settingsView != null) {
            settingsView.setVisibility(View.GONE);  // Sembunyikan tombol setting saat popup muncul
        }

        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

//        INISIALISASI BUTTON
        ImageButton closeBtn = popupView.findViewById(R.id.silang);
        ImageButton homeBtn = popupView.findViewById(R.id.homebtn);
        ImageButton playBtn = popupView.findViewById(R.id.playbtn);

        playBtn.setOnClickListener(view -> showlevelActivity());


        homeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });
        closeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });


    }


    private void showlevelActivity() {
        Intent level1Intent = new Intent(SelectLevelActivity.this, GameActivity.class);
        startActivity(level1Intent);
        if (popupWindow != null) {
            popupWindow.dismiss();  // Tutup popup setelah pindah ke GameActivity
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        backgroundVideo.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        backgroundVideo.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backgroundVideo != null) {
            backgroundVideo.stopPlayback();
            backgroundVideo = null;
        }
    }
}
