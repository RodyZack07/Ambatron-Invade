package com.example.savesthekunti.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savesthekunti.Database.AccountCenter;
import com.example.savesthekunti.R;

public class InfoMenuActivity extends AppCompatActivity {

    private ImageButton prevsbtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_menu);

        prevsbtn = findViewById(R.id.backbutton);
        prevsbtn.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

    }
}
