package com.example.savesthekunti;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private EditText usernameText, emailText, passwordText;
    private Button createAccountButton;
    private DatabaseReference ambatronDB;
    private ImageButton prevsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        // Inisialisasi views
        usernameText = findViewById(R.id.usernameText);
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        createAccountButton = findViewById(R.id.regisnow);

        // Inisialisasi Firebase Database Reference
        ambatronDB = FirebaseDatabase.getInstance().getReference();

        // Inisialisasi tombol kembali
        prevsBtn = findViewById(R.id.prevsBtn3);
        prevsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Menutup aktivitas saat ini dan kembali ke aktivitas sebelumnya
            }
        });

        // Set OnClickListener untuk createAccountButton
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ambil data dari EditText
                String username = usernameText.getText().toString().trim();
                String email = emailText.getText().toString().trim();
                String password = passwordText.getText().toString().trim();

                // Validasi jika ada data yang kosong
                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Ada data yang masih kosong", Toast.LENGTH_SHORT).show();
                } else {
                    // Menyimpan data ke Firebase di bawah child "Akun" dengan username sebagai key
                    DatabaseReference userRef = ambatronDB.child("Akun").child(username);
                    userRef.child("username").setValue(username);
                    userRef.child("email").setValue(email);
                    userRef.child("password").setValue(password);

                    // Tampilkan pesan sukses
                    Toast.makeText(getApplicationContext(), "Pendaftaran Berhasil", Toast.LENGTH_SHORT).show();

                    // Kosongkan field setelah pendaftaran
                    usernameText.setText("");
                    emailText.setText("");
                    passwordText.setText("");

                    // Redirect ke MainActivity setelah pendaftaran berhasil
                    startActivity(new Intent(Register.this, MainActivity.class));
                }
            }
        });
    }
}
