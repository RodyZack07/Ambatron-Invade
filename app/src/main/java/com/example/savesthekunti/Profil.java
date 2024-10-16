package com.example.savesthekunti;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profil extends MainActivity {
    private DatabaseReference databaseReference;
    private String currentUserId; // Simpan ID pengguna yang sedang login
    private TextView tvUsername; // TextView untuk menampilkan username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_menu);

        tvUsername = findViewById(R.id.nicknameTextView);

        // Ambil ID pengguna yang sedang login (misalnya dari SharedPreferences)
        currentUserId = "id_pengguna_saat_ini"; // Ganti dengan ID pengguna yang sebenarnya

        // Ambil username dari database
        getUserProfile();
    }

    private void getUserProfile() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Akun").child(currentUserId);
        databaseReference.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.getValue(String.class);
                    tvUsername.setText(username);
                } else {
                    // Tangani jika username tidak ditemukan
                    tvUsername.setText("Username tidak ditemukan");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Tangani kesalahan
                tvUsername.setText("Kesalahan mengambil data");
            }
        });
    }
}
