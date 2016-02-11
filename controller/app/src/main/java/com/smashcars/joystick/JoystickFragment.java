package com.smashcars.joystick;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smashcars.core.MainActivity;
import com.smashcars.R;

/**
 * Created by Jonathan on 2016-02-10.
 */
public class JoystickFragment extends Fragment{

    private static final String TAG = "JoystickFragment";

    private static final int SERVO_STRAIGHT_AHEAD = 90;
    private static final int SERVO_MAXIMUM_ANGLE = 35;
    private static final int MOTOR_STOP = 256;
    private static final int MOTOR_FORWARD = 768;
    private static final int MOTOR_BACKWARD = 256;
    private static final int MOTOR_FULL_SPEED = 255;
    int lastMotor = 256;
    int lastServo = 90;
    private int previousValue = 0;
    RelativeLayout horizontal_joystick, vertical_joystick; // Background layout of the joystick (the pad or whatever)
    TextView xposText, yposText, directionText, directionText2; // Writes out the angle (in degrees) and direction of the joystick
    HorizontalJoystick h_joystick; // The actual joystick (smaller version that goes on top of the pad)
    VerticalJoystick v_joystick;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.joystick_fragment, container, false);

        final MainActivity activity = (MainActivity)getActivity();

        xposText = (TextView)view.findViewById(R.id.xposText);
        yposText = (TextView)view.findViewById(R.id.yposText);
        directionText2 = (TextView)view.findViewById(R.id.directionText2);
        directionText = (TextView)view.findViewById(R.id.directionText);
        horizontal_joystick = (RelativeLayout)view.findViewById(R.id.horizontal_joystick);
        vertical_joystick = (RelativeLayout)view.findViewById(R.id.vertical_joystick);

        h_joystick = new HorizontalJoystick(getActivity().getApplicationContext(),horizontal_joystick, 0);
        v_joystick = new VerticalJoystick(getActivity().getApplicationContext(),vertical_joystick, 0.3);

        horizontal_joystick.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                // Send the motion event to the Joystick class to draw a new joystick
                h_joystick.drawJoystick(event);
                // Get the action event

                int action = event.getAction();
                int returnData = 0;
                // If the joystick is touched or moved, do stuff
                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                    xposText.setText("X: " + h_joystick.getPosition());
                    double x_pos = h_joystick.getPosition();
                    // Depending on the direction of the joystick, do stuff
                    if (x_pos < 0) {
                        returnData = SERVO_STRAIGHT_AHEAD + (int)(SERVO_MAXIMUM_ANGLE  * x_pos);
                        directionText.setText("Direction: Left");
                        lastServo = returnData;
                    } else if (x_pos >= 0) {
                        returnData = SERVO_STRAIGHT_AHEAD + (int) (SERVO_MAXIMUM_ANGLE * x_pos);
                        directionText.setText("Direction: Right");
                        lastServo = returnData;
                    }
                    if(previousValue != returnData) {
                        Log.d(TAG, "Servo data: " + returnData);
                        activity.addCommand(returnData);
                        previousValue = returnData;
                    }
                }
                // When the joystick is released, reset
                else if (action == MotionEvent.ACTION_UP) {
                    returnData = SERVO_STRAIGHT_AHEAD;

                    //for(int i = 0 ; i <= 20; i++)
                       // activity.addCommand(returnData);
                    xposText.setText("X:");
                    directionText.setText("Direction:");
                    activity.stopServo(lastMotor);
                    lastServo = 90;

                }


                return true;
            }
        });

        vertical_joystick.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {

                // Send the motion event to the Joystick class to draw a new joystick
                v_joystick.drawJoystick(event);
                // Get the action event
                int action = event.getAction();
                int returnData = 0;
                // If the joystick is touched or moved, do stuff
                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                    yposText.setText("Y: " + v_joystick.getPosition());
                    double y_pos = v_joystick.getPosition();
                    // Depending on the direction of the joystick, do stuff
                    if (y_pos >= 0) {
                        returnData = MOTOR_FORWARD + (int)(MOTOR_FULL_SPEED * y_pos);
                        directionText2.setText("Direction: Up");
                        lastMotor = returnData;

                    } else if (y_pos < 0) {
                        returnData = MOTOR_BACKWARD + (int)(MOTOR_FULL_SPEED  * (-1) * y_pos);
                        directionText2.setText("Direction: Down");
                        lastMotor = returnData;
                    }

                    if(previousValue != returnData) {
                        Log.d(TAG, "Motor data: " + returnData);
                        activity.addCommand(returnData);
                        previousValue = returnData;
                    }
                }
                // When the joystick is released, reset
                else if (action == MotionEvent.ACTION_UP) {
                    // Stop the engine
                    returnData = MOTOR_STOP;
                    //for(int i = 0 ; i <= 20; i++)
                        //activity.addCommand(returnData);
                    yposText.setText("Y:");
                    directionText2.setText("Direction:");
                    activity.stopMotor(lastServo);
                    lastMotor = 256;
                }

                return true;
            }
        });

        return view;
    }
}
