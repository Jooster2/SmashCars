package com.smashcars;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

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

        position = (double)(-1)*(event.getY() - params.height/2)/(params.height/2);

        // If the joystick pad is touched, draw a joystick
        if(action == MotionEvent.ACTION_DOWN) {
            draw.position(params.width/2, event.getY());
            draw();
            isTouched = true;
        }
        // If the finger moves over the joystick pad, draw a new joystick
        else if(action == MotionEvent.ACTION_MOVE && isTouched) {
            // If the joystick is inside of the pad
            if(checkInsideBoundries()) {
                draw.position(params.width/2, event.getY());
                draw();
            }
            // If the joystick is outside of the pad
            else {
                if(position < 0) {
                    position = -1;
                    draw.position(params.width/2, params.height);
                    draw();
                }
                else if(position > 0) {
                    position = 1;
                    draw.position(params.width/2, 0);
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

    public double getPosition() { return super.getPosition(); }
}