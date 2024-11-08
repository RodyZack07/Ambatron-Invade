package com.example.savesthekunti.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.savesthekunti.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends View {
    private PlayerShip playerShip;
    private Drawable shipDrawable; // Menyimpan drawable pesawat
    private BossAmba bossAmba;
    private Level levelData;

    private Bitmap monsterMiniBitmap;
    private Bitmap bulletsBitmap;
    private Bitmap bossAmbaBitmap;
    private Bitmap rudalAmba;

    private List<MonsterMini> monsterMini;
    private List<Bullet> bullets;
    private List<RudalAmba> rudalAmbas;

    private boolean isBossAmbaSpawned = false;
    private boolean isBossAmbaDefeated = false;
    private boolean isPlayerAlive = true;
    private boolean isPlayerDefeated = false;
    private boolean gameOver = false;

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



    //LOGIC

    // Interface untuk mengubah skor
    interface OnChangeScoreListener {
        void onScoreChange(int score, int defeatedCount);
    }

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
        this.rudalDurability = levelData.getRudalDurability();
        this.rudalDamage = levelData.getRudalDamage();
        bossAmbaBitmap = BitmapFactory.decodeResource(getResources(), levelData.getBossImageSrc());
    }

    private void init(Context context) {
        paint = new Paint();
        playerShip = new PlayerShip(context, spaceShips[0]);
        monsterMini = new ArrayList<>();
        bullets = new ArrayList<>();
        rudalAmbas = new ArrayList<>();



        //Objek Bitmap
        monsterMiniBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.monster_mini);
        bulletsBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.beam_bullet);
        rudalAmba = BitmapFactory.decodeResource(getResources(), R.drawable.rudal_amba);



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

        long currentTime = System.currentTimeMillis();
        if (lastFrameTime == 0) {
            lastFrameTime = currentTime;
        }

        float deltaTime = (currentTime - lastFrameTime) / 1000f;
        lastFrameTime = currentTime;

        List<MonsterMini> removeMonsters = new ArrayList<>();
        List<Bullet> removeBullets = new ArrayList<>();
        List<RudalAmba> removeRudal = new ArrayList<>();


        if (isPlayerAlive && !isPlayerDefeated) {
            playerShip.draw(canvas);
        } else {
            Log.d("GameView", "PlayerShip is defeated and will not be drawn.");
        }

        if(gameOver){
            return;
        }

        //Panggil BosAmba
        if(isBossAmbaSpawned && bossAmba != null){
            bossAmba.updatePositionBoss(deltaTime);
            bossAmba.draw(canvas);

        }

        if(isBossAmbaSpawned && !isBossAmbaDefeated) {
            for (RudalAmba rudal : rudalAmbas) {
                rudal.updatePositionRudal(deltaTime);
                rudal.draw(canvas);
            }
        }

        for (MonsterMini monster : monsterMini) {
            monster.updatePositionMonster(deltaTime);
            monster.draw(canvas);


            if (!isPlayerDefeated && checkCollision(monster, playerShip)) {
                playerShip.reduceHp(monster.getDamage());
                removeMonsters.add(monster); // Hapus monster setelah tabrakan


                if (playerShip.getHp() <= 0) {
                    isPlayerDefeated = true;
                    isPlayerAlive = false;
                    bullets.clear();
                    gameOver = true; // Set game over flag
                    gameActivity.showGameOver(this);
                }
            }
        }


        if(!isPlayerDefeated) {
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

                if (isBossAmbaSpawned && checkCollision(bullet, bossAmba)) {
                    bossAmba.reduceHp(bullet.getDamage());
                    removeBullets.add(bullet);  // Hapus peluru setelah mengenai bos


                    if (bossAmba.getHp() <= 0) {
                        isBossAmbaSpawned = false;
                        isBossAmbaDefeated = true;
                        rudalAmbas.clear();
                    }
                }

                for (RudalAmba rudal : rudalAmbas){
                    if(checkCollision(bullet, rudal)){
                        rudal.reduceDurability(bullet.getDamage());
                        removeBullets.add(bullet);

                        if(rudal.getDurability() <= 0){
                            removeRudal.add(rudal);
                        }
                    }
                }

            }
        }

        monsterMini.removeAll(removeMonsters);
        bullets.removeAll(removeBullets);
        rudalAmbas.removeAll(removeBullets);


        removeOffScreenMonsters();
        removeOffScreenBullets();

        if(score >= 200 && !isBossAmbaSpawned && !isBossAmbaDefeated){
            spawnBossAmba();
        }

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

    private void startShootingRudal(){
        if (isBossAmbaSpawned && !isBossAmbaDefeated) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    shotRudalAmba(); // Menembakkan 1 rudal per kali eksekusi
                    handler.postDelayed(this, 1000); // Ganti interval sesuai keinginan
                }
            }, 100);
        }
    }




    //SHOOT METHOD

    private void shootBullet() {
        int bulletSize = getResources().getDimensionPixelSize(R.dimen.bullet_size);
        float bulletX = playerShip.getShipX() + (playerShip.getShipWidth() / 2) - (bulletSize / 2);
        float bulletY = playerShip.getShipY();
        bullets.add(new Bullet(getContext(), bulletsBitmap, bulletX, bulletY, 2500, bulletSize));
    }

    private void shotRudalAmba(){
        int rudalSize = getResources().getDimensionPixelSize(R.dimen.rudal_size);
        float bossX = bossAmba.getX();
        int bossWidth = bossAmba.getWidth();

        Random random = new Random();
        float randomRudalX = bossX + random.nextInt(bossWidth - rudalSize);
        float rudalY = bossAmba.getY() + (bossAmba.getHeight() / 2) - (rudalSize / 2);

        rudalAmbas.add(new RudalAmba(getContext(),rudalAmba, randomRudalX, rudalY, 1500, rudalSize));
        Log.d("GameView", "New RudalAmba created at: " + randomRudalX + ", " + rudalY);
    }
    //SHOOT METHOD END


    private void spawnBossAmba(){
        int bossWidth = getResources().getDimensionPixelSize(R.dimen.boss_width);
        int spawnX = (screenWidth - bossWidth) / 2;
        bossAmba = new BossAmba(getContext(), bossAmbaBitmap, spawnX, 0,  100);
        isBossAmbaSpawned = true;
        startShootingRudal();
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

    //RUDAL COLLISION
    private boolean checkCollision(Bullet bullet, RudalAmba rudalAmba){
        return  bullet.getX() < rudalAmba.getX() + rudalAmba.getSize() &&
                bullet.getX() + bullet.getSize() > rudalAmba.getX() &&
                bullet.getY() < rudalAmba.getY() + rudalAmba.getSize() &&
                bullet.getY() + bullet.getSize() > rudalAmba.getY();
    }

    //BOSS COLLISION
    private boolean checkCollision(Bullet bullet, BossAmba boss) {
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
        bullets.removeAll(bulletsToRemove);
    }




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
            this.damage = 100;
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

        public BossAmba(Context context, Bitmap bossAmbaBitmap, float x, float y, float velocityY) {
            this.bossAmbaBitmap = bossAmbaBitmap;
            this.x = x;
            this.y = y;
            this.velocityY = velocityY;
            this.hp = levelData.getBossAmbaHp();
            width = context.getResources().getDimensionPixelSize(R.dimen.boss_width);
            height = context.getResources().getDimensionPixelSize(R.dimen.boss_height);
        }

        public void updatePositionBoss(float deltaTime) {
            // Gerakan jatuh bos dan berhenti ketika y = 500
            if (y < 50) {
                y += velocityY * deltaTime;
            }
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
            this.durability = levelData.getRudalDurability();
            this.damage = levelData.getRudalDamage();
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

    }

}

