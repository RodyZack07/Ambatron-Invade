package com.example.savesthekunti;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectFighterActivity extends AppCompatActivity {

    private VideoView videoBackground;
    private int videoPosition;

    // PESAWAT
    private ImageView spaceShip;
    private String[] fighterIDs = {"blue_cosmos", "retro_sky"}; // ID untuk setiap skin
    private int currentSkinIndex = 0;
    private ArrayList<Boolean> userSkins = new ArrayList<>(); // Untuk menyimpan status terkunci dari setiap skin

    // Gembok
    private int lockedSkinImage = R.drawable.skin_activity_key;

    private DatabaseReference userSkinsRef;
    private String username; // Simpan username di sini

    // SharedPreferences
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_fighter);

        // Inisialisasi VideoView untuk background
        videoBackground = findViewById(R.id.selectBg);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.skin_selector);
        videoBackground.setVideoURI(videoUri);
        videoBackground.setOnPreparedListener(mp -> mp.setLooping(true));
        videoBackground.start();

        // Inisialisasi Views
        ImageButton prevsBtn1 = findViewById(R.id.prevsBtn2);
        spaceShip = findViewById(R.id.ship_img);
        ImageButton prevsBtn = findViewById(R.id.prevsBtn);
        ImageButton nextBtn = findViewById(R.id.nextBtn);
        ImageButton selectBtn = findViewById(R.id.selectBtn);
        Button unlockSkin = findViewById(R.id.skinUnlock);

        // Ambil username dari intent atau login session
        username = getIntent().getStringExtra("username");
        if (username == null) {
            // Jika username tidak ditemukan di intent, gunakan SharedPreferences sebagai fallback
            sharedPreferences = getSharedPreferences("LoginSession", MODE_PRIVATE);
            username = sharedPreferences.getString("username", "default_user");
        }

        // Firebase reference untuk skin yang dimiliki user
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://ambatrondb-default-rtdb.asia-southeast1.firebasedatabase.app");
        userSkinsRef = database.getReference("Akun").child(username).child("Koleksi_Skin");

        // Inisialisasi SharedPreferences untuk skin status
        sharedPreferences = getSharedPreferences("UserSkins", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Ambil data skin dari Firebase
        fetchUserSkins();

        // Listener untuk tombol sebelumnya dan selanjutnya
        prevsBtn.setOnClickListener(v -> prevsFighter());
        nextBtn.setOnClickListener(v -> nextFighter());
        selectBtn.setOnClickListener(v -> selectGame());
        prevsBtn1.setOnClickListener(view -> finish());

        // Listener untuk tombol unlock
        unlockSkin.setOnClickListener(v -> unlockCurrentSkin());
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoBackground.seekTo(videoPosition);
        videoBackground.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPosition = videoBackground.getCurrentPosition();
        videoBackground.pause();
    }

    private void fetchUserSkins() {
        userSkinsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userSkins.clear(); // Clear untuk menghindari data duplikat
                for (String fighterID : fighterIDs) {
                    userSkins.add(false);
                }

                if (snapshot.exists()) {
                    for (DataSnapshot skinSnapshot : snapshot.getChildren()) {
                        String skinId = skinSnapshot.child("id_skin").getValue(String.class);
                        Boolean isLocked = skinSnapshot.child("status_terkunci").getValue(Boolean.class);

                        if (skinId != null && isLocked != null) {
                            int index = getSkinIndex(skinId);
                            if (index != -1) {
                                userSkins.set(index, !isLocked); // false -> skin terbuka
                                saveSkinStatusToSharedPreferences(skinId, !isLocked);
                            }
                        }
                    }
                } else {
                    Log.d("FetchUserSkins", "Tidak ada skin ditemukan untuk pengguna ini");
                }
                updateFighterView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FetchUserSkins", "Gagal memuat skin: " + error.getMessage());
            }
        });
    }

    private void saveSkinStatusToSharedPreferences(String skinId, boolean isUnlocked) {
        editor.putBoolean(skinId, isUnlocked);
        editor.apply();
    }

    private int getSkinIndex(String skinId) {
        for (int i = 0; i < fighterIDs.length; i++) {
            if (fighterIDs[i].equals(skinId)) {
                return i;
            }
        }
        return -1;
    }

    private void updateFighterView() {
        String currentSkinID = fighterIDs[currentSkinIndex];
        if (userSkins.get(currentSkinIndex)) {
            spaceShip.setImageResource(getResources().getIdentifier(currentSkinID, "drawable", getPackageName()));
        } else {
            spaceShip.setImageResource(lockedSkinImage);
        }
    }

    private void nextFighter() {
        currentSkinIndex = (currentSkinIndex + 1) % fighterIDs.length;
        FighterSwitchAnimation();
    }

    private void prevsFighter() {
        currentSkinIndex = (currentSkinIndex - 1 + fighterIDs.length) % fighterIDs.length;
        FighterSwitchAnimation();
    }

    private void selectGame() {
        if (userSkins.get(currentSkinIndex)) {
            Intent intent = new Intent(SelectFighterActivity.this, SelectLevelActivity.class);
            intent.putExtra("selectedShipIndex", currentSkinIndex);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Skin ini terkunci!", Toast.LENGTH_SHORT).show();
        }
    }

    private void FighterSwitchAnimation() {
        AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(200);

        AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(200);

        spaceShip.startAnimation(fadeOut);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                updateFighterView();
                spaceShip.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void unlockCurrentSkin() {
        String selectedSkinID = fighterIDs[currentSkinIndex];

        // Cek apakah skin terkunci
        if (!userSkins.get(currentSkinIndex)) { // Jika skin terkunci
            // Ubah status terkunci di Firebase menjadi false
            userSkinsRef.child(selectedSkinID).child("status_terkunci").setValue(false)
                    .addOnSuccessListener(aVoid -> {
                        // Update status terkunci di aplikasi
                        userSkins.set(currentSkinIndex, true); // Set status skin menjadi tidak terkunci
                        saveSkinStatusToSharedPreferences(selectedSkinID, true); // Simpan status tidak terkunci

                        // Update tampilan
                        updateFighterView();
                        Toast.makeText(SelectFighterActivity.this, "Skin berhasil dibuka!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SelectFighterActivity.this, "Gagal membuka skin!", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Skin ini sudah terbuka!", Toast.LENGTH_SHORT).show();
        }
    }
}
