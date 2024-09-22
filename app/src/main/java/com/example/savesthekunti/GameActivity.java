    package com.example.savesthekunti;

    import android.os.Bundle;
    import android.os.Handler;
    import android.view.MotionEvent;
    import android.view.View;
    import android.widget.Button;
    import android.widget.FrameLayout;
    import android.widget.ImageView;
    import androidx.appcompat.app.AppCompatActivity;

    import java.util.ArrayList;

    public class GameActivity extends AppCompatActivity {

        private ImageView playerShip ;
        private Button leftBtn, rightBtn;
        private FrameLayout asteContainerLeft,  asteContainerRight ,shipContainer, bulletContainer;
        private float shipSpeed = 10f;
        private Handler moveHandler = new Handler();
        private Handler asteroidHandler = new Handler();
        private Handler bulletHandler = new Handler();
        private Runnable asteroidRunnableLeft, asteroidRunnableRight;
        private boolean isMovingLeft = false;
        private boolean isMovingRight = false;
        private boolean isMoving = false;
        private ArrayList<ImageView> asteroidListLeft = new ArrayList<>();
        private ArrayList<ImageView> asteroidListRight= new ArrayList<>();
        private ArrayList<ImageView> bulletList = new ArrayList<>();

        private static final int asteroidSize = 25;
        private static final int asteroidInterval = 400;
        private static final int asteroidSpeed =2;
        private static final int bulletSpeed = 10;
        private static final int bulletInterval = 300;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.game_activity); // Mengatur layout ke game_activity.xml

            //referensi button dan player
            playerShip = findViewById(R.id.playerShip);
            leftBtn = findViewById(R.id.leftButton);
            rightBtn = findViewById(R.id.rightButton);
            shipContainer = (FrameLayout) playerShip.getParent();

            //Refrerensi untuk  Asteroid
            asteContainerLeft = findViewById(R.id.asteroidContainerLeft);
            asteContainerRight = findViewById(R.id.asteroidContainerRight);

            //Refrensi bullet
            bulletContainer = findViewById(R.id.bulletContainer);


            //INDEX UNTUK PESAWAT
             int selectedShipIndex = getIntent().getIntExtra("selectedShipIndex", 0);

             int gameSpaceShips[] = {
                     R.drawable.blue_cosmos_game,
                     R.drawable.retro_sky,
                     R.drawable.wing_of_justice_game,
                     R.drawable.x56_core_game
             };

             // Set pesawat sesuai index
            playerShip.setImageResource(gameSpaceShips[selectedShipIndex]);


            leftBtn.setOnTouchListener((v, event) ->{
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isMovingLeft = true;
                        isMovingRight = false;
                        startMovingIfNeeded();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        isMovingLeft = false;
                        stopMovingIfNeeded();
                        break;
                } return true;
            });

            rightBtn.setOnTouchListener((v, event) ->{
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isMovingRight = true;
                        isMovingLeft = false;
                        startMovingIfNeeded();
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        isMovingRight = false;
                        stopMovingIfNeeded();
                        break;
                }return true;
            });


            startAsteroidSpawn();
            startAsteroirdAnim();
            startShooting();



        }

        private void moveShip(float deltaX){
            float currentX = playerShip.getX();
            float newX = currentX + deltaX;

            float containerLeft = 0;
            float containerRight = shipContainer.getWidth() - playerShip.getWidth();

            if(newX >= containerLeft && newX <= containerRight){
                playerShip.setX(newX);
            }
        }

      



        //Runnable untuk pesawat
        private final Runnable moveRunnable = new Runnable(){
            @Override
            public void run(){
                if(isMovingLeft){
                    moveShip(-shipSpeed);
                }else if(isMovingRight){
                    moveShip(shipSpeed);
                }

                //ulangi jika tomboil ditahan
                if(isMovingLeft || isMovingRight){
                    moveHandler.postDelayed(this, 5);
                }else{
                    isMoving = false;
                }
            }
        };

        private void startMovingIfNeeded(){
            if(!isMoving){
                isMoving = true;
                moveHandler.post(moveRunnable);
            }
        }

        private void stopMovingIfNeeded(){
            if(!isMovingLeft && !isMovingRight){
                isMoving = false;
                moveHandler.removeCallbacks(moveRunnable);
            }
        }

        //hentikan handler saat berhenti
        @Override
        protected void onDestroy(){
            super.onDestroy();
            moveHandler.removeCallbacks(moveRunnable);
            asteroidHandler.removeCallbacks(asteroidRunnableRight);
            asteroidHandler.removeCallbacks(asteroidRunnableLeft);
        }

        //LOGIKA ASTEROID

        //SPAWN PER 1 DETIK
        private void startAsteroidSpawn(){
            asteroidHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    spawnAsteroid(true);
                    spawnAsteroid(false);
                    asteroidHandler.postDelayed(this, asteroidInterval);
                }
            }, asteroidInterval);
        }

        private void spawnAsteroid(boolean isLeft){

            //Ambil gambar asteroid
            ImageView newAsteroid = new ImageView(this);
            newAsteroid.setLayoutParams(new FrameLayout.LayoutParams(asteroidSize, asteroidSize));
            newAsteroid.setImageResource(R.drawable.asteroid_left);

            if(isLeft){
                asteContainerLeft.addView(newAsteroid); // Tambah new asteroid ke container
                newAsteroid.setX(asteContainerLeft.getWidth() - asteroidSize);
                newAsteroid.setY(0);
                asteroidListLeft.add(newAsteroid); // tambah gambar ke array
            } else{
                asteContainerRight.addView(newAsteroid);
                newAsteroid.setX(0);
                newAsteroid.setY(0);
                asteroidListRight.add(newAsteroid);
            }
        }

        private void startAsteroirdAnim () {
            asteroidHandler.post(new Runnable() {
                @Override
                public void run() {
                    moveAsteroids();
                    asteroidHandler.postDelayed(this, 10);
                }
            });


        }

        private void moveAsteroids(){
            for(int i = asteroidListLeft.size() - 1; i >= 0; i--){
                ImageView asteroid = asteroidListLeft.get(i);
                if(outContainer(asteroid, asteContainerLeft)){
                    asteContainerLeft.removeView(asteroid); //hapus Gambar
                    asteroidListLeft.remove(i);// hapus dari array
                } else{
                        moveAsteroid(asteroid, asteContainerLeft, -asteroidSpeed, 5, true);}
            }

            for(int i = asteroidListRight.size() - 1; i >= 0; i--){
                ImageView asteroid = asteroidListRight.get(i);
                if(outContainer(asteroid, asteContainerRight)){
                    asteContainerRight.removeView(asteroid); //hapus Gambar
                    asteroidListRight.remove(i);// hapus dari array
                } else{
                    moveAsteroid(asteroid, asteContainerRight, asteroidSpeed, 5, false);}
                }
        }

        private boolean outContainer(ImageView asteroid, FrameLayout container){
            float asteroidX = asteroid.getX();
            float asteroidY = asteroid.getY();
            return asteroidY > container.getHeight() || asteroidX + asteroid.getWidth() < 0 || asteroidX > container.getWidth();
        }


        private void moveAsteroid (ImageView asteroid, FrameLayout container, float deltaX,float deltaY, boolean isLeftAsteroid ){
            float newX = asteroid.getX() + deltaX;
            float newY = asteroid.getY() + deltaY;
            asteroid.setX(newX);
            asteroid.setY(newY);

            upScalecaleAsteroid(asteroid);
        }

        private void upScalecaleAsteroid(ImageView asteroid){
            float maxScale = 5f;
            float currentScaleX = asteroid.getScaleX();
            float currentScaleY = asteroid.getScaleY();


            //transisi
            float scaleIncrement = 0.03f;

            if(currentScaleX < maxScale){
                asteroid.setScaleX(currentScaleX + scaleIncrement);
                asteroid.setScaleY(currentScaleY + scaleIncrement);
            }
        }

        //LOGIKA TEMBAKAN
        private void spawnBullet(){
            ImageView bullet = new ImageView(this);
            bullet.setLayoutParams(new FrameLayout.LayoutParams(100, 200));
            bullet.setImageResource(R.drawable.bullet);

            //Atur posisi bulllet
            float bulletX = playerShip.getX() + (playerShip.getWidth() / 2) - (bullet.getLayoutParams().width / 2);
            float bulletY = playerShip.getY() - bullet.getLayoutParams().height;

            //set poisisi bulletnya
            bullet.setX(bulletX);
            bullet.setY(bulletY);

            ((FrameLayout) findViewById(R.id.gameContent)).addView(bullet);
            bulletList.add(bullet);
        }


        private void startShooting(){
            bulletHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    spawnBullet();
                    moveBullet();

                    bulletHandler.postDelayed(this, bulletInterval);
                }
            }, bulletInterval);

        }

        private void moveBullet (){
            bulletHandler.post(new Runnable() {
                @Override
                public void run() {
                    for(int i = bulletList.size() - 1; i >= 0; i--) {
                        ImageView bullet = bulletList.get(i);
                        bullet.setY(bullet.getY() - bulletSpeed);

                        //Hapus bullet

                    }
                    bulletHandler.postDelayed(this, 16);// animasi bullet
                }
            });
        }

        // PAUSE UNTUK MEMINIMALISIR MEMORI
        @Override
        protected void onPause(){
            super.onPause();
            moveHandler.removeCallbacks(moveRunnable);
            asteroidHandler.removeCallbacksAndMessages(null);
            bulletHandler.removeCallbacksAndMessages(null);

            asteroidListRight.clear();
            asteroidListLeft.clear();
            bulletList.clear();
        }

        @Override
        protected void onResume(){
            super.onResume();
            startAsteroidSpawn();
            startShooting();
        }



    }