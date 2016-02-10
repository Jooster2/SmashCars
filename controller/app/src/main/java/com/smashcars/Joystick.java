package com.smashcars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Joystick {

    protected double MIN_DIST_TO_MID = 0.3; // States the minimum distance position need from the middle

    protected Context context;
    protected RelativeLayout layout;
    protected LayoutParams params;
    protected DrawCanvas draw;
    protected Paint paint;
    protected Bitmap joystick;

    protected int joystick_width, joystick_height; // Size of the joystick
    protected double position = 0; // Position of the joystick [-1,1] = {x,y in R | -1 <= x,y <= 1}

    protected boolean isTouched = false; // Checks if joystick

    public Joystick (Context context, RelativeLayout layout) {
        this.context = context;
        this.layout = layout;
        draw = new DrawCanvas(context);
        paint = new Paint();

        // Get the width/height of the layout
        params = layout.getLayoutParams();

        // Get the image of the joystick
        joystick = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.joystick_pressed);
        // Set values for joystick (half the size of the layout)
        joystick_width = params.width/2;
        joystick_height = params.height/2;
        // Create a scaled version (smaller than the background layout)
        joystick = Bitmap.createScaledBitmap(joystick, joystick_width, joystick_height,
                false);
    }

    protected double getPosition() {
        BigDecimal bd = new BigDecimal(position);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        position = bd.doubleValue();
        // Check if the position is far enough from the middle
        if(position > MIN_DIST_TO_MID || position < (-1)*MIN_DIST_TO_MID)
            return position;
        else
            return 0;
    }

    public boolean checkInsideBoundries() {
        if(position >= -1 && position <= 1) {
            return true;
        }
        return false;
    }

    protected void draw() {
        // When drawing a new joystick, remove old on to remove clutter
        layout.removeView(draw);
        layout.addView(draw);
    }

    protected class DrawCanvas extends View{
        float x, y;

        private DrawCanvas(Context context) {
            super(context);
        }

        public void onDraw(Canvas canvas) {
            canvas.drawBitmap(joystick, x, y, paint);
        }

        public void position(float pos_x, float pos_y) {
            x = pos_x - (joystick_width / 2);
            y = pos_y - (joystick_height / 2);
        }
    }
}