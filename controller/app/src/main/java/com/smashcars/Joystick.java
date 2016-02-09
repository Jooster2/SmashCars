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

public class Joystick {

    protected Context context;
    protected RelativeLayout layout;
    protected LayoutParams params;
    protected DrawCanvas draw;
    protected Paint paint;
    protected Bitmap joystick;

    protected int joystick_width, joystick_height; // Size of the joystick
    protected double position = 0, min_dis = 0.3; // min_dis states how far from the middle the joystick need to go in order to update
    protected int min_x, max_x, min_y, max_y; // The min/max x and y values of the layout

    protected boolean isTouched = false; // Checks if joystick

    public Joystick (Context context, RelativeLayout layout) {
        this.context = context;
        this.layout = layout;
        draw = new DrawCanvas(context);
        paint = new Paint();

        // Get the width/height of the layout
        params = layout.getLayoutParams();
        // Set min/max x and y value
        min_x = 0 - params.width/2;
        max_x = params.width/2;
        min_y = 0 - params.height/2;
        max_y = params.height/2;
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

    protected boolean checkInside(MotionEvent event) {
        if(((event.getX() - params.width/2) > min_x) &&
                (event.getX() - params.width/2) < max_x) {

            if(((event.getY() - params.height/2) > min_y) &&
                    ((event.getY() - params.height/2) < max_y)) {
                return true;
            }
            return false;
        }
        return false;
    }

    protected boolean enoughDistance(double position) {
        if((position > min_dis) || (position < (-1)*min_dis)) {
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
