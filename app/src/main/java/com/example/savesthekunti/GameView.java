package com.example.savesthekunti;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends View {

    private PlayerShip playerShip;
    //bitmap
    private Bitmap background;
    private Bitmap monsterMiniBitmap;
    private Bitmap bulletsBitmap;
    //Array
    private List<MonsterMini> monsterMini;
    private List<Bullet> bullets;

    private int screenWidth, screenHeight;
    private Handler handler;
    private Paint paint;
    private long lastFrameTime = 0;
    private int[] spaceShips = {R.drawable.blue_cosmos, R.drawable.retro_sky, R.drawable.wing_of_justice, R.drawable.x56_core};



                        //CONSCRUCTOR GAMEVIEW
    public GameView(Context context, int selectedShipIndex) {
        super(context);

        //posisi pesawat
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background_gameplay);
        paint = new Paint();

        playerShip = new PlayerShip(context, spaceShips[selectedShipIndex]);
        monsterMini = new ArrayList<>();

        // Memuat bitmap monster mini hanya sekali
        monsterMiniBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.monster_mini);

        post(() -> {
            screenWidth = getWidth();
            screenHeight = getHeight();
            playerShip.setShipPosition(screenWidth, screenHeight);
            spawnMonsterMini();
            startRespawn();
            startShooting();
        });
    }

    public void spawnMonsterMini() {
        Random random = new Random();
        int monsterSize = 100;

        // Menghasilkan jumlah monster yang acak antara 1 hingga 10
        int numberOfMonsters = random.nextInt(4) + 1; // Antara 1 hingga 10

        for (int i = 0; i < numberOfMonsters; i++) {
            // Menghasilkan posisi X acak dalam batas layar
            int randomX = random.nextInt(screenWidth - monsterSize);
            int randomY = 0; // Tetapkan posisi Y monster dari bagian atas layar
            monsterMini.add(new MonsterMini(getContext(), monsterMiniBitmap, randomX, randomY, 400, monsterSize)); // Kecepatan monster
        }

    }

                        // DRAW ON CANVAS
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Menggambar background
        canvas.drawBitmap(background, 0, 0, paint);

        playerShip.draw(canvas);

        long currentTime = System.currentTimeMillis();
        if (lastFrameTime == 0) {
            lastFrameTime = currentTime;
        }

        float deltaTime = (currentTime - lastFrameTime) / 1000f;
        lastFrameTime = currentTime;

        // Perbarui posisi monster dan gambar
        for (MonsterMini monster : monsterMini) {
            monster.updatePositionMonster(deltaTime);
            monster.draw(canvas);
        }

        // Hapus monster yang sudah keluar dari layar
        removeOffScreenMonsters();

        // Hanya panggil invalidate() di akhir
        invalidate();
    }

    private void startRespawn() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                spawnMonsterMini();
                invalidate(); // Gambar ulang setelah spawn monster
                handler.postDelayed(this, 700); // Spawn setiap 1 detik
            }
        }, 700);
    }

    private void startShooting (){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                shootBullet();
                handler.postDelayed(this, 100);
            }
        }, 100);
    }

    private void shootBullet() {
        float BulletX = playerShip.getShipX() + (playerShip.getShipWidth() / 2) - (20 / 2);
        float BulletY = playerShip.getShipY();
        int bulletSize = getResources().getDimensionPixelSize(R.dimen.bullet_size);
        bullets.add(new Bullet(getContext(),bulletsBitmap, BulletX, BulletY, 800, bulletSize));

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return playerShip.handleTouch(event);
    }


                        // CLASS PLAYER SHIP
    class PlayerShip {
        private Bitmap playerShipBitmap;
        private float shipX, shipY;
        private int shipWidth, shipHeight;

        // Class untuk membuat pesawat
        public PlayerShip(Context context, int playerShipResId) {
            playerShipBitmap = BitmapFactory.decodeResource(context.getResources(), playerShipResId);
            shipWidth = context.getResources().getDimensionPixelSize(R.dimen.player_ship_width);
            shipHeight = context.getResources().getDimensionPixelSize(R.dimen.player_ship_height);
        }
        // Set posisi pesawat
        public void setShipPosition(int screenWidth, int screenHeight) {
            shipX = (screenWidth - shipWidth) / 2;
            shipY = screenHeight - shipHeight - 50;
        }
        // Gambar pesawat
        public void draw(Canvas canvas) {
            canvas.drawBitmap(Bitmap.createScaledBitmap(playerShipBitmap, shipWidth, shipHeight, false), shipX, shipY, paint);
        }

        public boolean handleTouch(MotionEvent event) {
            // Menggerakkan pesawat sesuai posisi sentuhan (horizontal dan vertikal)
            float touchX = event.getX();
            float touchY = event.getY();

            // Gerakan horizontal
            shipX = touchX - (shipWidth / 2);
            // Gerakan vertikal (batasi agar tidak keluar dari batas layar)
            shipY = touchY - (shipHeight / 2);

            // Batasi gerakan agar pesawat tetap dalam batas layar
            if (shipX < 0) {
                shipX = 0;
            } else if (shipX > screenWidth - shipWidth) {
                shipX = screenWidth - shipWidth;
            }

            if (shipY < 0) {
                shipY = 0;
            } else if (shipY > screenHeight - shipHeight) {
                shipY = screenHeight - shipHeight;
            }

            invalidate(); // Panggil invalidate() setelah posisi diubah
            return true;
        }

        public float getShipX(){
            return shipX;
        }
        public float getShipY(){
            return shipY;
        }
        public int getShipWidth(){
            return shipWidth;
        }
        public int getShipheight(){
            return shipHeight;
        }
    }

    public void removeOffScreenMonsters() {
        List<MonsterMini> monstersToRemove = new ArrayList<>();
        for (MonsterMini monster : monsterMini) {
            if (monster.isOffScreen(screenHeight)) {
                monstersToRemove.add(monster);
                Log.d("GameView", "Monster removed");
            }
        }
        monsterMini.removeAll(monstersToRemove);
    }


                        // CLASS MONSTER MINI
    class MonsterMini {
        private Bitmap monsterMiniBitmap;
        private float x, y;
        private int size;
        private float velocity;

        public MonsterMini(Context context,Bitmap monsterMiniBitmap, float x, float y, float velocityY, int size) {
            this.monsterMiniBitmap = monsterMiniBitmap;
            this.x = x;
            this.y = y;
            this.velocity = velocityY;

            this.size = context.getResources().getDimensionPixelSize(R.dimen.mosnter_size);
        }

        public void updatePositionMonster(float deltaTime) {
            y += velocity * deltaTime; // Pergerakan monster ke bawah
        }

        public void draw(Canvas canvas) {
            canvas.drawBitmap(Bitmap.createScaledBitmap(monsterMiniBitmap, size, size, false), x, y, paint);
        }

        public boolean isOffScreen(int screenHeight) {
            return y > screenHeight;  // Jika y lebih besar dari tinggi layar, monster dianggap keluar
        }
    }
                        //CLASS MONSTER END


                        //CLASS BULLET
    class Bullet{
        private Bitmap bulletsBitmap;
        private float x, y;
        private int size;
        private float velocity;

            public Bullet (Context context, Bitmap bulletBitmap, float x, float y, float velocityY, int size) {
                this.bulletsBitmap = bulletBitmap;
                this.x = x;
                this.y = y;
                this.velocity = velocityY;
                this.size = context.getResources().getDimensionPixelSize(R.dimen.bullet_size);
            }
            public void updatePositionBullet(float deltaTime){
                y -= velocity * deltaTime;
            }
            public void draw(Canvas canvas){
                canvas.drawBitmap(Bitmap.createScaledBitmap(bulletsBitmap, size, size, false), x, y, paint);
            }
            public boolean isOffScreen(int screenHeight){
                return y < 0;
            }

    }
}



