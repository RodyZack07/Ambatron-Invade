package com.example.savesthekunti;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GameView extends View {

    private PlayerShip playerShip;
    //bitmap

    private Bitmap monsterMiniBitmap;
    private Bitmap bulletsBitmap;
    private Bitmap monsterBulletBitmap; // Bitmap untuk peluru monster
    //Array
    private List<MonsterMini> monsterMini;
    private List<Bullet> bullets;

    private int screenWidth, screenHeight;
    private Handler handler;
    private Paint paint;
    private long lastFrameTime = 0;
    private int[] spaceShips = {R.drawable.blue_cosmos, R.drawable.retro_sky, R.drawable.wing_of_justice, R.drawable.x56_core};
    private int score = 0;
    private int defeatedCount = 0;
    private OnChangeScoreListener scoreChangeListener;
    private TextView scoreText;
    private TextView monsterDefeatedText;
    private GameView gameView;


                    //UI SCORE DAN DEFEATED COUNT
                    interface OnChangeScoreListener{
        void onScoreChange(int score, int defeatedCount);
    }

    public void setOnChangeScoreListener(OnChangeScoreListener listener){
        this.scoreChangeListener = listener;
    }
    public GameView (Context context, AttributeSet attrs){
        super(context, attrs);
        Init(context);
    }




    public GameView (Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        Init(context);
    }
                        //
    public void Init (Context context) {
        //posisi pesawat
        paint = new Paint();

        playerShip = new PlayerShip(context, spaceShips[0]);
        monsterMini = new ArrayList<>();
        bullets =  new ArrayList<>();

        // Memuat bitmap monster mini hanya sekali
        monsterMiniBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.monster_mini);
        bulletsBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.beam_bullet);

        post(() -> {
            screenWidth = getWidth();
            screenHeight = getHeight();
            playerShip.setShipPosition(screenWidth, screenHeight);
            spawnMonsterMini();
            startRespawn();
            startShooting();
        });
    }

    public void setSelectedShipIndex(int selectedShipIndex){
        playerShip = new PlayerShip(getContext(), spaceShips[selectedShipIndex]);
        playerShip.setShipPosition(screenWidth, screenHeight);
    }

    public void spawnMonsterMini() {
        Random random = new Random();
        int monsterSize = 100;

        // Menghasilkan jumlah monster yang acak antara 1 hingga 10
        int numberOfMonsters = random.nextInt(3) + 1; // Antara 1 hingga 10

        for (int i = 0; i < numberOfMonsters; i++) {
            int randomX, randomY;
            boolean isOverlapping;
            // Loop hingga posisi monster tidak bertumpuk
            do {
                isOverlapping = false;
                randomX = random.nextInt(screenWidth - monsterSize);
                randomY = 0; // Tetapkan posisi Y monster dari bagian atas layar

                // Cek apakah posisi monster bertumpuk dengan monster lain
                for (MonsterMini existingMonster : monsterMini) {
                    if (Math.abs(existingMonster.getX() - randomX) < monsterSize &&
                            Math.abs(existingMonster.getY() - randomY) < monsterSize) {
                        isOverlapping = true;
                        break;
                    }
                }
            } while (isOverlapping);

            // Tambahkan monster ke list setelah memastikan tidak bertumpuk
            monsterMini.add(new MonsterMini(getContext(), monsterMiniBitmap, randomX, randomY, 1200, monsterSize)); // Kecepatan monster
        }
    }

                        // DRAW ON CANVAS
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Menggambar background

        playerShip.draw(canvas);

        long currentTime = System.currentTimeMillis();
        if (lastFrameTime == 0) {
            lastFrameTime = currentTime;
        }

        float deltaTime = (currentTime - lastFrameTime) / 1000f;
        lastFrameTime = currentTime;

        //Array baru buat hitting monster & bullet
        List<MonsterMini> removeMosnters = new ArrayList<>();
        List<Bullet> removeBullets = new ArrayList<>();

        // Perbarui posisi monster dan gambar
        for (MonsterMini monster : monsterMini) {
            monster.updatePositionMonster(deltaTime);
            monster.draw(canvas);
        }

        for (Bullet bullet : bullets){
            bullet.updatePositionBullet(deltaTime);
            bullet.draw(canvas);

            //collision
            for(MonsterMini monsters : monsterMini){
                if(checkCollision(bullet, monsters)){
                    removeBullets.add(bullet);
                    removeMosnters.add(monsters);
                    score += 15;
                    defeatedCount++;

                    if(scoreChangeListener != null){
                        scoreChangeListener.onScoreChange(score, defeatedCount);
                    }
                }
            }
        }


        //PANGGIL Collision
        monsterMini.removeAll(removeMosnters);
        bullets.removeAll(removeBullets);

        // Hapus monster yang sudah keluar dari layar
        removeOffScreenMonsters();
        removeOffScreenBullets();

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
                handler.postDelayed(this, 400); // Spawn setiap 1 detik
            }
        }, 400);
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
        int bulletSize = getResources().getDimensionPixelSize(R.dimen.bullet_size);
        float bulletX = playerShip.getShipX() + (playerShip.getShipWidth() / 2) - (bulletSize / 2);
        float BulletY = playerShip.getShipY();
        bullets.add(new Bullet(getContext(),bulletsBitmap, bulletX, BulletY, 2500, bulletSize));

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

        //METHOD COLLISION
    public boolean checkCollision (Bullet bullet, MonsterMini monsters){
        return bullet.getX() < monsters.getX() + monsters.getSize() &&
                bullet.getX() + bullet.getSize() > monsters.getX() &&
                bullet.getY() < monsters.getY() + monsters.getSize() &&
                bullet.getY() + bullet.getSize() > monsters.getY();
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

    public void removeOffScreenBullets(){
        List<Bullet> bulletsToRemove = new ArrayList<>();
        for (Bullet bullet : bullets){
            if(bullet.isOffScreen(screenHeight)){
                bulletsToRemove.add(bullet);
            }
        }
        bullets.removeAll(bulletsToRemove);
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

        public float getX (){return x;}
                            public float getY () {return y;}
                            public float getSize(){return size;}
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
                this.size = size;
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

            public float getX (){return x;}
            public float getY () {return y;}
            public float getSize(){return size;}

    }
}



