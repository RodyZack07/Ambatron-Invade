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

    private ImageButton accountButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_menu);

        Log.d("InfoMenuActivity", "Layout loaded successfully"); // Debug log

        accountButton = findViewById(R.id.AkunCenter);

        if (accountButton == null) {
            Log.e("InfoMenuActivity", "ImageButton with ID AkunCenter not found!");
        } else {
            accountButton.setOnClickListener(view -> {
                Intent akun = new Intent(InfoMenuActivity.this, AccountCenter.class);
                startActivity(akun);
            });
        }
    }
}
