package com.example.savesthekunti.Database;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savesthekunti.Model.AdaptorUser;
import com.example.savesthekunti.Model.EditAsAdminActivity;
import com.example.savesthekunti.R;

public class Admin extends AppCompatActivity {

    private Button updateButton;
    private Button deleteButton;
    private ImageButton prevsbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_layout);

        // Inisialisasi tombol
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);
        prevsbtn = findViewById(R.id.prevsBtn3);

        prevsbtn.setOnClickListener(view -> finish());





        // Set onclick listener untuk tombol update
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin.this, EditAsAdminActivity.class);
                startActivity(intent);
            }
        });

        // Set onclick listener untuk tombol delete
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin.this, Delete.class);
                startActivity(intent);
            }
        });
    }
}
