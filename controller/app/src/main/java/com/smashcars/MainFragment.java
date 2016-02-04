package com.smashcars;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        Button button = (Button) view.findViewById(R.id.button);
        button.setOnTouchListener(new View.OnTouchListener()
        {

            Context context = getActivity();
            CharSequence text = "GO FORWARD!";
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
                        mHandler.postDelayed(mAction, 0);
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
                        mHandler.postDelayed(mAction, 0);
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
                        mHandler.postDelayed(mAction, 0);
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
                        mHandler.postDelayed(mAction, 0);
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
                    mHandler.postDelayed(this, 0);
                }
            };
        });
        return view;
    }
}
