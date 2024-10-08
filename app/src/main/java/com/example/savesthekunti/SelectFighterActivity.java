package com.example.savesthekunti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.VideoView;


public class SelectFighterActivity extends AppCompatActivity {

    private VideoView videoBackground;
    private int videoPosition;

    //PESAWAT
    private ImageView spaceShip;
    private int[] spaceShips = {R.drawable.blue_cosmos, R.drawable.retro_sky, R.drawable.wing_of_justice, R.drawable.x56_core};


    private int currentIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_fighter);

        ImageButton prevsBtn1 = findViewById(R.id.prevsBtn2);
        prevsBtn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        spaceShip = findViewById(R.id.ship_img);

        ImageButton prevsBtn = findViewById(R.id.prevsBtn);
        ImageButton nextBtn = findViewById(R.id.nextBtn);
        ImageButton selectBtn = findViewById(R.id.selectBtn);

        //inisialisasi blue cosmos secara default
        spaceShip.setImageResource(spaceShips[currentIndex]);

        //inisialisasi video
        videoBackground = findViewById(R.id.selectBg);

        // set video uri
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.skin_selector);
        videoBackground.setVideoURI(videoUri);

        //Looping
        videoBackground.setOnPreparedListener(mp -> mp.setLooping(true));
        videoBackground.start();

        prevsBtn.setOnClickListener(v -> prevsFighter());
        nextBtn.setOnClickListener(v -> nextFighter());
        selectBtn.setOnClickListener(v -> selectGame());


    }

    @Override
    protected void onResume(){
        super.onResume();
        videoBackground.seekTo(videoPosition);
        videoBackground.start();
    }
    @Override
    protected void onPause(){
        super.onPause();
        videoPosition = videoBackground.getCurrentPosition();
        videoBackground.pause();
    }

    private void nextFighter(){
        currentIndex = (currentIndex + 1 ) % spaceShips.length;
        FighterSwitchAnimation();
    }

    private void prevsFighter(){
        currentIndex = (currentIndex - 1 + spaceShips.length) % spaceShips.length;
       FighterSwitchAnimation();

    }

    private void selectGame(){
        Intent intent = new Intent(SelectFighterActivity.this, SelectLevelActivity.class);
        intent.putExtra("selectedShipIndex", currentIndex);
        startActivity(intent);
    }


    private void FighterSwitchAnimation(){
        //animasi fade-out
        AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(200);

        //animasi fade-in
        AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(200);

        //Jalankan Animasi
        spaceShip.startAnimation(fadeOut);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {


            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                //ganti gambar pesawat
                spaceShip.setImageResource(spaceShips[currentIndex]);

                //mulai fadeIn
                spaceShip.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation){}

        });
    }
}
