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
    private Bitmap background;
    private List<MonsterMini> monsterMini;
    private int screenWidth, screenHeight;
    private Handler handler;
    private Paint paint;

    private int spawnGroup = 0;
    private long lastFrameTime = 0;



    public GameView(Context context) {
        super(context);
        // Inisialisasi gambar dan posisi pesawat

        background = BitmapFactory.decodeResource(getResources(), R.drawable.background_gameplay);
        paint = new Paint();

        playerShip = new PlayerShip(context);
        monsterMini = new ArrayList<>();

        post(()  ->{
            screenWidth = getWidth();
            screenHeight = getHeight();
            playerShip.setShipPosition(screenWidth, screenHeight);
            spawnMonsterMini();
            startRespawn();
        });
    }

    public void spawnMonsterMini() {
        Random random = new Random();
        Bitmap monsterMiniBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.monster_mini);
        int monsterSize = 100;
        int rows = (spawnGroup % 2 == 0) ? 3 : 4; // Alternatif pola monster
        int spacing = 15;

        // Posisi Spawn monstermini
        int startY = 0; // Tetapkan Y di posisi 0 (bagian atas layar)

        for (int i = 0; i < rows; i++) {
            int cols = (spawnGroup % 2 == 0) ? 4 : i + 1; // Untuk pola persegi panjang atau segitiga
            for (int j = 0; j < cols; j++) {
                // Menghasilkan posisi X acak dalam batas layar
                int randomX = random.nextInt(screenWidth - monsterSize);
                int y = startY + i * (monsterSize + spacing);  // Posisi Y masih berurutan
                monsterMini.add(new MonsterMini(monsterMiniBitmap, randomX, y, 400, monsterSize));
            }
        }
        spawnGroup++;
    }


    //DRAW ON CANVAS
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

            invalidate();
        }



    private void startRespawn(){
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                spawnMonsterMini();
                invalidate();
                handler.postDelayed(this, 3000);
            }
        },3000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        return playerShip.handleTouch(event);
    }



                     //CLASSS PLAYERSHIP
    class PlayerShip {
        private Bitmap playerShipBitmap;
        private float shipX, shipY;
        private int shipWidth, shipHeight;

        //Class untuk membuat pesawat
        public PlayerShip (Context context){
            playerShipBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_cosmos);
            shipWidth = 250;
            shipHeight = 250;
        }

        // Set posisi pesawat
        public void setShipPosition (int screenWidth, int screenHeight){
            shipX = (screenWidth - shipWidth) / 2;
            shipY = screenHeight - shipHeight - 50;
        }

        //Gambar pesawat
        public void draw(Canvas canvas){
            canvas.drawBitmap(Bitmap.createScaledBitmap(playerShipBitmap, shipWidth, shipHeight, false), shipX, shipY, paint);
        }


        public boolean handleTouch(MotionEvent event) {
            // Menggerakkan pesawat sesuai posisi sentuhan (horizontal saja)
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                float touchX = event.getX();
                shipX = touchX - (shipWidth / 2) ;
                invalidate();
            }
            return true;
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


    //CLASS MONSTER MINI
    class MonsterMini {

        private Bitmap mosterMiniBitmap;
        private float x, y;
        private int size;
        private float velocity;


        public MonsterMini(Bitmap monsterMiniBitmap, float x, float y, float velocityY, int size){
            this.mosterMiniBitmap = monsterMiniBitmap;
            this.x = x;
            this.y = y;
            this.velocity = velocityY;
            this.size = size;
        }

        public void updatePositionMonster(float deltaTime){
            y += velocity * deltaTime;
        }

        public void draw(Canvas canvas){
            canvas.drawBitmap(Bitmap.createScaledBitmap(mosterMiniBitmap, size, size, false), x, y, paint);
        }

        public boolean isOffScreen(int screenHeight) {
            return y > screenHeight;  // Jika y lebih besar dari tinggi layar, monster dianggap keluar
        }

    }


}




