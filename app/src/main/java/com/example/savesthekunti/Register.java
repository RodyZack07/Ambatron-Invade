package com.example.savesthekunti;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
                // Pengecekan apakah username sudah ada
                ambatronDB.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(getApplicationContext(), "Username sudah digunakan", Toast.LENGTH_SHORT).show();
                        } else {
                            // Menghash password sebelum menyimpannya
                            String hashedPassword = hashPassword(password);
                            // Menyimpan data ke Firebase di bawah child "Akun" dengan username sebagai key
                            DatabaseReference userRef = ambatronDB.child(username);
                            userRef.child("username").setValue(username);
                            userRef.child("email").setValue(email);
                            userRef.child("password").setValue(hashedPassword).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Setelah pendaftaran sukses, tambahkan data Profile dan Achievement
                                    createProfile(userRef);
                                    createAchievements(userRef);
                                    createSkin(userRef); // Menambahkan skin default

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
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

    // Method untuk meng-hash password
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
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

    // Method untuk membuat skin default di Firebase
    private void createSkin(DatabaseReference userRef) {
        DatabaseReference skinRef = userRef.child("Koleksi_Skin");

        // Skin pertama (Blue Cosmos) - tidak terkunci
        DatabaseReference skin1 = skinRef.child("blue_cosmos"); // Gunakan id_skin sebagai child
        skin1.child("id_skin").setValue("blue_cosmos");
        skin1.child("status_terkunci").setValue(false);  // Skin ini tidak terkunci
        skin1.child("created_at").setValue(System.currentTimeMillis());
        skin1.child("updated_at").setValue(System.currentTimeMillis());

        // Skin kedua (Retro Sky) - terkunci
        DatabaseReference skin2 = skinRef.child("retro_sky"); // Gunakan id_skin sebagai child
        skin2.child("id_skin").setValue("retro_sky");
        skin2.child("status_terkunci").setValue(true);  // Skin ini terkunci
        skin2.child("created_at").setValue(System.currentTimeMillis());
        skin2.child("updated_at").setValue(System.currentTimeMillis());

        // Skin Ketiga (Wings of Justice)
        DatabaseReference skin3 = skinRef.child("wing_of_justice");
        skin3.child("id_skin").setValue("wing_of_justice");
        skin3.child("status_terkunci").setValue(true);
        skin3.child("created_at").setValue(System.currentTimeMillis());
        skin3.child("updated_at").setValue(System.currentTimeMillis());
    }
}
