package com.smashcars;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.MotionEvent;
import android.os.Handler;
import android.widget.Toast;
import android.content.Context;

/**
 * Created by Jeppe on 2016-01-21.
 */
public class MainFragment extends Fragment
{
    private static final int SERVO = 0;
    private static final int TURN_LEFT = 135;
    private static final int TURN_RIGHT = 45;
    private static final int MOTOR = 256;
    private static final int STOP = 0;
    private static final int ACCELERATE = 180;
    private static final int DIR_FORWARD = 0;
    private static final int BACKWARD = 512;
    private static final int BRAKE_OFF = 0;
    private static final int BRAKE_ON = 1024;

    private static final String TAG = "mainfragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        Button button = (Button) view.findViewById(R.id.button);
        button.setOnTouchListener(new View.OnTouchListener()
        {

            Context context = getActivity();
            CharSequence text = "GO DIR_FORWARD!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);


            private Handler mHandler;

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        Log.i(TAG, "Forwards");
                        mHandler.postDelayed(mAction, 50);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable()
            {
                @Override
               public void run()
                {
                    toast.show();
                    ((MainActivity)getActivity()).addCommand(MOTOR + DIR_FORWARD + BRAKE_OFF + ACCELERATE);
                    mHandler.postDelayed(this, 0);
                }
            };
        });

        Button buttonLeft = (Button) view.findViewById(R.id.button2);
        buttonLeft.setOnTouchListener(new View.OnTouchListener()
        {

            Context context = getActivity();
            CharSequence text = "TURN LEFT!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);

            private Handler mHandler;

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        Log.i(TAG, "Turn left");
                        mHandler.postDelayed(mAction, 50);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable()
            {
                @Override
                public void run()
                {
                    toast.show();
                    ((MainActivity)getActivity()).addCommand(SERVO + TURN_LEFT);
                    mHandler.postDelayed(this, 0);
                }
            };
        });

        Button buttonRight = (Button) view.findViewById(R.id.button3);
        buttonRight.setOnTouchListener(new View.OnTouchListener()
        {

            Context context = getActivity();
            CharSequence text = "TURN RIGHT!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);

            private Handler mHandler;

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        Log.i(TAG, "Turn right");
                        mHandler.postDelayed(mAction, 50);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable()
            {
                @Override
                public void run()
                {
                    toast.show();
                    ((MainActivity)getActivity()).addCommand(SERVO + TURN_RIGHT);
                    mHandler.postDelayed(this, 0);
                }
            };
        });

        Button buttonBack = (Button) view.findViewById(R.id.button4);
        buttonBack.setOnTouchListener(new View.OnTouchListener()
        {

            Context context = getActivity();
            CharSequence text = "GO BACKWARDS!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);

            private Handler mHandler;

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        Log.i(TAG, "Backwards");
                        mHandler.postDelayed(mAction, 50);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable()
            {
                @Override
                public void run()
                {
                    toast.show();
                    ((MainActivity)getActivity()).addCommand(MOTOR + STOP + BRAKE_OFF);
                    mHandler.postDelayed(this, 0);
                }
            };
        });

        return view;
    }
}
