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

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Initialize UI elements
        prevsBtn = findViewById(R.id.prevsBtn3);
        prevsBtn.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        usernameField = findViewById(R.id.usernameText);
        passwordField = findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.Login);
        registerButton = findViewById(R.id.createAccountButton);

        loginButton.setOnClickListener(v -> {
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            loginUser(username, password);
        });

        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, Register.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

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
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username dan password harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashedPassword = hashPassword(password);
        CollectionReference akunRef = firestore.collection("Akun");

        akunRef.whereEqualTo("username", username)
                .whereEqualTo("password", hashedPassword)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String userId = document.getId();

                        editor.putString("username", username);
                        editor.putBoolean("isLoggedIn", true);

                        Boolean isAdmin = document.getBoolean("isAdmin");
                        editor.putBoolean("isAdmin", isAdmin != null && isAdmin);
                        editor.apply();



                        fetchUserSkins(userId);

                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Login gagal, username atau password salah", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Login", "Error querying Firestore", e);
                    Toast.makeText(this, "Terjadi kesalahan saat login. Coba lagi.", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchUserSkins(String userId) {
        firestore.collection("Akun").document(userId).collection("Koleksi_Skin")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        for (DocumentSnapshot document : task.getResult()) {
                            String skinId = document.getString("id_skin");
                            boolean isLocked = document.getBoolean("status_terkunci");
                            editor.putBoolean(skinId + "_locked", isLocked);
                        }
                        editor.apply();
                    } else {
                        editor.putBoolean("blue_cosmos_locked", false);
                        editor.apply();
                    }
                })
                .addOnFailureListener(e -> Log.e("Login", "Error fetching skin data", e));
    }
}
