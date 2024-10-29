package com.example.savesthekunti;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private EditText usernameField, emailField, passwordField, passwordConfirmField;
    private Button registerButton;
    private ImageButton prevsBtn;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        // Inisialisasi Firestore
        firestore = FirebaseFirestore.getInstance();

        // Inisialisasi UI
        prevsBtn = findViewById(R.id.prevsBtn3);
        prevsBtn.setOnClickListener(view -> finish());

        usernameField = findViewById(R.id.usernameText);
        emailField = findViewById(R.id.emailText);
        passwordField = findViewById(R.id.passwordText);
        passwordConfirmField = findViewById(R.id.passwordTextConfirm);
        registerButton = findViewById(R.id.regisnow);

        // Set OnClickListener untuk tombol register
        registerButton.setOnClickListener(v -> {
            String username = usernameField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String confirmPassword = passwordConfirmField.getText().toString().trim();
            registerUser(username, email, password, confirmPassword);
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

    private void registerUser(String username, String email, String password, String confirmPassword) {
        // Validasi input
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(Register.this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(Register.this, "Password dan konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hash password
        String hashedPassword = hashPassword(password);

        // Buat struktur data akun baru
        Map<String, Object> akunData = new HashMap<>();
        akunData.put("username", username);
        akunData.put("email", email);
        akunData.put("password", hashedPassword);
        akunData.put("created_at", System.currentTimeMillis());
        akunData.put("updated_at", System.currentTimeMillis());
        akunData.put("score", 0); // Set default score untuk pengguna baru

        // Simpan data akun dengan username sebagai ID
        firestore.collection("Akun").document(username)
                .set(akunData)
                .addOnSuccessListener(aVoid -> {
                    // Jika berhasil disimpan
                    Log.d("Register", "Akun berhasil didaftarkan dengan username sebagai ID");
                    Toast.makeText(Register.this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();

                    // Set skin default untuk pengguna baru
                    setDefaultSkin(username);

                    // Berhasil register, pindah ke MainActivity
                    Intent intent = new Intent(Register.this, MainActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Jika ada kesalahan saat menyimpan
                    Log.e("Register", "Gagal menyimpan akun", e);
                    Toast.makeText(Register.this, "Registrasi gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Fungsi untuk mengatur skin default saat registrasi
    private void setDefaultSkin(String userId) {
        CollectionReference skinRef = firestore.collection("Akun").document(userId).collection("Koleksi_Skin");

        // Skin default yang tidak terkunci
        Map<String, Object> defaultSkinData = new HashMap<>();
        defaultSkinData.put("id_skin", "blue_cosmos");
        defaultSkinData.put("status_terkunci", false); // Skin default tidak terkunci
        defaultSkinData.put("created_at", System.currentTimeMillis());
        defaultSkinData.put("updated_at", System.currentTimeMillis());

        // Tambahkan skin default
        skinRef.document("blue_cosmos").set(defaultSkinData)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(Register.this, "Gagal mengatur skin default: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Skin lainnya yang terkunci
        Map<String, Object> lockedSkinData1 = new HashMap<>();
        lockedSkinData1.put("id_skin", "retro_sky");
        lockedSkinData1.put("status_terkunci", true); // Skin terkunci
        lockedSkinData1.put("created_at", System.currentTimeMillis());
        lockedSkinData1.put("updated_at", System.currentTimeMillis());

        skinRef.document("retro_sky").set(lockedSkinData1);

        // Tambahkan skin lain yang terkunci, contoh untuk skin kedua
        Map<String, Object> lockedSkinData2 = new HashMap<>();
        lockedSkinData2.put("id_skin", "wing_of_justice");
        lockedSkinData2.put("status_terkunci", true);
        lockedSkinData2.put("created_at", System.currentTimeMillis());
        lockedSkinData2.put("updated_at", System.currentTimeMillis());

        skinRef.document("wing_of_justice").set(lockedSkinData2);
    }

    // Fungsi untuk meng-unlock skin
    public void unlockSkin(String skinId) {
        String username = usernameField.getText().toString(); // Dapatkan username dari input

        // Referensi ke koleksi skin untuk pengguna
        DocumentReference skinRef = firestore.collection("Akun").document(username)
                .collection("Koleksi_Skin").document(skinId);

        // Update status terkunci menjadi false
        skinRef.update("status_terkunci", false)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UnlockSkin", "Skin berhasil di-unlock");
                    Toast.makeText(this, "Skin berhasil di-unlock!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("UnlockSkin", "Gagal meng-unlock skin", e);
                    Toast.makeText(this, "Gagal meng-unlock skin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
