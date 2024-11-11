package com.example.savesthekunti.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.savesthekunti.R;

public class Bullet {

        private Bitmap bulletBitmap;
        private float x, y;
        private int size;
        private float velocity;
        int damage;
        private Paint paint;

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

