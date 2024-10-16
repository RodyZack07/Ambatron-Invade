package com.example.savesthekunti;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private EditText usernameField, passwordField;
    private Button loginButton, registerButton;
    private ImageButton prevsBtn;
    private DatabaseReference ambatronDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://ambatrondb-default-rtdb.asia-southeast1.firebasedatabase.app");
        ambatronDB = database.getReference("Akun");

        prevsBtn = findViewById(R.id.prevsBtn3);
        prevsBtn.setOnClickListener(view -> finish());

        usernameField = findViewById(R.id.usernameText);
        passwordField = findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.Login);
        registerButton = findViewById(R.id.createAccountButton);

        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> startActivity(new Intent(Login.this, Register.class)));
    }

    private void loginUser() {
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(Login.this, "Username atau password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = ambatronDB.child(username);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String storedPassword = dataSnapshot.child("password").getValue(String.class);
                    if (storedPassword != null && storedPassword.equals(password)) {
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.putExtra("username", username); // Kirim username ke MainActivity
                        Toast.makeText(Login.this, "Selamat datang, " + username + "!", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Login.this, "Password salah!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login.this, "Username tidak ditemukan!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Login.this, "Gagal mengambil data pengguna", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
