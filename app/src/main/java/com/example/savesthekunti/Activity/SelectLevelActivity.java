package com.example.savesthekunti.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
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
    private MediaPlayer buttonSFX;

    //    AUDIO
    private MediaPlayer mediaPlayer;

    private Level[] levels = {
            new Level(1, 100, 30, 500, 10, 30, R.drawable.boss_amba),
            new Level(2, 130, 60, 100000, 20, 60, R.drawable.boss_amba),
            new Level(3, 160, 90, 1500, 30, 90, R.drawable.bos_astronout),
            new Level(4, 190, 110, 2000, 40, 110, R.drawable.bos_astronout),
            new Level(5, 210, 140, 2500, 50, 140, R.drawable.bos_blackarmor),
            new Level(6, 240, 170, 3000, 60, 170, R.drawable.bos_blackarmor),
            new Level(7, 270, 200, 3500, 70, 200, R.drawable.bos_jawa),
            new Level(8, 300, 230, 4000, 80, 220, R.drawable.bos_jawa),
            new Level(9, 160, 260, 5000, 90, 240, R.drawable.bos_claw),
            new Level(10, 190, 290, 6000, 100, 260, R.drawable.bos_claw),
            new Level(11, 230, 300, 8000, 110, 280, R.drawable.bos_crab),
            new Level(12, 260, 310, 1000, 120, 300, R.drawable.bos_crab),
            new Level(13, 290, 320, 12000, 140, 310, R.drawable.bos_punk),
            new Level(14, 310, 340, 15000, 160, 320, R.drawable.bos_punk),
            new Level(15, 320, 360, 25000, 250, 330, R.drawable.bos_punk),
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_activity);

        // Ambil data skin yang dipilih dari SelectFighterActivity
        selectedSkin = getIntent().getStringExtra("selectedSkin");
        buttonSFX = MediaPlayer.create(this, R.raw.button_sfx);

        ImageButton prevButton = findViewById(R.id.prevsBtn1);


        //        ====================================== Audio ======================================

        // Initialize MediaPlayer for audio
        mediaPlayer = MediaPlayer.create(this, R.raw.level_idle);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        backgroundVideo = findViewById(R.id.backgroundVideo);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.raw_gameplay);
        backgroundVideo.setVideoURI(videoUri);

        backgroundVideo.setOnPreparedListener(mp -> {
            mp.setVolume(0f, 0f);
            mp.setLooping(true);
        });
        backgroundVideo.start();

        prevButton.setOnClickListener(v -> prevsbutton());





//        ============== CALL RESOURCE IMAGE VIEW LEVEL ================
        ImageView bgLevel1 = findViewById(R.id.bg_level_1_1);
        bgLevel1.setOnClickListener(this::showLevelPopup);

        ImageView bgLevel2 = findViewById(R.id.bg_level_2_1);
        bgLevel2.setOnClickListener(this::showLevelPopup2);

        ImageView bgLevel3 = findViewById(R.id.bg_level_3_1);
        bgLevel3.setOnClickListener(this::showLevelPopup3);

        ImageView bgLevel4= findViewById(R.id.bg_level_4_1);
        bgLevel4.setOnClickListener(this::showLevelPopup4);

        ImageView bgLevel5= findViewById(R.id.bg_level_5_1);
        bgLevel5.setOnClickListener(this::showLevelPopup5);

        ImageView bgLevel6= findViewById(R.id.bg_level_6_1);
        bgLevel6.setOnClickListener(this::showLevelPopup6);

        ImageView bgLevel7= findViewById(R.id.bg_level_7_1);
        bgLevel7.setOnClickListener(this::showLevelPopup7);

        ImageView bgLevel8= findViewById(R.id.bg_level_8_1);
        bgLevel8.setOnClickListener(this::showLevelPopup8);

        ImageView bgLevel9= findViewById(R.id.bg_level_9_1);
        bgLevel9.setOnClickListener(this::showLevelPopup9);

        ImageView bgLevel10= findViewById(R.id.bg_level_10_1);
        bgLevel10.setOnClickListener(this::showLevelPopup10);

        ImageView bgLevel11= findViewById(R.id.bg_level_11_1);
        bgLevel11.setOnClickListener(this::showLevelPopup11);

        ImageView bgLevel12= findViewById(R.id.bg_level_12_1);
        bgLevel12.setOnClickListener(this::showLevelPopup12);

        ImageView bgLevel13= findViewById(R.id.bg_level_13_1);
        bgLevel13.setOnClickListener(this::showLevelPopup13);

        ImageView bgLevel14= findViewById(R.id.bg_level_14_1);
        bgLevel14.setOnClickListener(this::showLevelPopup14);

        ImageView bgLevel15= findViewById(R.id.bg_level_15_1);
        bgLevel15.setOnClickListener(this::showLevelPopup15);
        // Implementasi lainnya untuk level yang berbeda
    }


    //    =============== FUNCTION SHOW LEVEL POPUP ===================
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

        playBtn.setOnClickListener(view -> {showlevelActivity();
                                            finish();});


        homeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });
        closeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });


    }

    private void showLevelPopup2(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.game_info_2, null);

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

        playBtn.setOnClickListener(view -> {showlevelActivity2();
                                            finish();});


        homeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });
        closeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });


    }

    private void prevsbutton() {
        Intent newIntent = new Intent(this, SelectFighterActivity.class);
        newIntent.putExtra("username", "shinoa"); // Contoh pengiriman username
        startActivity(newIntent);

        // Tambahkan animasi fade transition jika diinginkan
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    private void showLevelPopup3(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.game_info_3, null);

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

        playBtn.setOnClickListener(view -> {showlevelActivity3();
                                            finish();});


        homeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });
        closeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });


    }

    private void showLevelPopup4(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.game_info_4, null);

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

        playBtn.setOnClickListener(view -> {showlevelActivity4();
                                            finish();});


        homeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });
        closeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });


    }

    private void showLevelPopup5(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.game_info_5, null);

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

        playBtn.setOnClickListener(view -> {showlevelActivity5();
            finish();});


        homeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });
        closeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });


    }

    private void showLevelPopup6(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.game_info_6, null);

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

        playBtn.setOnClickListener(view -> {showlevelActivity6();
                                            finish();});


        homeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });
        closeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });


    }

    private void showLevelPopup7(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.game_info_7, null);

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

        playBtn.setOnClickListener(view -> {showlevelActivity7();
                                            finish();});


        homeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });
        closeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });


    }

    private void showLevelPopup8(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.game_info_8, null);

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

        playBtn.setOnClickListener(view -> {showlevelActivity8();
                                            finish();});


        homeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });
        closeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });


    }

    private void showLevelPopup9(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.game_info_9, null);

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

        playBtn.setOnClickListener(view -> {showlevelActivity9();
                                            finish();});


        homeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });
        closeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });


    }

    private void showLevelPopup10(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.game_info_10, null);

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

        playBtn.setOnClickListener(view -> {showlevelActivity10();
                                            finish();});


        homeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });
        closeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });


    }

    private void showLevelPopup11(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.game_info_11, null);

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

        playBtn.setOnClickListener(view -> {showlevelActivity11();
                                            finish();});


        homeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });
        closeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });


    }

    private void showLevelPopup12(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.game_info_12, null);

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

        playBtn.setOnClickListener(view -> {showlevelActivity12();
                                            finish();});


        homeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });
        closeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });


    }

    private void showLevelPopup13(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.game_info_13, null);

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

        playBtn.setOnClickListener(view -> {showlevelActivity13();
                                            finish();});

        homeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });
        closeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });


    }

    private void showLevelPopup14(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.game_info_14, null);

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

        playBtn.setOnClickListener(view -> {showlevelActivity14();
                                            finish();});


        homeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });
        closeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });


    }

    private void showLevelPopup15(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.game_info_15, null);

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

        playBtn.setOnClickListener(view -> {showlevelActivity15();
                                            finish();});


        homeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });
        closeBtn.setOnClickListener(view -> {
            popupWindow.dismiss();
        });


    }

    // =============== FUNCTION BUTTON LEVEL START =======================
    private void showlevelActivity() {
        Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
        intent.putExtra("selectedSkin", selectedSkin);
        Level levelData = levels[0];
        intent.putExtra("levelData", levelData);
        startActivity(intent);
        buttonSFX.start();
    }

    private void showlevelActivity2() {
        Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
        intent.putExtra("selectedSkin", selectedSkin); // Kirim skin yang dipilih ke GameActivity
        Level levelData = levels[1];
        intent.putExtra("levelData", levelData);
        startActivity(intent);
        buttonSFX.start();
    }

    private void showlevelActivity3() {
        Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
        intent.putExtra("selectedSkin", selectedSkin);
        Level levelData = levels[2];
        intent.putExtra("levelData", levelData);
        startActivity(intent);
        buttonSFX.start();
    }

    private void showlevelActivity4() {
        Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
        intent.putExtra("selectedSkin", selectedSkin);
        Level levelData = levels[3];
        intent.putExtra("levelData", levelData);
        startActivity(intent);
        buttonSFX.start();
    }

    private void showlevelActivity5() {
        Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
        intent.putExtra("selectedSkin", selectedSkin);
        Level levelData = levels[4];
        intent.putExtra("levelData", levelData);
        startActivity(intent);
        buttonSFX.start();
    }

    private void showlevelActivity6() {
        Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
        intent.putExtra("selectedSkin", selectedSkin);
        Level levelData = levels[5];
        intent.putExtra("levelData", levelData);
        startActivity(intent);
        buttonSFX.start();
    }

    private void showlevelActivity7() {
        Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
        intent.putExtra("selectedSkin", selectedSkin);
        Level levelData = levels[6];
        intent.putExtra("levelData", levelData);
        startActivity(intent);
        buttonSFX.start();
    }

    private void showlevelActivity8() {
        Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
        intent.putExtra("selectedSkin", selectedSkin);
        Level levelData = levels[7];
        intent.putExtra("levelData", levelData);
        startActivity(intent);
        buttonSFX.start();
    }

    private void showlevelActivity9() {
        Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
        intent.putExtra("selectedSkin", selectedSkin);
        Level levelData = levels[8];
        intent.putExtra("levelData", levelData);
        startActivity(intent);
        buttonSFX.start();
    }

    private void showlevelActivity10() {
        Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
        intent.putExtra("selectedSkin", selectedSkin);
        Level levelData = levels[9];
        intent.putExtra("levelData", levelData);
        startActivity(intent);
        buttonSFX.start();
    }

    private void showlevelActivity11() {
        Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
        intent.putExtra("selectedSkin", selectedSkin);
        Level levelData = levels[10];
        intent.putExtra("levelData", levelData);
        startActivity(intent);
        buttonSFX.start();
    }

    private void showlevelActivity12() {
        Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
        intent.putExtra("selectedSkin", selectedSkin);
        Level levelData = levels[11];
        intent.putExtra("levelData", levelData);
        startActivity(intent);
        buttonSFX.start();
    }

    private void showlevelActivity13() {
        Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
        intent.putExtra("selectedSkin", selectedSkin);
        Level levelData = levels[12];
        intent.putExtra("levelData", levelData);
        startActivity(intent);
        buttonSFX.start();
    }

    private void showlevelActivity14() {
        Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
        intent.putExtra("selectedSkin", selectedSkin);
        Level levelData = levels[13];
        intent.putExtra("levelData", levelData);
        startActivity(intent);
        buttonSFX.start();
    }

    private void showlevelActivity15() {
        Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
        intent.putExtra("selectedSkin", selectedSkin);
        Level levelData = levels[14];
        intent.putExtra("levelData", levelData);
        startActivity(intent);
        buttonSFX.start();

    }


    //    ======================= LOAD VOLUME AND SET VOLUME ===================================
    private int loadVolumePreference() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        return prefs.getInt("volume", 50); // default 50
    }

    private void setVolume(MediaPlayer mediaPlayer, float volume) {
        mediaPlayer.setVolume(volume, volume);
    }

    @Override
    protected void onResume() {
        super.onResume();
        backgroundVideo.start();

        if (mediaPlayer != null) {
            mediaPlayer.start();
            setVolume(mediaPlayer, loadVolumePreference() / 100f);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        backgroundVideo.pause();

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backgroundVideo != null) {
            backgroundVideo.stopPlayback();
            backgroundVideo = null;
        }

        if (buttonSFX != null) {
            buttonSFX.release();
            buttonSFX = null;
        }
    }
}
