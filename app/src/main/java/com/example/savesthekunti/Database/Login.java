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

        // Initialize UI
        prevsBtn = findViewById(R.id.prevsBtn3);
        prevsBtn.setOnClickListener(view -> finish());

        usernameField = findViewById(R.id.usernameText);
        passwordField = findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.Login);
        registerButton = findViewById(R.id.createAccountButton);

        // Set OnClickListener for login and register buttons
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

    // Function to hash password using SHA-256
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
        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(Login.this, "Username dan password harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hash password
        String hashedPassword = hashPassword(password);

        // Query Firestore to check if username and password match
        CollectionReference akunRef = firestore.collection("Akun");
        akunRef.whereEqualTo("username", username)
                .whereEqualTo("password", hashedPassword) // Hash password being searched
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            String userId = document.getId();

                            // Save username to SharedPreferences
                            editor.putString("username", username);

                            editor.putBoolean("isLoggedIn", true);

                            // Check admin status
                            Boolean isAdmin = document.getBoolean("isAdmin");
                            if (isAdmin != null && isAdmin) {
                                editor.putBoolean("isAdmin", true);
                            } else {
                                editor.putBoolean("isAdmin", false);
                            }
                            editor.apply();

                            // Fetch skin status from Firestore and save to SharedPreferences
                            fetchUserSkins(username);

                            // Move to MainActivity
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("isAdmin", true);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Login gagal, username atau password salah", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle Firestore query errors
                        Log.e("Login", "Error querying Firestore: " + task.getException());
                        Toast.makeText(Login.this, "Terjadi kesalahan saat login. Silakan coba lagi.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Function to fetch skin status from Firestore and save it to SharedPreferences
    private void fetchUserSkins(String username) {
        firestore.collection("Akun").document(username).collection("Koleksi_Skin")
                .get()
                .addOnCompleteListener(task -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String skinId = document.getString("id_skin");
                            boolean isLocked = document.getBoolean("status_terkunci");

                            // Save skin status to SharedPreferences
                            editor.putBoolean(skinId + "_locked", isLocked);
                        }
                    } else {
                        // If no skins are found, set default skin to unlocked
                        editor.putBoolean("blue_cosmos_locked", false); // Set blue_cosmos as default skin
                    }
                    editor.apply(); // Save changes
                })
                .addOnFailureListener(e -> {
                    // Handle errors if the query fails
                    Log.e("Login", "Error fetching skins: " + e.getMessage());
                    Toast.makeText(Login.this, "Error retrieving skin data.", Toast.LENGTH_SHORT).show();
                });
    }
}
