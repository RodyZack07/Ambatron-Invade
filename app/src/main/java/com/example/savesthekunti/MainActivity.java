package com.example.savesthekunti;

import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton settingsBtn = findViewById(R.id.setting_button);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettingsPopup(view);
            }
        });
    }

    private void showSettingsPopup(View anchorView) {
        // Inflate layout untuk popup
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        // Calculate the width and height of the popup
        int popupWidth = 650;  // The new width in dp
        int popupHeight = 450; // The new height in dp


        // Create the PopupWindow
        popupWindow = new PopupWindow(popupView, popupWidth, popupHeight, true);

        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        // Show the popup at the center of the screen
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
    }
}
