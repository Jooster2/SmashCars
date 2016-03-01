package com.smashcars.joystick;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.test.suitebuilder.annotation.Suppress;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smashcars.core.MainActivity;
import com.smashcars.R;

/**
 * Created by Jonathan on 2016-02-10.
 */
public class JoystickFragment extends Fragment{

    private static final String TAG = "JoystickFragment";
    private char powerup = '#';
    private int numberOfPowerups = 0;
    private int lockMotorSpeed = -1;
    private int lockServoAngle = -1;
    private boolean servoReverse = false;
    private boolean motorReverse = false;

    private static final int SERVO_STRAIGHT_AHEAD = 90;
    private static final int SERVO_MAXIMUM_ANGLE = 50;
    private static final int MOTOR_STOP = 256;
    private static final int MOTOR_FORWARD = 768;
    private static final int MOTOR_BACKWARD = 256;
    private static final int MOTOR_FULL_SPEED = 200;

    private String macAddress;

    private static final String CAR_1_MAC_ADDRESS = "00:06:66:7B:AC:2C";
    private static final String CAR_2_MAC_ADDRESS = "00:06:66:7B:AB:CA";

    int lastMotor = 256;
    int lastServo = 90;
    private int previousValue = 0;

    RelativeLayout horizontal_joystick, vertical_joystick; // Background layout of the joystick (the pad or whatever)
    HorizontalJoystick h_joystick; // The actual joystick (smaller version that goes on top of the pad)
    VerticalJoystick v_joystick;
    RadioButton car1Button;
    RadioButton car2Button;
    //ImageButton powerupButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.joystick_fragment, container, false);

        final MainActivity activity = (MainActivity)getActivity();

        horizontal_joystick = (RelativeLayout)view.findViewById(R.id.horizontal_joystick);
        vertical_joystick = (RelativeLayout)view.findViewById(R.id.vertical_joystick);
        car1Button = (RadioButton)view.findViewById(R.id.car1Button);
        car2Button = (RadioButton)view.findViewById(R.id.car2Button);

        car1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                macAddress = CAR_1_MAC_ADDRESS;
            }

        });

        car2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                macAddress = CAR_2_MAC_ADDRESS;
            }
        });
        //powerupButton = (ImageButton)view.findViewById(R.id.powerButton);

        h_joystick = new HorizontalJoystick(getActivity().getApplicationContext(), horizontal_joystick, 0);
        v_joystick = new VerticalJoystick(getActivity().getApplicationContext(), vertical_joystick, 0.3);

        horizontal_joystick.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                // Send the motion event to the Joystick class to draw a new joystick
                h_joystick.drawJoystick(event);
                // Get the action event

                int action = event.getAction();
                int returnData = 0;
                // If the joystick is touched or moved, do stuff
                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                    double x_pos = h_joystick.getPosition();

                    if(servoReverse)
                        x_pos = -x_pos;
                    // Depending on the direction of the joystick, do stuff
                    if (x_pos < 0) {
                        returnData = SERVO_STRAIGHT_AHEAD + (int)(SERVO_MAXIMUM_ANGLE  * x_pos);
                        lastServo = returnData;
                    } else if (x_pos >= 0) {
                        returnData = SERVO_STRAIGHT_AHEAD + (int) (SERVO_MAXIMUM_ANGLE * x_pos);
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
                    double y_pos = v_joystick.getPosition();

                    if(lockMotorSpeed != -1) {
                        returnData = 768 + lockMotorSpeed;

                    }
                    // Depending on the direction of the joystick, do stuff
                    else if (y_pos >= 0) {
                        if(!motorReverse)
                            returnData = MOTOR_FORWARD + (int)(MOTOR_FULL_SPEED * y_pos);
                        else
                            returnData = MOTOR_BACKWARD + (int)(MOTOR_FULL_SPEED * y_pos);
                        lastMotor = returnData;

                    } else if (y_pos < 0) {
                        if(!motorReverse)
                            returnData = MOTOR_BACKWARD + (int)(MOTOR_FULL_SPEED  * (-1) * y_pos);
                        else
                            returnData = MOTOR_FORWARD + (int)(MOTOR_FULL_SPEED  * (-1) * y_pos);
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
                    activity.stopMotor(lastServo);
                    lastMotor = 256;
                }

                return true;
            }
        });

        return view;
    }

    public void setPowerup(char power) {
        if(powerup == '#')
            return;
        powerup = power;
        switch(power) {
            //Saveable powers
            case 'B': numberOfPowerups = 1; break;
            case 'b': numberOfPowerups = 3; break;
            case 'I': numberOfPowerups = 1; break;

            //Immediate powers
            case 'N': numberOfPowerups = 1; usePowerup(null); break;
            case 'S': numberOfPowerups = 1; usePowerup(null); break;
            case 'W': numberOfPowerups = 1; usePowerup(null); break;
            case 'R': numberOfPowerups = 1; usePowerup(null); break;
        }


    }

    private void usePowerup(View v) {
        if(numberOfPowerups == 0)
            return;
        switch(powerup) {
            case 'B': lockMotorSpeed = 255; new Timer(2500, powerup).start(); break;
            case 'b': lockMotorSpeed = 255; new Timer(2500, powerup).start(); break;
            case 'I': ((MainActivity)getActivity()).setImmortal(true); new Timer(10000, powerup).start(); break;
            case 'N': ((MainActivity)getActivity()).changeHitpoints(1); break;
            case 'S': lockMotorSpeed = 50; new Timer(5000, powerup).start(); break;
            case 'R': servoReverse = true; motorReverse = true; new Timer(10000, powerup).start(); break;
            case 'W': lockMotorSpeed = 0; new Timer(1000, powerup).start(); break;
            case 'T': break;
        }

        //setPowerupImage();

        numberOfPowerups--;
        if(numberOfPowerups == 0)
            powerup = '#';


    }

    public void stopPowerup(char powerup) {
        switch(powerup) {
            case 'B': lockMotorSpeed = -1; break;
            case 'b': lockMotorSpeed = -1; break;
            case 'I': ((MainActivity)getActivity()).setImmortal(false); break;
            case 'S': lockMotorSpeed = -1; break;
            case 'R': servoReverse = false; motorReverse = false; break;
            case 'W': lockMotorSpeed = -1; break;
            default: break;
        }
    }

    /*private void setPowerupImage() {
        Context context = (MainActivity)getActivity();
        powerupButton.setImageResource(context.getResources().getIdentifier(
                Character.toString(powerup), "drawable", context.getPackageName()));

    }*/

    public void setServoReverse(boolean reverse) {
        servoReverse = reverse;
    }

    private class Timer extends Thread {
        private int milliseconds;
        private char powerup;

        public Timer(int milli, char power) {
            milliseconds = milli;
            powerup = power;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                sleep(milliseconds);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

            ((MainActivity)getActivity()).stopPowerup(powerup);
        }
    }

    public String getMacAddress() {
        return macAddress;
    }
}
