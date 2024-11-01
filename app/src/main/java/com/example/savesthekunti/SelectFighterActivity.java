package com.example.savesthekunti;

import android.content.Intent;
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

    private ImageView spaceShip;
    private ImageView lockOverlay; // Overlay untuk gembok
    private String[] fighterIDs = {"blue_cosmos", "retro_sky", "wing_of_justice"};
    private ArrayList<String> ownedSkins = new ArrayList<>(); // ArrayList untuk menyimpan skin yang dimiliki
    private int currentSkinIndex = 0;
    private Button unlockSkin;

    private DatabaseReference userSkinsRef;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_fighter);

        videoBackground = findViewById(R.id.selectBg);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.skin_selector);
        videoBackground.setVideoURI(videoUri);
        videoBackground.setOnPreparedListener(mp -> mp.setLooping(true));
        videoBackground.start();

        ImageButton prevsBtn1 = findViewById(R.id.prevsBtn2);
        spaceShip = findViewById(R.id.ship_img);
        lockOverlay = findViewById(R.id.lockOverlay);
        ImageButton prevsBtn = findViewById(R.id.prevsBtn);
        ImageButton nextBtn = findViewById(R.id.nextBtn);
        ImageButton selectBtn = findViewById(R.id.selectBtn);

        // Inisialisasi tombol unlockSkin
        unlockSkin = findViewById(R.id.skinUnlock); // Pastikan ID ini ada di layout XML

        username = getIntent().getStringExtra("username");
        String koleksiSkin = getIntent().getStringExtra("koleksiSkin");
        if (koleksiSkin == null) {
            Log.d("SelectFighterActivity", "Skin tidak ada.");
            koleksiSkin = "default_user";
        }

        if (username == null) {
            Log.d("SelectFighterActivity", "Username tidak ditemukan.");
            username = "default_user";
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://ambatrondb-default-rtdb.asia-southeast1.firebasedatabase.app");
        userSkinsRef = database.getReference("Akun").child(koleksiSkin).child("Koleksi_Skin");

        fetchUserSkins();

        prevsBtn.setOnClickListener(v -> prevsFighter());
        nextBtn.setOnClickListener(v -> nextFighter());
        selectBtn.setOnClickListener(v -> selectGame());
        prevsBtn1.setOnClickListener(view -> finish());

        unlockSkin.setOnClickListener(v -> {
            unlockSkin.setEnabled(false); // Disable the button while unlocking
            unlockCurrentSkin();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoBackground.seekTo(videoPosition);
        videoBackground.start();

        fetchUserSkins();
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
                ownedSkins.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot skinSnapshot : snapshot.getChildren()) {
                        String skinId = skinSnapshot.child("id_skin").getValue(String.class);
                        Boolean isLocked = skinSnapshot.child("status_terkunci").getValue(Boolean.class);

                        // Memastikan skinId dan isLocked tidak null
                        if (skinId != null && isLocked != null && !isLocked) {
                            ownedSkins.add(skinId);
                        }
                    }
                } else {
                    Log.d("FetchUserSkins", "Tidak ada skin ditemukan untuk pengguna ini.");
                }
                updateFighterView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FetchUserSkins", "Gagal memuat skin: " + error.getMessage());
                Toast.makeText(SelectFighterActivity.this, "Gagal memuat skin. Silakan coba lagi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFighterView() {
        String currentSkinID = fighterIDs[currentSkinIndex];

        // Mendapatkan ID drawable berdasarkan nama skin
        int skinDrawableId = getResources().getIdentifier(currentSkinID, "drawable", getPackageName());

        // Log untuk debug
        Log.d("UpdateFighterView", "Current Skin ID: " + currentSkinID);
        Log.d("UpdateFighterView", "Drawable ID: " + skinDrawableId);

        // Mengecek apakah skin tersedia di daftar ownedSkins
        if (ownedSkins.contains(currentSkinID)) {
            if (skinDrawableId != 0) {
                spaceShip.setImageResource(skinDrawableId);
                lockOverlay.setVisibility(View.GONE); // Sembunyikan overlay gembok
                Log.d("UpdateFighterView", "Menampilkan skin: " + currentSkinID);
            } else {
                Log.e("UpdateFighterView", "Skin drawable " + currentSkinID + " tidak ditemukan di drawable resources.");
                Toast.makeText(this, "Skin " + currentSkinID + " tidak ditemukan!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Jika skin terkunci, tampilkan gambar gembok

            lockOverlay.setVisibility(View.VISIBLE); // Tampilkan overlay gembok
            Log.d("UpdateFighterView", "Menampilkan gembok untuk skin: " + currentSkinID);
        }

        spaceShip.invalidate();
    }

    private void nextFighter() {
        if (fighterIDs.length > 0) {
            currentSkinIndex = (currentSkinIndex + 1) % fighterIDs.length;
            FighterSwitchAnimation();
        }
    }

    private void prevsFighter() {
        if (fighterIDs.length > 0) {
            currentSkinIndex = (currentSkinIndex - 1 + fighterIDs.length) % fighterIDs.length;
            FighterSwitchAnimation();
        }
    }

    private void selectGame() {
        if (ownedSkins.contains(fighterIDs[currentSkinIndex])) {
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
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                updateFighterView();
                spaceShip.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void unlockCurrentSkin() {
        String selectedSkinID = fighterIDs[currentSkinIndex];
        Log.d("UnlockSkin", "Selected Skin ID: " + selectedSkinID); // Log untuk memeriksa ID yang dipilih

        userSkinsRef.child(selectedSkinID).child("status_terkunci").setValue(false)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UnlockSkin", "Skin " + selectedSkinID + " berhasil dibuka.");
                    Toast.makeText(this, "Skin berhasil dibuka!", Toast.LENGTH_SHORT).show();
                    fetchUserSkins();
                })
                .addOnFailureListener(e -> {
                    Log.e("UnlockSkin", "Error: " + e.getMessage()); // Log kesalahan
                    Toast.makeText(this, "Gagal membuka skin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnCompleteListener(task -> unlockSkin.setEnabled(true)); // Re-enable the button after operation
    }
}
