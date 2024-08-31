package com.example.savesthekunti;

import android.media.MediaPlayer;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //class audio
    private AudioPlayer audioPlayer;


    private PopupWindow popupWindow;
    private PopupWindow exitPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inisialisasi Audio
        audioPlayer = new AudioPlayer(this, R.raw.galatic_idle);
        audioPlayer.playMusic();


        // Inisialisasi setting button
        ImageButton settingsBtn = findViewById(R.id.setting_button);
        ImageButton quitButton = findViewById(R.id.btn_quit);

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettingsPopup(view);
            }
        });

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExitPopup(view);
            }

        });
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

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitPopupWindow.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(audioPlayer != null){
            audioPlayer.stopMusik();
        }
    }
}
