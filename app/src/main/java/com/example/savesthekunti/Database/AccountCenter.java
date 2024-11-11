package com.example.savesthekunti.Database;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.savesthekunti.Activity.MainActivity;
import com.example.savesthekunti.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountCenter extends AppCompatActivity {

    private PopupWindow exitPopupWindow;
    private TextView nicknameTextView, createdAtTextView;
    private ImageView profileImageView;
    private FirebaseFirestore firestore;
    private String username;
    private String email;
    private ImageButton prevsbtn;
    private Button logoutBtn;
    private FirebaseAuth mAuth; // Add FirebaseAuth instance



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_center);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Inisialisasi tombol kembali
        prevsbtn = findViewById(R.id.backbutton);
        prevsbtn.setOnClickListener(view -> finish());

        // Inisialisasi tombol logout
        logoutBtn = findViewById(R.id.logout_button);
        logoutBtn.setOnClickListener(view ->  showExitPopup(view));
//        logoutBtn.setOnClickListener(view -> signOut()); // Call signOut method on click



        // Inisialisasi Firestore
        firestore = FirebaseFirestore.getInstance();

        // Ambil username dan email dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);
        email = sharedPreferences.getString("email", null);  // Ambil email dari SharedPreferences

        if (username != null && email != null) {
            // Set email ke TextView
            createdAtTextView.setText(email);  // Tampilkan email di createdAtTextView

            // Panggil metode untuk load profil
            loadUserProfile();
            loadUserEmail();
        } else {
            Toast.makeText(AccountCenter.this, "Username atau email tidak ditemukan!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showExitPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.account_center_logout_popup, null);

        int popupWidth = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int popupHeight = getResources().getDimensionPixelSize(R.dimen.popup_height);

        exitPopupWindow = new PopupWindow(popupView, popupWidth, popupHeight, true);
        exitPopupWindow.setAnimationStyle(R.style.PopupAnimation);
        exitPopupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        ImageButton confirmExit = popupView.findViewById(R.id.imageExit);
        ImageButton cancelExit = popupView.findViewById(R.id.imageYes);
        confirmExit.setOnClickListener(v -> signOut());

        cancelExit.setOnClickListener(v -> exitPopupWindow.dismiss());
    }

    private void loadUserProfile() {
        // Ambil data dari Firestore berdasarkan username sebagai ID
        DocumentReference akunRef = firestore.collection("Akun").document(username);
        akunRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Mengambil data profil
                    String username = document.getString("username");
                    String email = document.getString("email");

                    // Tampilkan data di TextView
                    nicknameTextView.setText(username);
                    createdAtTextView.setText(email);
                } else {
                    Toast.makeText(AccountCenter.this, "Profil tidak ditemukan!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AccountCenter.this, "Gagal memuat profil: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserEmail() {
        // Ambil data dari Firestore berdasarkan username sebagai ID
        DocumentReference akunRef = firestore.collection("Akun").document(email);
        akunRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Mengambil data profil
                    // Tampilkan data di TextView
                    createdAtTextView.setText(email);
                }
            }
        });
    }

    private void signOut() {
        mAuth.signOut(); // Sign out from Firebase Authentication
        // Clear SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Navigate to the login activity
        Intent intent = new Intent(AccountCenter.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close the current activity
    }
}

