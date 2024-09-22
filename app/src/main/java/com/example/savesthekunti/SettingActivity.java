package com.example.savesthekunti;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity
private PopupWindow infoPopupWindow;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings);

    // Initialize Info Button
    ImageButton infoButton = findViewById(R.id.info_button);
    infoButton.setOnClickListener(view -> showInfoPopup(view));
}

private void showInfoPopup(View anchorView) {
    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    View popupView = inflater.inflate(R.layout.info_menu, null);

    int popupWidth = getResources().getDimensionPixelSize(R.dimen.popup_width);
    int popupHeight = getResources().getDimensionPixelSize(R.dimen.popup_height);

    infoPopupWindow = new PopupWindow(popupView, popupWidth, popupHeight, true);
    infoPopupWindow.setAnimationStyle(R.style.PopupAnimation);
    infoPopupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

    // Apply shrink-up animation
    Animation shrinkUp = AnimationUtils.loadAnimation(this, R.anim.shrink_up);
    popupView.startAnimation(shrinkUp);
}
{
}
