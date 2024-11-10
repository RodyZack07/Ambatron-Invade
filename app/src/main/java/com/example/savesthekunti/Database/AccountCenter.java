package com.example.savesthekunti.Database;

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

public class AccountCenter extends AppCompatActivity {

    private TextView nicknameTextView, createdAtTextView;
    private ImageView profileImageView;
    private FirebaseFirestore firestore;
    private String username;
    private String email;
    private ImageButton prevsbtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_center);

        // Inisialisasi tombol kembali
        prevsbtn = findViewById(R.id.backbutton);
        prevsbtn.setOnClickListener(view -> finish());

        // Inisialisasi tampilan
        nicknameTextView = findViewById(R.id.username_value);
        createdAtTextView = findViewById(R.id.email_label);

        // Inisialisasi Firestore
        firestore = FirebaseFirestore.getInstance();

        // Ambil username dan email dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);
        email = sharedPreferences.getString("email", null);  // Ambil email dari SharedPreferences

        if (username != null && email != null) {
            // Set email ke TextView
            createdAtTextView.setText(email);  // Tampilkan email di createdAtTextView

            // Panggil metode untuk load profil
            loadUserProfile();
            loadUserEmail();
        } else {
            Toast.makeText(AccountCenter.this, "Username atau email tidak ditemukan!", Toast.LENGTH_SHORT).show();
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
                    String email = document.getString("email");

                    // Tampilkan data di TextView
                    nicknameTextView.setText(username);
                    createdAtTextView.setText(email);
                } else {
                    Toast.makeText(AccountCenter.this, "Profil tidak ditemukan!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AccountCenter.this, "Gagal memuat profil: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserEmail() {
        // Ambil data dari Firestore berdasarkan username sebagai ID
        DocumentReference akunRef = firestore.collection("Akun").document(email);
        akunRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Mengambil data profil
                    // Tampilkan data di TextView
                    createdAtTextView.setText(email);
                }
            }
        });
    }
}
