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
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends View {
    private PlayerShip playerShip;
    private Bitmap monsterMiniBitmap;
    private Bitmap bulletsBitmap;
    private List<MonsterMini> monsterMini;
    private List<Bullet> bullets;

    private GameActivity gameActivity; // Referensi ke GameActivity
    private int screenWidth, screenHeight;
    private Handler handler;
    private Paint paint;
    private long lastFrameTime = 0;
    private int[] spaceShips = {R.drawable.blue_cosmos, R.drawable.retro_sky, R.drawable.wing_of_justice, R.drawable.x56_core};
    private int score = 0;
    private int defeatedCount = 0;
    private OnChangeScoreListener scoreChangeListener;

    // Interface untuk mengubah skor
    interface OnChangeScoreListener {
        void onScoreChange(int score, int defeatedCount);
    }

    public void setSelectedShipIndex(int selectedShipIndex) {
        if (selectedShipIndex < 0 || selectedShipIndex >= spaceShips.length) {
            throw new IllegalArgumentException("Invalid ship index");
        }
        playerShip = new PlayerShip(getContext(), spaceShips[selectedShipIndex]);
        playerShip.setShipPosition(screenWidth, screenHeight); // Mengatur posisi pesawat setelah diganti
    }


    public void setOnChangeScoreListener(OnChangeScoreListener listener) {
        this.scoreChangeListener = listener;
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.gameActivity = (GameActivity) context; // Mendapatkan referensi ke GameActivity
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        playerShip = new PlayerShip(context, spaceShips[0]);
        monsterMini = new ArrayList<>();
        bullets = new ArrayList<>();

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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        playerShip.draw(canvas);

        long currentTime = System.currentTimeMillis();
        if (lastFrameTime == 0) {
            lastFrameTime = currentTime;
        }

        float deltaTime = (currentTime - lastFrameTime) / 1000f;
        lastFrameTime = currentTime;

        List<MonsterMini> removeMonsters = new ArrayList<>();
        List<Bullet> removeBullets = new ArrayList<>();

        for (MonsterMini monster : monsterMini) {
            monster.updatePositionMonster(deltaTime);
            monster.draw(canvas);
        }

        for (Bullet bullet : bullets) {
            bullet.updatePositionBullet(deltaTime);
            bullet.draw(canvas);

            for (MonsterMini monster : monsterMini) {
                if (checkCollision(bullet, monster)) {
                    removeBullets.add(bullet);
                    removeMonsters.add(monster);
                    score += 15;
                    defeatedCount++;

                    // Menampilkan animasi ledakan di posisi monster
                    Log.d("GameView", "Triggering explosion at: " + monster.getX() + ", " + monster.getY());
                    gameActivity.triggerExplosion(monster.getX(), monster.getY());

                    if (scoreChangeListener != null) {
                        scoreChangeListener.onScoreChange(score, defeatedCount);
                    }
                }
            }
        }

        monsterMini.removeAll(removeMonsters);
        bullets.removeAll(removeBullets);

        removeOffScreenMonsters();
        removeOffScreenBullets();

        invalidate();
    }

    private void spawnMonsterMini() {
        Random random = new Random();
        int monsterSize = 100;

        int numberOfMonsters = random.nextInt(1) + 1; // Antara 1 hingga 3 monster

        for (int i = 0; i < numberOfMonsters; i++) {
            int randomX, randomY;
            boolean isOverlapping;

            do {
                isOverlapping = false;
                randomX = random.nextInt(screenWidth - monsterSize);
                randomY = 0; // Posisi Y monster dari bagian atas layar

                for (MonsterMini existingMonster : monsterMini) {
                    if (Math.abs(existingMonster.getX() - randomX) < monsterSize &&
                            Math.abs(existingMonster.getY() - randomY) < monsterSize) {
                        isOverlapping = true;
                        break;
                    }
                }
            } while (isOverlapping);

            monsterMini.add(new MonsterMini(getContext(), monsterMiniBitmap, randomX, randomY, 600, monsterSize));
        }
    }

    private void startRespawn() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                spawnMonsterMini();
                invalidate();
                handler.postDelayed(this, 400); // Spawn setiap 400 ms
            }
        }, 400);
    }

    private void startShooting() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                shootBullet();
                handler.postDelayed(this, 350); // Menembak setiap 100 ms
            }
        }, 100);
    }

    private void shootBullet() {
        int bulletSize = getResources().getDimensionPixelSize(R.dimen.bullet_size);
        float bulletX = playerShip.getShipX() + (playerShip.getShipWidth() / 2) - (bulletSize / 2);
        float bulletY = playerShip.getShipY();
        bullets.add(new Bullet(getContext(), bulletsBitmap, bulletX, bulletY, 2500, bulletSize));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return playerShip.handleTouch(event);
    }

    public boolean checkCollision(Bullet bullet, MonsterMini monster) {
        return bullet.getX() < monster.getX() + monster.getSize() &&
                bullet.getX() + bullet.getSize() > monster.getX() &&
                bullet.getY() < monster.getY() + monster.getSize() &&
                bullet.getY() + bullet.getSize() > monster.getY();
    }

    public void removeOffScreenMonsters() {
        List<MonsterMini> monstersToRemove = new ArrayList<>();
        for (MonsterMini monster : monsterMini) {
            if (monster.isOffScreen(screenHeight)) {
                monstersToRemove.add(monster);
            }
        }
        monsterMini.removeAll(monstersToRemove);
    }

    public void removeOffScreenBullets() {
        List<Bullet> bulletsToRemove = new ArrayList<>();
        for (Bullet bullet : bullets) {
            if (bullet.isOffScreen(screenHeight)) {
                bulletsToRemove.add(bullet);
            }
        }
        bullets.removeAll(bulletsToRemove);
    }

    // Class PlayerShip
    class PlayerShip {
        private Bitmap playerShipBitmap;
        private float shipX, shipY;
        private int shipWidth, shipHeight;

        public PlayerShip(Context context, int playerShipResId) {
            playerShipBitmap = BitmapFactory.decodeResource(context.getResources(), playerShipResId);
            shipWidth = context.getResources().getDimensionPixelSize(R.dimen.player_ship_width);
            shipHeight = context.getResources().getDimensionPixelSize(R.dimen.player_ship_height);
        }

        public void setShipPosition(int screenWidth, int screenHeight) {
            shipX = (screenWidth - shipWidth) / 2;
            shipY = screenHeight - shipHeight - 50;
        }

        public void draw(Canvas canvas) {
            canvas.drawBitmap(Bitmap.createScaledBitmap(playerShipBitmap, shipWidth, shipHeight, false), shipX, shipY, paint);
        }

        public boolean handleTouch(MotionEvent event) {
            float touchX = event.getX();
            float touchY = event.getY();

            shipX = touchX - (shipWidth / 2);
            shipY = touchY - (shipHeight / 2);

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

            invalidate();
            return true;
        }

        public float getShipX() {
            return shipX;
        }

        public float getShipY() {
            return shipY;
        }

        public int getShipWidth() {
            return shipWidth;
        }
    }

    // Class MonsterMini
    class MonsterMini {
        private Bitmap monsterMiniBitmap;
        private float x, y;
        private int size;
        private float velocity;

        public MonsterMini(Context context, Bitmap monsterMiniBitmap, float x, float y, float velocityY, int size) {
            this.monsterMiniBitmap = monsterMiniBitmap;
            this.x = x;
            this.y = y;
            this.velocity = velocityY;
            this.size = context.getResources().getDimensionPixelSize(R.dimen.mosnter_size);
        }

        public void updatePositionMonster(float deltaTime) {
            y += velocity * deltaTime;
        }

        public void draw(Canvas canvas) {
            canvas.drawBitmap(Bitmap.createScaledBitmap(monsterMiniBitmap, size, size, false), x, y, paint);
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public int getSize() {
            return size;
        }

        public boolean isOffScreen(int screenHeight) {
            return y > screenHeight;
        }
    }

    // Class Bullet
    class Bullet {
        private Bitmap bulletBitmap;
        private float x, y;
        private int size;
        private float velocity;

        public Bullet(Context context, Bitmap bulletBitmap, float x, float y, float velocityY, int size) {
            this.bulletBitmap = bulletBitmap;
            this.x = x;
            this.y = y;
            this.velocity = velocityY;
            this.size = context.getResources().getDimensionPixelSize(R.dimen.bullet_size);
        }

        public void updatePositionBullet(float deltaTime) {
            y -= velocity * deltaTime; // Peluru bergerak ke atas
        }

        public void draw(Canvas canvas) {
            canvas.drawBitmap(Bitmap.createScaledBitmap(bulletBitmap, size, size, false), x, y, paint);
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public int getSize() {
            return size;
        }

        public boolean isOffScreen(int screenHeight) {
            return y < 0;
        }
    }
}
