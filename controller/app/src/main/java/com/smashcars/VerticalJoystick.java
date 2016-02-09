package com.smashcars;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Jonathan on 2016-02-09.
 */

public class VerticalJoystick extends Joystick {

    public VerticalJoystick (Context context, RelativeLayout layout) {
        super(context, layout);
    }

    public void drawJoystick(MotionEvent event) {

        // Get the motion event
        int action = event.getAction();
        // If touchscreen is pressed
        if(action == MotionEvent.ACTION_DOWN) {

            // If the touchpad area is pressed, draw a joystick
            if(checkInside(event)) {
                position = (double)(event.getY() - params.height/2)/(params.height/2);
                draw.position(params.width/2, event.getY());
                draw();
                isTouched = true;
            }

            // Else, dont draw a joystick
        }
        else if(action == MotionEvent.ACTION_MOVE && isTouched) {
            // If the joystick is inside of the pad
            if(checkInside(event)) {
                position = (double)(event.getY() - params.width/2)/(params.width/2);
                draw.position(params.width/2, event.getY());
                draw();
            }
            // If the joystick is outside of the pad
            else {
                if(position < 0) {
                    position = -1;
                    draw.position(params.width/2, 0);
                    draw();
                }
                else if(position > 0) {
                    position = 1;
                    draw.position(params.width/2, params.height);
                    draw();
                }
            }
        }
        // If the joystick is released, remove the joystick
        else {
            layout.removeView(draw);
            isTouched = false;
        }
    }

    public double getYpos() {
        BigDecimal bd = new BigDecimal(position);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        position = bd.doubleValue();
        if(enoughDistance(position))
            return position;
        else
            return 0;
    }
}