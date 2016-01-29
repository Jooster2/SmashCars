package com.smashcars;

import android.os.Bundle;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity{

    RelativeLayout layout_joystick; // Background layout of the joystick (the pad or whatever)
    TextView angleText, directionText; // Writes out the angle (in degrees) and direction of the joystick
    Joystick joystick; // The actual joystick (smaller version that goes on top of the pad)

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        angleText = (TextView)findViewById(R.id.angleText);
        directionText = (TextView)findViewById(R.id.directionText);
        layout_joystick = (RelativeLayout)findViewById(R.id.layout_joystick);

        joystick = new Joystick(getApplicationContext(),layout_joystick);

        layout_joystick.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {

                // Send the motion event to the Joystick class to draw a new joystick
                joystick.drawJoystick(event);
                // Get the action event
                int action = event.getAction();
                // If the joystick is touched or moved, do stuff
                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                    angleText.setText("Angle : " + String.valueOf(joystick.getAngle()));
                    int direction = joystick.getDirection();
                    // Depending on the direction of the joystick, do stuff
                    if (direction == Joystick.JOYSTICK_UP) {
                        directionText.setText("Direction: Up");
                    }
                    else if (direction == Joystick.JOYSTICK_UPRIGHT) {
                        directionText.setText("Direction: Up Right");
                    }
                    else if (direction == Joystick.JOYSTICK_RIGHT) {
                        directionText.setText("Direction: Right");
                    }
                    else if (direction == Joystick.JOYSTICK_DOWNRIGHT) {
                        directionText.setText("Direction: Down Right");
                    }
                    else if (direction == Joystick.JOYSTICK_DOWN) {
                        directionText.setText("Direction: Down");
                    }
                    else if (direction == Joystick.JOYSTICK_DOWNLEFT) {
                        directionText.setText("Direction: Down Left");
                    }
                    else if (direction == Joystick.JOYSTICK_LEFT) {
                        directionText.setText("Direction: Left");
                    }
                    else if (direction == Joystick.JOYSTICK_UPLEFT) {
                        directionText.setText("Direction: Up Left");
                    }
                    else if (direction == Joystick.JOYSTICK_NONE) {
                        directionText.setText("Direction: Center");
                    }
                }
                // When the joystick is released, reset
                else if (action == MotionEvent.ACTION_UP) {
                    angleText.setText("Angle:");
                    directionText.setText("Direction:");
                }
                return true;
            }
        });
    }
}
