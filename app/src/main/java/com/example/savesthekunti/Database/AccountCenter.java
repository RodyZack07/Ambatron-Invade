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
    private Button logoutBtn, deleteAccountBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_center);

        // Inisialisasi Firebase Auth dan Firestore
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Inisialisasi elemen UI

        prevsbtn = findViewById(R.id.backbutton);
        logoutBtn = findViewById(R.id.logout_button);
        deleteAccountBtn = findViewById(R.id.btn_delete_account);

        // Tombol kembali
        prevsbtn.setOnClickListener(view -> finish());

        // Tombol logout
        logoutBtn.setOnClickListener(view -> showExitPopup(view));

        // Tombol delete account
        deleteAccountBtn.setOnClickListener(view -> showDeleteAccountPopup(view));

        // Ambil username dan email dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);
        email = sharedPreferences.getString("email", null);

        if (username != null && email != null) {
            createdAtTextView.setText(email);
            loadUserProfile();
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

    private void showDeleteAccountPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.account_center_delete, null);

        int popupWidth = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int popupHeight = getResources().getDimensionPixelSize(R.dimen.popup_height);

        exitPopupWindow = new PopupWindow(popupView, popupWidth, popupHeight, true);
        exitPopupWindow.setAnimationStyle(R.style.PopupAnimation);
        exitPopupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        Button confirmDelete = popupView.findViewById(R.id.imageExit);
        Button cancelDelete = popupView.findViewById(R.id.imageYes);

        confirmDelete.setOnClickListener(v -> deleteAccount());
        cancelDelete.setOnClickListener(v -> exitPopupWindow.dismiss());
    }

    private void deleteAccount() {
        // Hapus data pengguna dari Firestore
        DocumentReference userRef = firestore.collection("Akun").document(username);
        userRef.delete()
                .addOnSuccessListener(aVoid -> {
                    if (mAuth.getCurrentUser() != null) {
                        mAuth.getCurrentUser().delete()
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(AccountCenter.this, "Akun berhasil dihapus.", Toast.LENGTH_SHORT).show();
                                    signOut();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(AccountCenter.this, "Error deleting account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(AccountCenter.this, "Pengguna tidak masuk.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AccountCenter.this, "Error deleting account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadUserProfile() {
        DocumentReference akunRef = firestore.collection("Akun").document(username);
        akunRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String username = document.getString("username");
                    String email = document.getString("email");
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

    private void signOut() {
        mAuth.signOut();
        SharedPreferences sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(AccountCenter.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
