package com.example.savesthekunti;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;

public class GameView extends View {

    private Bitmap playerShip;
    private Bitmap background;
    private Bitmap scaledShip;

    private float shipX, shipY;
    private Paint paint;




    public GameView(Context context) {
        super(context);
        // Inisialisasi gambar dan posisi pesawat
        playerShip = BitmapFactory.decodeResource(getResources(), R.drawable.blue_cosmos);
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background_gameplay);
        shipX = 100;
        shipY = 1200;
        paint = new Paint();

        scaledShip = Bitmap.createScaledBitmap(playerShip, 250, 250, false);
    }

        //DRAW ON CANVAS
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Menggambar background
        canvas.drawBitmap(background, 0, 0, paint);

        // Menggambar pesawat
        canvas.drawBitmap(scaledShip, shipX, shipY, paint);

        // Log.d("GameView", "onDraw called, ship position: " + shipX + ", " + shipY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Menggerakkan pesawat sesuai posisi sentuhan (horizontal saja)
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float touchX = event.getX();
            shipX = touchX - (scaledShip.getWidth() / 2) ;
            invalidate(); //Update OnDraw
        }
        return true;
    }
}
