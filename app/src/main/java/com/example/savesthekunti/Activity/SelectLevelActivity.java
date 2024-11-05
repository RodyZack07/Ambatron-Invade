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
    private String selectedSkin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_activity);

        // Ambil data skin yang dipilih dari SelectFighterActivity
        selectedSkin = getIntent().getStringExtra("selectedSkin");

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
        bgLevel1.setOnClickListener(this::showLevelPopup);

        ImageView bgLevel2 = findViewById(R.id.bg_level_2_1);
        bgLevel2.setOnClickListener(this::showLevelPopup);

        ImageView bgLevel3 = findViewById(R.id.bg_level_3_1);
        bgLevel3.setOnClickListener(this::showLevelPopup);

        ImageView bgLevel4= findViewById(R.id.bg_level_4_1);
        bgLevel4.setOnClickListener(this::showLevelPopup);

        ImageView bgLevel5= findViewById(R.id.bg_level_5_1);
        bgLevel5.setOnClickListener(this::showLevelPopup);

        ImageView bgLevel6= findViewById(R.id.bg_level_6_1);
        bgLevel6.setOnClickListener(this::showLevelPopup);

        ImageView bgLevel7= findViewById(R.id.bg_level_7_1);
        bgLevel7.setOnClickListener(this::showLevelPopup);

        ImageView bgLevel8= findViewById(R.id.bg_level_8_1);
        bgLevel8.setOnClickListener(this::showLevelPopup);

        ImageView bgLevel9= findViewById(R.id.bg_level_9_1);
        bgLevel9.setOnClickListener(this::showLevelPopup);

        ImageView bgLevel10= findViewById(R.id.bg_level_10_1);
        bgLevel10.setOnClickListener(this::showLevelPopup);

        ImageView bgLevel11= findViewById(R.id.bg_level_11_1);
        bgLevel11.setOnClickListener(this::showLevelPopup);

        ImageView bgLevel12= findViewById(R.id.bg_level_12_1);
        bgLevel12.setOnClickListener(this::showLevelPopup);

        ImageView bgLevel13= findViewById(R.id.bg_level_13_1);
        bgLevel13.setOnClickListener(this::showLevelPopup);

        ImageView bgLevel14= findViewById(R.id.bg_level_14_1);
        bgLevel14.setOnClickListener(this::showLevelPopup);

        ImageView bgLevel15= findViewById(R.id.bg_level_15_1);
        bgLevel15.setOnClickListener(this::showLevelPopup);
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
        Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
        intent.putExtra("selectedSkin", selectedSkin); // Kirim skin yang dipilih ke GameActivity
        startActivity(intent);
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
