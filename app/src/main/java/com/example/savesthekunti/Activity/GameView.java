package com.example.savesthekunti.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.savesthekunti.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class GameView extends View {
    private PlayerShip playerShip;
    private Drawable shipDrawable; // Menyimpan drawable pesawat
    private BossAmba bossAmba;
    private Level levelData;

    private Bitmap monsterMiniBitmap;
    private Bitmap bulletsBitmap;
    private Bitmap bossAmbaBitmap;

    private List<MonsterMini> monsterMini;
    private List<Bullet> bullets;

    private boolean isBossAmbaSpawned = false;
    private boolean isBossAmbaDefeated = false;
    private boolean isPlayerAlive = true;
    private boolean isPlayerDefeated = false;
    private boolean gameOver = false;
    private boolean gameWin = false;

    private int monsterMiniDamage;
    private int monsterMiniHp;
    private int bossAmbaHp;
    private int rudalDurability;
    private int rudalDamage;


    private GameActivity gameActivity; // Referensi ke GameActivity
    private int screenWidth, screenHeight;
    private Handler handler;
    private Paint paint;
    private long lastFrameTime = 0;

    private int[] spaceShips = {R.drawable.blue_cosmos, R.drawable.retro_sky, R.drawable.wing_of_justice, R.drawable.x56_core};
    private int score = 0;
    private int defeatedCount = 0;

    private OnChangeScoreListener scoreChangeListener;
    private int scoreThresholdForStar = 100; // Skor minimal untuk memunculkan bintang baru
    private boolean scoreSaved = false;


    private MediaPlayer bossExplodeSFX;
    private MediaPlayer monsterExplodeSFX;
    private MediaPlayer laserSFX;
    private MediaPlayer hittenSFX;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String userID;


//    SCOREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
// Metode untuk mendapatkan nilai skor
public int getScore() {
    return score;
}

    // Metode untuk menambah skor
    public void addScore(int points) {
        score += points;
    }

    public Level getLevel(){
    return levelData;
    }


    //LOGIC

    // Interface untuk mengubah skor
    interface OnChangeScoreListener {
        void onScoreChange(int score, int defeatedCount);
    }

    public interface OnBossHpChangeListener {
        void onBossHpChange(int newHp);
    } private OnBossHpChangeListener bossHpChangeListener;

    public interface OnPlayerHpChangeListener {
        void onPlayerHpChange(int newHp);
    } private OnPlayerHpChangeListener hpChangeListener;

    public void setSelectedShipIndex(String selectedSkin) {
        // Get the drawable resource ID based on the selectedSkin
        int shipDrawableId = getResources().getIdentifier(selectedSkin, "drawable", getContext().getPackageName());

        // Create the PlayerShip object using the correct drawable
        playerShip = new PlayerShip(getContext(), shipDrawableId);
        playerShip.setShipPosition(screenWidth, screenHeight); // Mengatur posisi pesawat setelah diganti
    }

    public void setOnChangeScoreListener(OnChangeScoreListener listener) {
        this.scoreChangeListener = listener;
    }
    public void setOnPlayerHpChangeListener(OnPlayerHpChangeListener listener) {
        this.hpChangeListener = listener;
    }
    public void setOnBossHpChangeListener(OnBossHpChangeListener listener){
        this.bossHpChangeListener = listener;
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.gameActivity = (GameActivity) context; // Mendapatkan referensi ke GameActivity
        init(context);
    }

    public void setLevelData(Level levelData) {
        this.levelData = levelData;
        this.monsterMiniHp = levelData.getMonsterMiniHp();
        this.monsterMiniDamage = levelData.getMonsterMiniDamage();
        this.bossAmbaHp = levelData.getBossAmbaHp();
        bossAmbaBitmap = BitmapFactory.decodeResource(getResources(), levelData.getBossImageSrc());
    }

    private void init(Context context) {

        // ========================================  Inisialisasi Firebase dan Auth =============================================================
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Mendapatkan user ID dari pengguna yang sedang login
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        }

        paint = new Paint();
        playerShip = new PlayerShip(context, spaceShips[0]);
        monsterMini = new ArrayList<>();
        bullets = new ArrayList<>();

        //Objek Bitmap
        monsterMiniBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.monster_mini);
        bulletsBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.beam_bullet);
        bossExplodeSFX = MediaPlayer.create(context, R.raw.boss_explode);
        monsterExplodeSFX = MediaPlayer.create(context, R.raw.blown_monster);
        laserSFX = MediaPlayer.create(context, R.raw.laser_sfx);
        hittenSFX = MediaPlayer.create(context, R.raw.hit_sfx);

        post(() -> {
            screenWidth = getWidth();
            screenHeight = getHeight();
            playerShip.setShipPosition(screenWidth, screenHeight);
            spawnMonsterMini();
            startRespawn();
            startShooting();
        });
    }

    private void saveScoreToFirestore(int score, int defeatedCount, String status) {
        if (firestore == null) {
            firestore = FirebaseFirestore.getInstance();
        }

        if (userID == null) {
            Log.e("GameView", "User ID is not set. Please authenticate the user.");
            return;
        }

        // Generate a unique document ID using a timestamp
        String documentID = FieldValue.serverTimestamp().toString();

        // Create a reference to the "Score" subcollection within the user's document
        CollectionReference scoreRef = firestore.collection("Akun").document(userID).collection("Score");

        // Create a map to store the score data
        Map<String, Object> scoreData = new HashMap<>();
        scoreData.put("score", score);
        scoreData.put("defeatedCount", defeatedCount);
        scoreData.put("status", status); // Can be "win" or "lose"
        scoreData.put("date_created", FieldValue.serverTimestamp());

        // Save the score data to the Firestore database
        scoreRef.document(documentID).set(scoreData)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Score successfully added."))
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to add score: " + e.getMessage()));
    }



    //    ============================== DATABASE SCORE NOT READY YET ========================================================
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (gameOver || gameWin) {
            Log.d("GameView", "GameView draw stopped");

            // Simpan skor ke Firestore hanya sekali saat game selesai
            if (!scoreSaved) {
                scoreSaved = true; // Variabel boolean untuk mencegah penyimpanan berulang
                String status = gameWin ? "win" : "lose";
                saveScoreToFirestore(score, defeatedCount, status);
            }
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (lastFrameTime == 0) {
            lastFrameTime = currentTime;
        }

        float deltaTime = (currentTime - lastFrameTime) / 1000f;
        lastFrameTime = currentTime;

        List<MonsterMini> removeMonsters = new ArrayList<>();
        List<Bullet> removeBullets = new ArrayList<>();

        if (isPlayerAlive && !isPlayerDefeated) {
            playerShip.draw(canvas);
        } else {
            Log.d("GameView", "PlayerShip is defeated and will not be drawn.");
        }

        //Panggil BosAmba
        if(isBossAmbaSpawned && bossAmba != null){
            bossAmba.updatePositionBoss(deltaTime);
            bossAmba.draw(canvas);

        }
        for (MonsterMini monster : monsterMini) {
            monster.updatePositionMonster(deltaTime);
            monster.draw(canvas);

            if (!isPlayerDefeated && checkCollision(monster, playerShip)) {
                playerShip.reduceHp(monster.getDamage());
                removeMonsters.add(monster);
                hittenSFX.start();
                if (hpChangeListener != null) {
                    hpChangeListener.onPlayerHpChange(playerShip.getHp());
                }// Hapus monster setelah tabrakan

                if (playerShip.getHp() <= 0) {
                    isPlayerDefeated = true;
                    isPlayerAlive = false;
                    bullets.clear();
                    gameOver = true; // Set game over flag
                }
            }
        }

        if (!isPlayerDefeated) {
            for (Bullet bullet : bullets) {
                bullet.updatePositionBullet(deltaTime);
                bullet.draw(canvas);

                // Deteksi kolisi dengan monster mini
                for (MonsterMini monster : monsterMini) {
                    if (checkCollision(bullet, monster)) {
                        removeBullets.add(bullet);
                        monster.reduceHp(bullet.getDamage());
                        score += 15;
                        defeatedCount++;

                        if (monster.getHp() <= 0) {
                            removeMonsters.add(monster);
                            monsterExplodeSFX.start();
                        }

                        // Menampilkan animasi ledakan di posisi monster
                        gameActivity.triggerExplosion(monster.getX(), monster.getY());

                        if (scoreChangeListener != null) {
                            scoreChangeListener.onScoreChange(score, defeatedCount);
                        }
                    }
                }

                // Deteksi kolisi dengan bos
                if (isBossAmbaSpawned && checkCollision(bullet, bossAmba)) {
                    bossAmba.reduceHp(bullet.getDamage());
                    removeBullets.add(bullet);
                    if (bossHpChangeListener != null) {
                        bossHpChangeListener.onBossHpChange(bossAmba.getHp());
                    }

                    if (bossAmba.getHp() <= 0) {
                        isBossAmbaSpawned = false;
                        isBossAmbaDefeated = true;
                        bossExplodeSFX.start();
                        gameWin = true;

                        // **Tambahkan logika untuk menyimpan skor saat menang**
                        saveScoreToFirestore(score, defeatedCount, "win");
                    }
                }
            }
        }

        monsterMini.removeAll(removeMonsters);
        bullets.removeAll(removeBullets);

        removeOffScreenMonsters();
        removeOffScreenBullets();

        if(score >= 200 && !isBossAmbaSpawned && !isBossAmbaDefeated){
            spawnBossAmba();}
        invalidate();
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public int getBossAmbaMaxHp() {
        if (bossAmba != null) {
            return levelData.getBossAmbaHp(); // Ambil dari levelData, bukan nilai dinamis
        }
        return 0; // Nilai default jika BossAmba belum ada
    }


    public int getPlayerShipHp() {
        return playerShip.getHp();
    }
    public int getBossAmbaHp(){if (bossAmba != null) {
        return bossAmba.getHp();
    } return 0;}
    public BossAmba getBossAmba() {return bossAmba;}
    public void destroy() {// Stop any ongoing tasks

        // Hentikan Handler untuk menghindari tugas yang terus berjalan setelah GameView dihancurkan
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
            Log.d("GameView", "Handler has been stopped and all callbacks removed.");
        }
        // Recycle Bitmaps
        if (monsterMiniBitmap != null) {
            monsterMiniBitmap.recycle();
            monsterMiniBitmap = null;
            Log.d("GameView", "monsterMiniBitmap has been recycled.");
        }

        if (bulletsBitmap != null) {
            bulletsBitmap.recycle();
            bulletsBitmap = null;
            Log.d("GameView", "bulletsBitmap has been recycled.");
        }

        if (bossAmbaBitmap != null) {
            bossAmbaBitmap.recycle();
            bossAmbaBitmap = null;
            Log.d("GameView", "bossAmbaBitmap has been recycled.");
        }

        // Clear lists
        monsterMini.clear();
        bullets.clear();
        Log.d("GameView", "All lists have been cleared.");

        // Release Sound Effects
        if (bossExplodeSFX != null) {
            bossExplodeSFX.release();
            bossExplodeSFX = null;
            Log.d("GameView", "bossExplodeSFX has been released.");
        }

        if (monsterExplodeSFX != null) {
            monsterExplodeSFX.release();
            monsterExplodeSFX = null;
            Log.d("GameView", "monsterExplodeSFX has been released.");
        }

        if (laserSFX != null) {
            laserSFX.release();
            laserSFX = null;
            Log.d("GameView", "laserSFX has been released.");
        }
        if (hittenSFX != null) {
            hittenSFX.release();
            hittenSFX = null;
            Log.d("GameView", "laserSFX has been released.");
        }
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
                if (laserSFX != null) {
                    laserSFX.setLooping(true);
                    laserSFX.start();
                }
                shootBullet();
                handler.postDelayed(this, 115); // Menembak setiap 100 ms
            }
        }, 100);
    }

    //SHOOT METHOD
    private void shootBullet() {
        int bulletSize = getResources().getDimensionPixelSize(R.dimen.bullet_size);
        float bulletX = playerShip.getShipX() + (playerShip.getShipWidth() / 2) - (bulletSize / 2);
        float bulletY = playerShip.getShipY();
        bullets.add(new Bullet(getContext(), bulletsBitmap, bulletX, bulletY, 2500, bulletSize));
    }
    //SHOOT METHOD END

    private void spawnBossAmba(){
        int bossWidth = getResources().getDimensionPixelSize(R.dimen.boss_width);
        int spawnX = (screenWidth - bossWidth) / 2;
        bossAmba = new BossAmba(getContext(), bossAmbaBitmap, spawnX, 0,  100);
        isBossAmbaSpawned = true;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return playerShip.handleTouch(event);
    }

    //COLLISION
    //MONSTERS COLISSION
    public boolean checkCollision(Bullet bullet, MonsterMini monster) {
        return bullet.getX() < monster.getX() + monster.getSize() &&
                bullet.getX() + bullet.getSize() > monster.getX() &&
                bullet.getY() < monster.getY() + monster.getSize() &&
                bullet.getY() + bullet.getSize() > monster.getY();
    }

    //BOSS COLLISION
    private boolean checkCollision(Bullet bullet, BossAmba boss) {
        if(!isBossAmbaDefeated) {
            float bulletLeft = bullet.getX();
            float bulletRight = bullet.getX() + bullet.getSize(); // Asumsikan peluru menggunakan size
            float bulletTop = bullet.getY();
            float bulletBottom = bullet.getY() + bullet.getSize();

            float bossLeft = boss.getX();
            float bossRight = boss.getX() + boss.getWidth();
            float bossTop = boss.getY();
            float bossBottom = boss.getY() + boss.getHeight();

            return bulletRight > bossLeft && bulletLeft < bossRight &&
                    bulletBottom > bossTop && bulletTop < bossBottom;
        }
        return false;
    }

    private boolean checkCollision(MonsterMini monster, PlayerShip playerShip) {
        return monster.getX() < playerShip.getShipX() + playerShip.getShipWidth() &&
                monster.getX() + monster.getSize() > playerShip.getShipX() &&
                monster.getY() < playerShip.getShipY() + playerShip.getShipHeight() &&
                monster.getY() + monster.getSize() > playerShip.getShipY();
    }

    //COLLISION END


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
        bullets.removeAll(bulletsToRemove);}


    // Class PlayerShip
    class PlayerShip {
        private Bitmap playerShipBitmap;
        private float shipX, shipY;
        private int shipWidth, shipHeight;
        private int hp;

        public PlayerShip(Context context, int playerShipResId) {
            playerShipBitmap = BitmapFactory.decodeResource(context.getResources(), playerShipResId);
            shipWidth = context.getResources().getDimensionPixelSize(R.dimen.player_ship_width);
            shipHeight = context.getResources().getDimensionPixelSize(R.dimen.player_ship_height);
            this.hp = 1000;
        }

        public void setShipPosition(int screenWidth, int screenHeight) {
            shipX = (screenWidth - shipWidth) / 2;
            shipY = screenHeight - shipHeight - 50;
        }

        public void draw(Canvas canvas) {
            canvas.drawBitmap(Bitmap.createScaledBitmap(playerShipBitmap, shipWidth, shipHeight, false), shipX, shipY, paint);
        }

        public void reduceHp(int damage){
            hp -= damage;
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

        public boolean isOffScreen(){
            return hp <= 0;
        }

        public int getHp(){return hp;}

        public float getShipX() {
            return shipX;
        }

        public float getShipY() {
            return shipY;
        }

        public int getShipHeight() {
            return shipHeight;
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
        private int damage;
        private int hp;

        public MonsterMini(Context context, Bitmap monsterMiniBitmap, float x, float y, float velocityY, int size) {
            this.monsterMiniBitmap = monsterMiniBitmap;
            this.x = x;
            this.y = y;
            this.velocity = velocityY;
            this.size = context.getResources().getDimensionPixelSize(R.dimen.mosnter_size);
            this.damage = levelData.getMonsterMiniDamage();
            this.hp = levelData.getMonsterMiniHp();
        }

        public void updatePositionMonster(float deltaTime) {
            y += velocity * deltaTime;
        }

        public void draw(Canvas canvas) {
            canvas.drawBitmap(Bitmap.createScaledBitmap(monsterMiniBitmap, size, size, false), x, y, paint);
        }

        public void reduceHp(int damage){
            hp -= damage;
        }

        public int getDamage(){return damage;}
        public int getHp(){return hp;}
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




    //CLASS BULLET
    class Bullet {
        private Bitmap bulletBitmap;
        private float x, y;
        private int size;
        private float velocity;
        int damage;

        public Bullet(Context context, Bitmap bulletBitmap, float x, float y, float velocityY, int size) {
            this.bulletBitmap = bulletBitmap;
            this.x = x;
            this.y = y;
            this.velocity = velocityY;
            this.size = context.getResources().getDimensionPixelSize(R.dimen.bullet_size);
            this.damage = 200;
        }

        public void updatePositionBullet(float deltaTime) {
            y -= velocity * deltaTime; // Peluru bergerak ke atas
        }

        public void draw(Canvas canvas) {
            canvas.drawBitmap(Bitmap.createScaledBitmap(bulletBitmap, size, size, false), x, y, paint);
        }

        public int getDamage(){return damage;}
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


    // CLASS BOSS AMBA
    class BossAmba {
        private Bitmap bossAmbaBitmap;
        private float x, y;
        private float velocityY;
        private int width, height;
        int hp;
        private float velocityX;
        private boolean isMovingRight;

        public BossAmba(Context context, Bitmap bossAmbaBitmap, float x, float y, float velocityY) {
            this.bossAmbaBitmap = bossAmbaBitmap;
            this.x = x;
            this.y = y;
            this.velocityY = velocityY;
            this.hp = levelData.getBossAmbaHp();
            width = context.getResources().getDimensionPixelSize(R.dimen.boss_width);
            height = context.getResources().getDimensionPixelSize(R.dimen.boss_height);
            this.velocityX = 200f;
            this.isMovingRight = true;
        }

        public void updatePositionBoss(float deltaTime) {
            // Gerakan jatuh bos dan berhenti ketika y = 500
            if (y < 50) {
                y += velocityY * deltaTime;
            }

            if(isMovingRight){
                x += velocityX * deltaTime;
                if(x + width >= screenWidth){
                    isMovingRight = false;
                }
            }else{
                x -= velocityX * deltaTime;
                if(x <= 0){
                    isMovingRight = true;
                }}
        }

        public void draw(Canvas canvas) {
            canvas.drawBitmap(Bitmap.createScaledBitmap(bossAmbaBitmap, width, height, false), x, y, paint);
        }

        public void reduceHp(int damage){
            hp -= damage;
        }
        public int getHp(){return hp;}
        public float getX() {return x;}
        public float getY() {return y;}
        public int getWidth() {return width;}
        public int getHeight() {return height;
        }
    }

    //CLASS RUDAL AMBA
    class RudalAmba {
        private Bitmap rudalAmba;
        private float x, y;
        private float velocity;
        private int size;
        private int durability;
        private int damage;

        public RudalAmba (Context context,Bitmap rudalAmba, float x, float y, float velocityY, int size ){
            this.rudalAmba = rudalAmba;
            this.x = x;
            this.y = y;
            this.velocity = velocityY;
            this.size = getResources().getDimensionPixelSize(R.dimen.rudal_size);
        }
        public void updatePositionRudal(float deltaTime){
            y += velocity * deltaTime;
        }

        public void draw(Canvas canvas){
            canvas.drawBitmap(Bitmap.createScaledBitmap(rudalAmba, size , size, false), x, y, paint);
        }
        public void reduceDurability(int damage){durability -= damage;}
        public int getDamage(){return damage;}
        public int getDurability(){return durability;}
        public float getX(){return x;}
        public float getY() {return y;}
        public int getSize(){return size;}
        public boolean isOffScreen(int screenHeight) {
            return y > screenHeight;
        }
    }



}

