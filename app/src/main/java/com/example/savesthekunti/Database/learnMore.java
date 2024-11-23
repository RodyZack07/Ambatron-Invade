package com.example.savesthekunti.Database;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savesthekunti.R;

public class learnMore extends AppCompatActivity {

    private ImageButton backbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.account_center_learn_more);
        backbutton = findViewById(R.id.backbutton);
        backbutton.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        ScrollView scrollView = findViewById(R.id.scrollViewCredits);

        // Load animasi dari file XML
        Animation scrollAnimation = AnimationUtils.loadAnimation(this, R.anim.translate);
        scrollView.startAnimation(scrollAnimation);
    }
}
