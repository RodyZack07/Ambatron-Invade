package com.example.savesthekunti;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailField, passwordField;
    private Button loginButton, registerButton;
    private ImageButton prevsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        // Inisialisasi tombol kembali
        prevsBtn = findViewById(R.id.prevsBtn3);
        prevsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Menutup aktivitas saat ini dan kembali ke aktivitas sebelumnya
            }
        });

        // Inisialisasi Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Inisialisasi view
        emailField = findViewById(R.id.emailText);
        passwordField = findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.Login);
        registerButton = findViewById(R.id.createAccountButton);

        // Login button click
        loginButton.setOnClickListener(v -> loginUser());

        // Pindah ke halaman registrasi
        registerButton.setOnClickListener(v -> startActivity(new Intent(Login.this, Register.class)));
    }

    private void loginUser() {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    startActivity(new Intent(Login.this, Admin.class));
                    Toast.makeText(Login.this, "Login berhasil!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Login.this, "Login gagal!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
