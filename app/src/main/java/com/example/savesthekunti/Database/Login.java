package com.example.savesthekunti.Database;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savesthekunti.Activity.MainActivity;
import com.example.savesthekunti.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login extends AppCompatActivity {

    private EditText usernameField, passwordField;
    private Button loginButton, registerButton;
    private ImageButton prevsBtn;
    private FirebaseFirestore firestore;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        // Inisialisasi Firestore
        firestore = FirebaseFirestore.getInstance();

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

        registerButton.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Login.this, Register.class));
            } catch (Exception e) {
                Log.e("Login", "Error launching Register activity", e);
                Toast.makeText(Login.this, "Terjadi kesalahan saat membuka halaman registrasi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fungsi untuk hash password menggunakan SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void loginUser(String username, String password) {
        // Validasi input
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(Login.this, "Username dan password harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hash password
        String hashedPassword = hashPassword(password);

        // Query ke Firestore untuk cek apakah username dan password cocok
        CollectionReference akunRef = firestore.collection("Akun");
        akunRef.whereEqualTo("username", username)
                .whereEqualTo("password", hashedPassword) // Hash password yang dicari
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String userId = document.getId();

                        // Simpan username ke SharedPreferences
                        editor.putString("username", username);
                        editor.putBoolean("isLoggedIn", true);

                        // Periksa status admin
                        Boolean isAdmin = document.getBoolean("isAdmin");
                        if (isAdmin != null && isAdmin) {
                            editor.putBoolean("isAdmin", true);
                        } else {
                            editor.putBoolean("isAdmin", false);
                        }
                        editor.apply();

                        // Ambil status skin dari Firestore dan simpan ke SharedPreferences
                        fetchUserSkins(username);

                        // Pindah ke MainActivity
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("isAdmin", true);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Login.this, "Login gagal, username atau password salah", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Fungsi untuk mengambil status skin dari Firestore dan menyimpannya ke SharedPreferences
    private void fetchUserSkins(String username) {
        firestore.collection("Akun").document(username).collection("Koleksi_Skin")
                .get()
                .addOnCompleteListener(task -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String skinId = document.getString("id_skin");
                            boolean isLocked = document.getBoolean("status_terkunci");

                            // Simpan status skin ke SharedPreferences
                            editor.putBoolean(skinId + "_locked", isLocked);
                        }
                    } else {
                        // Jika tidak ada skin yang ditemukan, set skin default ke unlocked
                        editor.putBoolean("blue_cosmos_locked", false); // Set blue_cosmos sebagai skin default
                    }
                    editor.apply(); // Simpan perubahan
                })
                .addOnFailureListener(e -> {
                    // Menangani kesalahan jika query gagal
                    Log.e("Login", "Error fetching skins: " + e.getMessage());
                    Toast.makeText(Login.this, "Error retrieving skin data.", Toast.LENGTH_SHORT).show();
                });
    }
}

