package com.example.savesthekunti.Database;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.savesthekunti.Database.Login;
import com.example.savesthekunti.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private EditText usernameField, emailField, passwordField, passwordConfirmField;
    private Button registerButton;
    private ImageButton prevsBtn;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        // Inisialisasi Firebase Authentication dan Firestore
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Inisialisasi UI
        prevsBtn = findViewById(R.id.prevsBtn3);
        prevsBtn.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        usernameField = findViewById(R.id.usernameText);
        emailField = findViewById(R.id.emailText);
        passwordField = findViewById(R.id.passwordText);
        passwordConfirmField = findViewById(R.id.passwordTextConfirm);
        registerButton = findViewById(R.id.regisnow);

        // Klik listener untuk tombol registrasi
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
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(Register.this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(Register.this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(Register.this, "Password dan konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show();
            return;
        }

        // Daftar pengguna menggunakan Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Kirim email verifikasi
                            user.sendEmailVerification().addOnCompleteListener(verificationTask -> {
                                if (verificationTask.isSuccessful()) {
                                    Toast.makeText(Register.this, "Verifikasi email telah dikirim. Silakan cek email Anda.", Toast.LENGTH_SHORT).show();
                                    // Simpan data pengguna ke Firestore
                                    saveUserToFirestore(username, email, hashPassword(password));
                                } else {
                                    Toast.makeText(Register.this, "Gagal mengirim verifikasi email: " + verificationTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(Register.this, "Registrasi gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(String username, String email, String hashedPassword) {
        Map<String, Object> akunData = new HashMap<>();
        akunData.put("username", username);
        akunData.put("email", email);
        akunData.put("password", hashedPassword);
        akunData.put("isAdmin", false);
        akunData.put("created_at", System.currentTimeMillis());
        akunData.put("updated_at", System.currentTimeMillis());
        akunData.put("score", 0);
        akunData.put("currency", 10); // Tambahkan mata uang default

        firestore.collection("Akun").document(username)
                .set(akunData)
                .addOnSuccessListener(aVoid -> {
                    setDefaultSkin(username);
                    setDefaultAchievements(username);
                    setDefaultScore(username);
                    setDefaultLevels(username);

                    Log.d("Register", "Akun berhasil didaftarkan dengan username sebagai ID");
                    Toast.makeText(Register.this, "Registrasi berhasil, silakan verifikasi email Anda", Toast.LENGTH_SHORT).show();
                    // Logout untuk mencegah akses tanpa verifikasi dan arahkan ke Login
                    auth.signOut();
                    startActivity(new Intent(Register.this, Login.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("Register", "Gagal menyimpan akun", e);
                    Toast.makeText(Register.this, "Registrasi gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setDefaultAchievements(String userId) {
        CollectionReference achievementRef = firestore.collection("Akun").document(userId).collection("Achievement");

        Map<String, Object> achievement1 = new HashMap<>();
        achievement1.put("id", "achv_001");
        achievement1.put("name", "Monster Hunter");
        achievement1.put("desc", "Defeat 50 monsters.");
        achievement1.put("rules", "Defeat at least 50 monsters to unlock.");
        achievement1.put("isGet", false);
        achievement1.put("monster_defeated", 0);
        achievement1.put("highscore", 0);
        achievementRef.document("achv_001").set(achievement1);

        Map<String, Object> achievement2 = new HashMap<>();
        achievement2.put("id", "achv_002");
        achievement2.put("name", "Score Master");
        achievement2.put("desc", "Reach a highscore of 1000.");
        achievement2.put("rules", "Get a score of 1000 or higher to unlock.");
        achievement2.put("isGet", false);
        achievement2.put("monster_defeated", 0);
        achievement2.put("highscore", 0);
        achievementRef.document("achv_002").set(achievement2);

    }

    private void setDefaultSkin(String userId) {
        CollectionReference skinRef = firestore.collection("Akun").document(userId).collection("Koleksi_Skin");

        Map<String, Object> defaultSkinData = new HashMap<>();
        defaultSkinData.put("id_skin", "blue_cosmos");
        defaultSkinData.put("status_terkunci", false);
        defaultSkinData.put("is_unlocked", true);
        defaultSkinData.put("created_at", System.currentTimeMillis());
        defaultSkinData.put("updated_at", System.currentTimeMillis());

        skinRef.document("blue_cosmos").set(defaultSkinData);

        addLockedSkin(skinRef, "retro_sky");
        addLockedSkin(skinRef, "wing_of_justice");
        addLockedSkin(skinRef, "x56_core");

    }

    private void addLockedSkin(CollectionReference skinRef, String skinId) {
        Map<String, Object> lockedSkinData = new HashMap<>();
        lockedSkinData.put("id_skin", skinId);
        lockedSkinData.put("status_terkunci", true);
        lockedSkinData.put("is_unlocked", false);
        lockedSkinData.put("created_at", System.currentTimeMillis());
        lockedSkinData.put("updated_at", System.currentTimeMillis());

        skinRef.document(skinId).set(lockedSkinData);
    }

    private void setDefaultScore(String userId) {
        // Mengakses koleksi "Akun" dan dokumen berdasarkan userId
        CollectionReference scoreRef = firestore.collection("Akun").document(userId).collection("Score");

        // Membuat data default untuk score
        Map<String, Object> scoreData = new HashMap<>();
        scoreData.put("highscore", 0); // Misalnya, nilai awal highscore adalah 0
        scoreData.put("current_score", 0); // Nilai skor saat ini
        scoreData.put("level", 1); // Level awal
        scoreData.put("created_at", System.currentTimeMillis());
        scoreData.put("updated_at", System.currentTimeMillis());

        // Menyimpan data default ke dalam koleksi "Score" untuk pengguna
        scoreRef.document("default_score").set(scoreData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Register", "Default score berhasil diset.");
                })
                .addOnFailureListener(e -> {
                    Log.e("Register", "Gagal menyimpan default score", e);
                });
    }

    private void setDefaultLevels(String userId) {
        // Akses subkoleksi "Levels" di dalam koleksi "Akun"
        CollectionReference levelsRef = firestore.collection("Akun").document(userId).collection("Levels");

        Map<String, Object> levelData= new HashMap<>();
        levelData.put("isLevelCompleted1", true);
        levelData.put("isLevelCompleted2", false);
        levelData.put("isLevelCompleted3", false);
        levelData.put("isLevelCompleted4", false);
        levelData.put("isLevelCompleted5", false);
        levelData.put("isLevelCompleted6", false);
        levelData.put("isLevelCompleted7", false);
        levelData.put("isLevelCompleted8", false);
        levelData.put("isLevelCompleted9", false);
        levelData.put("isLevelCompleted10", false);
        levelData.put("isLevelCompleted11", false);
        levelData.put("isLevelCompleted12", false);
        levelData.put("isLevelCompleted13", false);
        levelData.put("isLevelCompleted14", false);
        levelData.put("isLevelCompleted15", false);

        levelsRef.document(userId).set(levelData);

    }
}