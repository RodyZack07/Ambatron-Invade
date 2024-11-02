package com.example.savesthekunti.Database;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savesthekunti.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Update extends AppCompatActivity {

    private EditText namaField, progressField, achievementField, skinField;
    private Button updateButton;
    private DatabaseReference databaseReference;
    private String userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_layout);

        // Inisialisasi Firebase dan UID
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Inisialisasi view
        namaField = findViewById(R.id.namaField);
        progressField = findViewById(R.id.progressField);
        achievementField = findViewById(R.id.achievementField);
        skinField = findViewById(R.id.skinField);
        updateButton = findViewById(R.id.updateButton);

        // Update button click
        updateButton.setOnClickListener(v -> updateUser());
    }

    private void updateUser() {
        String nama = namaField.getText().toString();
        String progress = progressField.getText().toString();
        String achievement = achievementField.getText().toString();
        String skin = skinField.getText().toString();

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("nama", nama);
        updateData.put("progress", progress);
        updateData.put("achievement", achievement);
        updateData.put("skin", skin);

        databaseReference.child("Akun").child(userUid).updateChildren(updateData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Update.this, "Data berhasil diperbarui!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Update.this, "Gagal memperbarui data!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
