package com.example.savesthekunti.Activity;

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

import com.example.savesthekunti.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectFighterActivity extends AppCompatActivity {

    private VideoView videoBackground;
    private int videoPosition;

    private ImageView spaceShip;
    private ImageView lockOverlay; // Overlay untuk gembok
    private String[] fighterIDs = {"blue_cosmos", "retro_sky", "wing_of_justice"};
    private ArrayList<String> ownedSkins = new ArrayList<>(); // ArrayList untuk menyimpan skin yang dimiliki
    private int currentSkinIndex = 0;
    private Button unlockSkin;

    private FirebaseFirestore firestore;
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
        String koleksiSkin = username != null ? username : "default_user"; // Menggunakan username sebagai ID koleksi skin

        firestore = FirebaseFirestore.getInstance();

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
        fetchUserSkins(); // Fetch skins setiap kali activity resume
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPosition = videoBackground.getCurrentPosition();
        videoBackground.pause();
    }

    private void fetchUserSkins() {
        CollectionReference skinRef = firestore.collection("Akun").document(username).collection("Koleksi_Skin");
        skinRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ownedSkins.clear();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String skinId = document.getString("id_skin");
                        Boolean isLocked = document.getBoolean("status_terkunci");
                        Boolean isUnlocked = document.getBoolean("is_unlocked");

                        if (skinId != null && isLocked != null && isUnlocked != null) {
                            if (!isLocked || isUnlocked) {
                                ownedSkins.add(skinId);
                            }
                        }
                    }
                } else {
                    Log.d("FetchUserSkins", "Error getting documents: ", task.getException());
                }
                updateFighterView();
            }
        });
    }

    private void updateFighterView() {
        String currentSkinID = fighterIDs[currentSkinIndex]; // Ambil ID skin saat ini

        // Dapatkan ID drawable berdasarkan nama skin
        int skinDrawableId = getResources().getIdentifier(currentSkinID, "drawable", getPackageName());
        spaceShip.setImageResource(skinDrawableId); // Set gambar pesawat

        // Ambil data skin dari Firestore untuk cek status terkunci
        DocumentReference skinRef = firestore.collection("Akun").document(username)
                .collection("Koleksi_Skin").document(currentSkinID);
        skinRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Boolean isUnlocked = document.getBoolean("is_unlocked");
                        Boolean isLocked = document.getBoolean("status_terkunci");

                        // Tampilkan atau sembunyikan overlay gembok sesuai status terkunci
                        if (isUnlocked != null && isUnlocked) {
                            lockOverlay.setVisibility(View.GONE); // Sembunyikan gembok
                        } else if (isLocked != null && isLocked) {
                            lockOverlay.setVisibility(View.VISIBLE); // Tampilkan gembok
                        }
                    } else {
                        lockOverlay.setVisibility(View.VISIBLE); // Tampilkan gembok jika data tidak ditemukan
                    }
                } else {
                    lockOverlay.setVisibility(View.VISIBLE); // Tampilkan gembok jika ada error
                }
            }
        });
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
            intent.putExtra("selectedSkin", fighterIDs[currentSkinIndex]); // Kirim ID skin yang dipilih
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
        Log.d("UnlockSkin", "Selected Skin ID: " + selectedSkinID); // Log untuk memeriksa ID yang dipilih

        DocumentReference skinRef = firestore.collection("Akun").document(username).collection("Koleksi_Skin").document(selectedSkinID);

        // Update status terkunci menjadi false
        skinRef.update("status_terkunci", false)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UnlockSkin", "Skin " + selectedSkinID + " berhasil dibuka.");
                    Toast.makeText(this, "Skin berhasil dibuka!", Toast.LENGTH_SHORT).show();

                    // Update is_unlocked field to true
                    skinRef.update("is_unlocked", true)
                            .addOnSuccessListener(aVoid2 -> {
                                Log.d("UnlockSkin", "is_unlocked field updated to true for " + selectedSkinID);
                                fetchUserSkins(); // Refresh the skin list
                            })
                            .addOnFailureListener(e -> {
                                Log.e("UnlockSkin", "Error updating is_unlocked field: " + e.getMessage());
                                Toast.makeText(this, "Gagal memperbarui status skin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("UnlockSkin", "Error: " + e.getMessage()); // Log kesalahan
                    Toast.makeText(this, "Gagal membuka skin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnCompleteListener(task -> unlockSkin.setEnabled(true)); // Re-enable the button after operation
    }
}
