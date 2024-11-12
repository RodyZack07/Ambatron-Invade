package com.example.savesthekunti.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.savesthekunti.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfilActivity extends AppCompatActivity {

    private TextView nicknameTextView, createdAtTextView;
    private ImageView profileImageView;
    private FirebaseFirestore firestore;
    private String username;
    private ImageButton prevsbtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_menu);

        //  PREVS BTN
        prevsbtn = findViewById(R.id.prevsBtn3);
        prevsbtn.setOnClickListener(view ->{
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        // Inisialisasi View
        nicknameTextView = findViewById(R.id.nicknameTextView);
        createdAtTextView = findViewById(R.id.createdAtTextView);
        profileImageView = findViewById(R.id.profileImageView); // Misalnya ImageView untuk foto profil

        // Inisialisasi Firestore
        firestore = FirebaseFirestore.getInstance();

        // Ambil username dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        if (username != null) {
            // Panggil method untuk load profil
            loadUserProfile();
        } else {
            Toast.makeText(ProfilActivity.this, "Username tidak ditemukan!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserProfile() {
        // Ambil data dari Firestore berdasarkan username sebagai ID
        DocumentReference akunRef = firestore.collection("Akun").document(username);
        akunRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Mengambil data profil
                    String username = document.getString("username");
                    Long createdAt = document.getLong("created_at");

                    // Tampilkan data di TextView
                    nicknameTextView.setText(username);
                    if (createdAt != null) {
                        createdAtTextView.setText("Created At: " + convertTimestampToDate(createdAt));
                    }
                } else {
                    Toast.makeText(ProfilActivity.this, "Profil tidak ditemukan!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ProfilActivity.this, "Gagal memuat profil: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method untuk mengonversi timestamp menjadi format tanggal yang lebih ramah pengguna
    private String convertTimestampToDate(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        java.util.Date date = new java.util.Date(timestamp);
        return sdf.format(date);
    }
}
