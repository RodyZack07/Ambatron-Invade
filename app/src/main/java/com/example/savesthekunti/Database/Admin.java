package com.example.savesthekunti.Database;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savesthekunti.R;

public class Admin extends AppCompatActivity {

    private Button updateButton;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_layout);

        // Inisialisasi tombol
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Set onclick listener untuk tombol update
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin.this, Update.class);
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
