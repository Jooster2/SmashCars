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

    private int joystick_ALPHA = 200;
    private int LAYOUT_ALPHA = 200;
    private int OFFSET = 0;

    private Context context;
    private ViewGroup layout;
    private LayoutParams params;

    private int position_x = 0, position_y = 0, min_distance = 0;
    private float distance = 0, angle = 0;

    private DrawCanvas draw;
    private Paint paint;
    private Bitmap joystick;
    private int joystick_width, joystick_height;

    private boolean touch_state = false;

    public Joystick (Context context, ViewGroup layout) {
        this.context = context;
        this.layout = layout;
        draw = new DrawCanvas(context);
        paint = new Paint();
        params = layout.getLayoutParams();

        joystick = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.joystick);
        joystick_width = joystick.getWidth();
        joystick_height = joystick.getHeight();
    }

    public void drawJoystick(MotionEvent event) {
        position_x = (int) (event.getX() - (params.width / 2));
        position_y = (int) (event.getY() - (params.height / 2));
        distance = (float) Math.sqrt(Math.pow(position_x, 2) + Math.pow(position_y, 2));
        angle = (float) cal_angle(position_x, position_y);


        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if(distance <= (params.width) - OFFSET) {
                draw.position(params.width/2, params.height/2);
                draw();
                touch_state = true;
            }
        } else if(event.getAction() == MotionEvent.ACTION_MOVE && touch_state) {
            if(distance <= (params.width / 2) - OFFSET) {
                draw.position(event.getX(), event.getY());
                draw();
            } else if(distance > (params.width / 2) - OFFSET){
                float x = (float) (Math.cos(Math.toRadians(cal_angle(position_x,
                        position_y))) * ((params.width / 2) - OFFSET));
                float y = (float) (Math.sin(Math.toRadians(cal_angle(position_x,
                        position_y))) * ((params.height / 2) - OFFSET));
                x += (params.width / 2);
                y += (params.height / 2);
                draw.position(x, y);
                draw();
            } else {
                layout.removeView(draw);
            }
        } else if(event.getAction() == MotionEvent.ACTION_UP) {
            layout.removeView(draw);
            touch_state = false;
        }
    }

    public int[] getPosition() {
        if(distance > min_distance && touch_state) {
            return new int[] { position_x, position_y };
        }
        return new int[] { 0, 0 };
    }

    public int getX() {
        if(distance > min_distance && touch_state) {
            return position_x;
        }
        return 0;
    }

    public int getY() {
        if(distance > min_distance && touch_state) {
            return position_y;
        }
        return 0;
    }

    public float getAngle() {
        if(distance > min_distance && touch_state) {
            return angle;
        }
        return 0;
    }

    public float getDistance() {
        if(distance > min_distance && touch_state) {
            return distance;
        }
        return 0;
    }

    public void setMinimumDistance(int minDistance) {
        min_distance = minDistance;
    }

    public int getMinimumDistance() {
        return min_distance;
    }

    public int get8Direction() {
        if(distance > min_distance && touch_state) {
            if(angle >= 247.5 && angle < 292.5 ) {
                return JOYSTICK_UP;
            } else if(angle >= 292.5 && angle < 337.5 ) {
                return JOYSTICK_UPRIGHT;
            } else if(angle >= 337.5 || angle < 22.5 ) {
                return JOYSTICK_RIGHT;
            } else if(angle >= 22.5 && angle < 67.5 ) {
                return JOYSTICK_DOWNRIGHT;
            } else if(angle >= 67.5 && angle < 112.5 ) {
                return JOYSTICK_DOWN;
            } else if(angle >= 112.5 && angle < 157.5 ) {
                return JOYSTICK_DOWNLEFT;
            } else if(angle >= 157.5 && angle < 202.5 ) {
                return JOYSTICK_LEFT;
            } else if(angle >= 202.5 && angle < 247.5 ) {
                return JOYSTICK_UPLEFT;
            }
        } else if(distance <= min_distance && touch_state) {
            return JOYSTICK_NONE;
        }
        return 0;
    }

    public int get4Direction() {
        if(distance > min_distance && touch_state) {
            if(angle >= 225 && angle < 315 ) {
                return JOYSTICK_UP;
            } else if(angle >= 315 || angle < 45 ) {
                return JOYSTICK_RIGHT;
            } else if(angle >= 45 && angle < 135 ) {
                return JOYSTICK_DOWN;
            } else if(angle >= 135 && angle < 225 ) {
                return JOYSTICK_LEFT;
            }
        } else if(distance <= min_distance && touch_state) {
            return JOYSTICK_NONE;
        }
        return 0;
    }

    public void setOffset(int offset) {
        OFFSET = offset;
    }

    public int getOffset() {
        return OFFSET;
    }

    public void setJoystickAlpha(int alpha) {
        joystick_ALPHA = alpha;
        paint.setAlpha(alpha);
    }

    public int getJoystickAlpha() {
        return joystick_ALPHA;
    }

    public void setLayoutAlpha(int alpha) {
        LAYOUT_ALPHA = alpha;
        layout.getBackground().setAlpha(alpha);
    }

    public int getLayoutAlpha() {
        return LAYOUT_ALPHA;
    }

    public void setJoystickSize(int width, int height) {
        joystick = Bitmap.createScaledBitmap(joystick, width, height, false);
        joystick_width = joystick.getWidth();
        joystick_height = joystick.getHeight();
    }

    public void setJoystickWidth(int width) {
        joystick = Bitmap.createScaledBitmap(joystick, width, joystick_height, false);
        joystick_width = joystick.getWidth();
    }

    public void setJoystickHeight(int height) {
        joystick = Bitmap.createScaledBitmap(joystick, joystick_width, height, false);
        joystick_height = joystick.getHeight();
    }

    public int getJoystickWidth() {
        return joystick_width;
    }

    public int getJoystickHeight() {
        return joystick_height;
    }

    public void setLayoutSize(int width, int height) {
        params.width = width;
        params.height = height;
    }

    public int getLayoutWidth() {
        return params.width;
    }

    public int getLayoutHeight() {
        return params.height;
    }

    private double cal_angle(float x, float y) {
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

    private void draw() {
        try {
            layout.removeView(draw);
        } catch (Exception e) { }
        layout.addView(draw);
    }

    private class DrawCanvas extends View{
        float x, y;

        private DrawCanvas(Context mContext) {
            super(mContext);
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
