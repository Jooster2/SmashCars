package com.smashcars;

import android.os.Bundle;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity{

    RelativeLayout horizontal_joystick, vertical_joystick; // Background layout of the joystick (the pad or whatever)
    TextView xposText, yposText, directionText, directionText2; // Writes out the angle (in degrees) and direction of the joystick
    HorizontalJoystick h_joystick; // The actual joystick (smaller version that goes on top of the pad)
    VerticalJoystick v_joystick;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xposText = (TextView)findViewById(R.id.xposText);
        yposText = (TextView)findViewById(R.id.yposText);
        directionText2 = (TextView)findViewById(R.id.directionText2);
        directionText = (TextView)findViewById(R.id.directionText);
        horizontal_joystick = (RelativeLayout)findViewById(R.id.horizontal_joystick);
        vertical_joystick = (RelativeLayout)findViewById(R.id.vertical_joystick);

        h_joystick = new HorizontalJoystick(getApplicationContext(),horizontal_joystick, 0.15);
        v_joystick = new VerticalJoystick(getApplicationContext(),vertical_joystick, 0.3);

        horizontal_joystick.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {

                // Send the motion event to the Joystick class to draw a new joystick
                h_joystick.drawJoystick(event);
                // Get the action event
                int action = event.getAction();
                // If the joystick is touched or moved, do stuff
                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                    xposText.setText("X: " + h_joystick.getPosition());
                    double x_pos = h_joystick.getPosition();
                    // Depending on the direction of the joystick, do stuff
                    if (x_pos < 0) {
                        directionText.setText("Direction: Left");
                    }

                    else if (x_pos > 0) {
                        directionText.setText("Direction: Right");
                    }

                    else {
                        directionText.setText("Direction: Center");
                    }
                }
                // When the joystick is released, reset
                else if (action == MotionEvent.ACTION_UP) {
                    xposText.setText("X:");
                    directionText.setText("Direction:");
                }
                return true;
            }
        });


        vertical_joystick.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {

                // Send the motion event to the Joystick class to draw a new joystick
                v_joystick.drawJoystick(event);
                // Get the action event
                int action = event.getAction();
                // If the joystick is touched or moved, do stuff
                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                    yposText.setText("Y: " + v_joystick.getPosition());
                    double y_pos = v_joystick.getPosition();
                    // Depending on the direction of the joystick, do stuff
                    if (y_pos > 0) {
                        directionText2.setText("Direction: Up");
                    }

                    else if (y_pos < 0) {
                        directionText2.setText("Direction: Down");
                    }

                    else {
                        directionText2.setText("Direction: Center");
                    }
                }
                // When the joystick is released, reset
                else if (action == MotionEvent.ACTION_UP) {
                    yposText.setText("Y:");
                    directionText2.setText("Direction:");
                }
                return true;
            }
        });
    }
}
