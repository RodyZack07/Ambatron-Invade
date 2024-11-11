package com.example.savesthekunti.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.savesthekunti.R;


public class MonsterMini {
        private Bitmap monsterMiniBitmap;
        private float x, y;
        private int size;
        private float velocity;
        private int damage;
        private int hp;
        private  Level levelData;
        private Paint paint;

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


