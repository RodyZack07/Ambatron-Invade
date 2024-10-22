package com.example.savesthekunti;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private EditText usernameField, passwordField;
    private Button loginButton, registerButton;
    private ImageButton prevsBtn;
    private DatabaseReference ambatronDB;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        // Inisialisasi Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://ambatrondb-default-rtdb.asia-southeast1.firebasedatabase.app");
        ambatronDB = database.getReference("Akun");

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Inisialisasi UI
        prevsBtn = findViewById(R.id.prevsBtn3);
        prevsBtn.setOnClickListener(view -> finish());

        usernameField = findViewById(R.id.usernameText);
        passwordField = findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.Login);
        registerButton = findViewById(R.id.createAccountButton);

        // Set OnClickListener untuk tombol login dan register
        loginButton.setOnClickListener(v -> {
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            loginUser(username, password);
        });

        registerButton.setOnClickListener(v -> startActivity(new Intent(Login.this, Register.class)));
    }

    private void loginUser(String username, String password) {
        // Validasi input
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(Login.this, "Username dan password harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Query untuk cek username dan password
        Query query = ambatronDB.orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot akunSnapshot : dataSnapshot.getChildren()) {
                        String dbPassword = akunSnapshot.child("password").getValue(String.class);

                        if (dbPassword != null) {
                            if (dbPassword.equals(password)) {
                                // Simpan username ke SharedPreferences
                                editor.putString("username", username);
                                editor.apply();

                                // Ambil data skin dari Firebase
                                String userId = akunSnapshot.getKey(); // Ambil ID pengguna
                                fetchSkinData(userId); // Ambil data skin

                                // Berhasil login, pindah ke MainActivity
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.putExtra("username", username); // Kirim username ke MainActivity
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Login.this, "Password salah", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    Toast.makeText(Login.this, "Username tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });
    }

    // Fungsi untuk mengambil status skin dari Firebase
    private void fetchSkinData(String userId) {
        DatabaseReference skinRef = FirebaseDatabase.getInstance().getReference("Koleksi_Skin").child(userId);

        skinRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot skinSnapshot : dataSnapshot.getChildren()) {
                        String skinId = skinSnapshot.getKey();
                        Boolean statusTerkunci = skinSnapshot.child("status_terkunci").getValue(Boolean.class);

                        // Logika untuk menampilkan skin berdasarkan status
                        if (statusTerkunci != null && !statusTerkunci) {
                            Log.d("SkinData", "Skin ID: " + skinId + " | Status Terkunci: " + statusTerkunci);
                            // Simpan informasi skin yang terunlock jika diperlukan
                            // Misalnya: simpan ke SharedPreferences atau tampilkan di UI
                        } else {
                            Log.d("SkinData", "Skin ID: " + skinId + " masih terkunci.");
                        }
                    }
                } else {
                    Log.d("SkinData", "Tidak ada data skin untuk pengguna ini.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("SkinData", "Error fetching skin data: " + databaseError.getMessage());
            }
        });
    }
}
