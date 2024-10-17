package com.example.savesthekunti;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfilActivity extends AppCompatActivity {

    private TextView nicknameTextView, createdAtTextView;
    private ImageView profileImageView;
    private DatabaseReference akunRef;
    private String username;
    private ImageButton prevsbtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_menu);

        //  PREVS BTN
        prevsbtn = findViewById(R.id.prevsBtn3);
        prevsbtn.setOnClickListener(view -> finish());

        // Inisialisasi View
        nicknameTextView = findViewById(R.id.nicknameTextView);
        createdAtTextView = findViewById(R.id.createdAtTextView);
        profileImageView = findViewById(R.id.profileImageView); // Misalnya ImageView untuk foto profil

        // Ambil username dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        if (username != null) {
            // Inisialisasi Firebase Database
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://ambatrondb-default-rtdb.asia-southeast1.firebasedatabase.app");
            akunRef = database.getReference("Akun").child(username); // Referensi ke data Akun

            // Panggil method untuk load profil
            loadUserProfile();
        } else {
            Toast.makeText(ProfilActivity.this, "Username tidak ditemukan!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserProfile() {
        // Ambil data dari profil
        DatabaseReference profileRef = akunRef.child("Profile");
        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Mengambil data profil
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String createdAt = String.valueOf(dataSnapshot.child("created_at").getValue(Long.class));

                    // Tampilkan data di TextView
                    nicknameTextView.setText(username);  // Set nickname berdasarkan username
                    createdAtTextView.setText("Created At: " + convertTimestampToDate(Long.parseLong(createdAt)));
                } else {
                    Toast.makeText(ProfilActivity.this, "Profil tidak ditemukan!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfilActivity.this, "Gagal memuat profil: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
