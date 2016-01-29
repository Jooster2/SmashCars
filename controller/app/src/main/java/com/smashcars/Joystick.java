package com.smashcars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class Joystick {

    //
    public static final int JOYSTICK_NONE = 0;
    public static final int JOYSTICK_UP = 1;
    public static final int JOYSTICK_UPRIGHT = 2;
    public static final int JOYSTICK_RIGHT = 3;
    public static final int JOYSTICK_DOWNRIGHT = 4;
    public static final int JOYSTICK_DOWN = 5;
    public static final int JOYSTICK_DOWNLEFT = 6;
    public static final int JOYSTICK_LEFT = 7;
    public static final int JOYSTICK_UPLEFT = 8;

    private int OFFSET = 50;

    private Context context;
    private ViewGroup layout;
    private LayoutParams params;
    private DrawCanvas draw;
    private Paint paint;
    private Bitmap joystick;

    private int joystick_width, joystick_height;
    private int pos_x = 0, pos_y = 0, min_dis = 0; // min_dis states how far from the middle the joystick need to go in order to update
    private float distance = 0, angle = 0; // distance from middle and angle

    private boolean isTouched = false;

    public Joystick (Context context, ViewGroup layout) {
        this.context = context;
        this.layout = layout;
        draw = new DrawCanvas(context);
        paint = new Paint();

        params = layout.getLayoutParams();
        // Get the image of the joystick
        joystick = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.joystick_pressed);
        joystick_width = params.width/2;
        joystick_height = params.height/2;
        // Create a scaled version (smaller than the background layout)
        joystick = Bitmap.createScaledBitmap(joystick, joystick_width, joystick_height,
                false);
    }

    public void drawJoystick(MotionEvent event) {

        pos_x = (int) (event.getX() - (params.width / 2));
        pos_y = (int) (event.getY() - (params.height / 2));
        distance = (float) Math.sqrt(Math.pow(pos_x, 2) + Math.pow(pos_y, 2));
        angle = (float) calculateAngle(pos_x, pos_y);

        // Get the motion event
        int action = event.getAction();
        // Depending on the event, do stuff
        if(action == MotionEvent.ACTION_DOWN) {
            if(distance <= (params.width) - OFFSET) {
                draw.position(event.getX(), event.getY());
                draw();
                isTouched = true;
            }
        }
        else if(action == MotionEvent.ACTION_MOVE && isTouched) {
            // If the joystick is inside of the pad
            if(distance <= (params.width / 2) - OFFSET) {
                draw.position(event.getX(), event.getY());
                draw();
            }
            // If the joystick is outside of the pad
            else if(distance > (params.width / 2) - OFFSET){
                float x = (float) (Math.cos(Math.toRadians(calculateAngle(pos_x,
                        pos_y))) * ((params.width / 2) - OFFSET));
                float y = (float) (Math.sin(Math.toRadians(calculateAngle(pos_x,
                        pos_y))) * ((params.height / 2) - OFFSET));
                x += (params.width / 2);
                y += (params.height / 2);
                draw.position(x, y);
                draw();
            }
            // Otherwise, just remove to joystick as to avoid clutter
            else {
                layout.removeView(draw);
            }
        }
        // If the joystick is released, remove the joystick
        else if(action == MotionEvent.ACTION_UP) {
            layout.removeView(draw);
            isTouched = false;
        }
    }

    public float getAngle() {
        if(distance > min_dis && isTouched) {
            return angle;
        }
        return 0;
    }

    public int getDirection() {
        if(distance > min_dis && isTouched) {
            if(angle >= 247.5 && angle < 292.5 ) {
                return JOYSTICK_UP;
            }
            else if(angle >= 292.5 && angle < 337.5 ) {
                return JOYSTICK_UPRIGHT;
            }
            else if(angle >= 337.5 || angle < 22.5 ) {
                return JOYSTICK_RIGHT;
            }
            else if(angle >= 22.5 && angle < 67.5 ) {
                return JOYSTICK_DOWNRIGHT;
            }
            else if(angle >= 67.5 && angle < 112.5 ) {
                return JOYSTICK_DOWN;
            }
            else if(angle >= 112.5 && angle < 157.5 ) {
                return JOYSTICK_DOWNLEFT;
            }
            else if(angle >= 157.5 && angle < 202.5 ) {
                return JOYSTICK_LEFT;
            }
            else if(angle >= 202.5 && angle < 247.5 ) {
                return JOYSTICK_UPLEFT;
            }
        }
        else if(distance <= min_dis && isTouched) {
            return JOYSTICK_NONE;
        }
        return 0;
    }
    private void draw() {
        // When drawing a new joystick, remove old on to remove clutter
        layout.removeView(draw);
        layout.addView(draw);
    }

    private double calculateAngle(float x, float y) {
        if(x >= 0 && y >= 0)
            return Math.toDegrees(Math.atan(y / x));
        else if(x < 0 && y >= 0)
            return Math.toDegrees(Math.atan(y / x)) + 180;
        else if(x < 0 && y < 0)
            return Math.toDegrees(Math.atan(y / x)) + 180;
        else if(x >= 0 && y < 0)
            return Math.toDegrees(Math.atan(y / x)) + 360;
        return 0;
    }

    private class DrawCanvas extends View{
        float x, y;

        private DrawCanvas(Context context) {
            super(context);
        }

        public void onDraw(Canvas canvas) {
            canvas.drawBitmap(joystick, x, y, paint);
        }

        private void position(float pos_x, float pos_y) {
            x = pos_x - (joystick_width / 2);
            y = pos_y - (joystick_height / 2);
        }
    }
}
