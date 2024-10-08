package com.example.savesthekunti;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class createAkun extends AppCompatActivity {

    private DBHelper dbHelper;
    private EditText usernameEditText, emailEditText, passwordEditText;
    private Button createAccountButton, saveProgressButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_akun);

        dbHelper = new DBHelper(this);
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        createAccountButton = findViewById(R.id.createAccountButton);
        saveProgressButton = findViewById(R.id.saveProgressButton);

        createAccountButton.setOnClickListener(view -> createAccount());
        saveProgressButton.setOnClickListener(view -> saveProgress());
    }

    private void createAccount() {
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        long akunId = dbHelper.addAkun(username, email, password);
        if (akunId != -1) {
            Toast.makeText(this, "Akun berhasil dibuat!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Gagal membuat akun!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProgress() {
        // Misalkan Anda ingin menyimpan progres level 1 dengan score 100
        int idAkun = 1; // Anda harus mendapatkan ID akun dari input pengguna atau dari database
        dbHelper.saveProgress(idAkun, 1, 100);
        Toast.makeText(this, "Progres berhasil disimpan!", Toast.LENGTH_SHORT).show();

        // Menampilkan progres yang telah disimpan
        Cursor cursor = dbHelper.getProgressByAkunId(idAkun);
        if (cursor.moveToFirst()) {
            do {
                int level = cursor.getInt(cursor.getColumnIndex("level"));
                int score = cursor.getInt(cursor.getColumnIndex("score"));
                String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));
                // Tampilkan informasi progres
                Toast.makeText(this, "Level: " + level + ", Score: " + score + ", Timestamp: " + timestamp, Toast.LENGTH_SHORT).show();
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
