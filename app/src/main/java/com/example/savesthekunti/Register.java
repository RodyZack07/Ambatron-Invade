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

    private EditText usernameText, emailText, passwordText, confirmPasswordText;
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
        confirmPasswordText = findViewById(R.id.passwordTextConfirm);
        createAccountButton = findViewById(R.id.regisnow);
        prevsBtn = findViewById(R.id.prevsBtn3);

        // Inisialisasi Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://ambatrondb-default-rtdb.asia-southeast1.firebasedatabase.app");
        ambatronDB = database.getReference("Akun"); // Set referensi untuk child "Akun"

        // Inisialisasi tombol kembali
        prevsBtn.setOnClickListener(view -> finish());

        // Set OnClickListener untuk createAccountButton
        createAccountButton.setOnClickListener(view -> {
            // Ambil data dari EditText
            String username = usernameText.getText().toString().trim();
            String email = emailText.getText().toString().trim();
            String password = passwordText.getText().toString().trim();
            String confirmPassword = confirmPasswordText.getText().toString().trim();

            // Validasi jika ada data yang kosong
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Ada data yang masih kosong", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(getApplicationContext(), "Password dan Konfirmasi Password tidak cocok", Toast.LENGTH_SHORT).show();
            } else {
                // Menyimpan data ke Firebase di bawah child "Akun" dengan username sebagai key
                DatabaseReference userRef = ambatronDB.child(username);
                userRef.child("username").setValue(username);
                userRef.child("email").setValue(email);
                userRef.child("password").setValue(password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Setelah pendaftaran sukses, tambahkan data Profile dan Achievement
                        createProfile(userRef);
                        createAchievements(userRef);

                        // Tampilkan pesan sukses
                        Toast.makeText(getApplicationContext(), "Pendaftaran Berhasil", Toast.LENGTH_SHORT).show();

                        // Kosongkan field setelah pendaftaran
                        clearFields();

                        // Redirect ke MainActivity setelah pendaftaran berhasil
                        startActivity(new Intent(Register.this, MainActivity.class));
                    } else {
                        // Jika gagal, tampilkan error
                        Toast.makeText(getApplicationContext(), "Gagal mendaftar, silakan coba lagi", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // Method untuk mengosongkan field input
    private void clearFields() {
        usernameText.setText("");
        emailText.setText("");
        passwordText.setText("");
        confirmPasswordText.setText("");
    }

    // Method untuk membuat Profile di Firebase
    private void createProfile(DatabaseReference userRef) {
        DatabaseReference profileRef = userRef.child("Profile");
        profileRef.child("id_profile").setValue(userRef.getKey()); // Menggunakan username sebagai id_profile
        profileRef.child("photo_profile").setValue("default_photo_url"); // Placeholder foto profil
        profileRef.child("created_at").setValue(System.currentTimeMillis());
        profileRef.child("updated_at").setValue(System.currentTimeMillis());
        profileRef.child("username").setValue(userRef.getKey());
    }

    // Method untuk membuat Achievement di Firebase
    private void createAchievements(DatabaseReference userRef) {
        DatabaseReference achievementsRef = userRef.child("Achievement");

        // Contoh achievement pertama
        DatabaseReference achievement1 = achievementsRef.push();
        achievement1.child("id_achievement").setValue(achievement1.getKey());
        achievement1.child("nama_achievement").setValue("First Login");
        achievement1.child("deskripsi_achievement").setValue("Login for the first time");
        achievement1.child("created_at").setValue(System.currentTimeMillis());
        achievement1.child("updated_at").setValue(System.currentTimeMillis());

        // Achievement kedua (bisa ditambah sesuai kebutuhan)
        DatabaseReference achievement2 = achievementsRef.push();
        achievement2.child("id_achievement").setValue(achievement2.getKey());
        achievement2.child("nama_achievement").setValue("First Game Played");
        achievement2.child("deskripsi_achievement").setValue("Complete your first game");
        achievement2.child("created_at").setValue(System.currentTimeMillis());
        achievement2.child("updated_at").setValue(System.currentTimeMillis());
    }
}
