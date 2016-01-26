package com.smashcars;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.Math;

public class MainActivity extends AppCompatActivity
{
    public boolean forwardTrue= false, backWardTrue = false;

    public SensorManager mSensorManager;
    public Sensor mSensor;
    TextView text, text2,text3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.test) ;
        text2 = (TextView) findViewById(R.id.test2) ;
        text3 = (TextView) findViewById(R.id.test3) ;
        GyroFun();
        RotationFun();
        gameRotationFun();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void gameRotationFun (){
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        mSensorManager.registerListener(sel,
                                        mSensor,
                                        SensorManager.SENSOR_DELAY_UI);

    }
    public void RotationFun (){
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorManager.registerListener(sel,
                                        mSensor,
                                        SensorManager.SENSOR_DELAY_UI);

    }
    public void GyroFun (){
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(sel,
                            mSensor,
                            SensorManager.SENSOR_DELAY_UI);

    }
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;


    private final SensorEventListener sel = new SensorEventListener()
    {
        public void onSensorChanged(SensorEvent event)
        {
            // This timestep's delta rotation to be multiplied by the current rotation
            // after computing it from the gyro sample data.
            if(false)//event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
            {
                if(timestamp != 0)
                {
                    final float dT = (event.timestamp - timestamp) * NS2S;
                    // Axis of the rotation sample, not normalized yet.
                    float axisX = event.values[0];
                    float axisY = event.values[1];
                    float axisZ = event.values[2];

                    // Calculate the angular speed of the sample
                    float omegaMagnitude = (float) Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

                    // Normalize the rotation vector if it's big enough to get the axis
                    // (that is, EPSILON should represent your maximum allowable margin of error)
                    if(omegaMagnitude > 1)
                    {
                        axisX /= omegaMagnitude;
                        axisY /= omegaMagnitude;
                        axisZ /= omegaMagnitude;
                    }

                    // Integrate around this axis with the angular speed by the timestep
                    // in order to get a delta rotation from this sample over the timestep
                    // We will convert this axis-angle representation of the delta rotation
                    // into a quaternion before turning it into the rotation matrix.
                    float thetaOverTwo = omegaMagnitude * dT / 2.0f;
                    float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
                    float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
                    deltaRotationVector[0] = sinThetaOverTwo * axisX;
                    deltaRotationVector[1] = sinThetaOverTwo * axisY;
                    deltaRotationVector[2] = sinThetaOverTwo * axisZ;
                    deltaRotationVector[3] = cosThetaOverTwo;
                }
                timestamp = event.timestamp;
                float[] deltaRotationMatrix = new float[9];
                SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
                // User code should concatenate the delta rotation we computed with the current rotation
                // in order to get the updated rotation.
                //rotationCurrent = rotationCurrent * deltaRotationMatrix;
                text.setText("X-axel:   " + deltaRotationMatrix[0]);
                text2.setText("Y-axel:  " + deltaRotationMatrix[1]);
                text3.setText("Z-axel:  " + deltaRotationMatrix[2]);
                //Toast.makeText(getApplicationContext(),  " : X-axel" +deltaRotationMatrix[0], Toast.LENGTH_SHORT).show();
            }
            else if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
            {
                /*
                This simple code will give Right and left, but has some problems:
                If you tilt screen forward, it will also show left, and when you tilt it towards you, it shows right.
                The check : !forwardTrue makes it impossible to turn left whilst driving forward. This creates
                new problems however, when youre holding the device. Its pretty hard to hold it precisely flat.
                So Ill look at other solutions.
                 */
                //System.out.println(event.values[1]);
                text.setText("Heading:   ");
                if (event.values [1]> 0.05 &&!forwardTrue)
                    text2.setText("Left");
                else if (event.values [1]< -0.05 && !backWardTrue)
                    text2.setText("Right");
                else
                    text2.setText("Straight");

            }
            else if(event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR)
            {
                /*
                This simple code will give Forward and backwards, and it works.
                It works because the values received is both negative on one side(right,left),
                and changing on the other(forward,backward.
                 */
               // System.out.println(event.values[1]);


                text.setText("Heading:   ");

                if (event.values [1]> 0.01)
                {
                    text3.setText("Forward");
                    forwardTrue = true;
                    backWardTrue = false;
                }
                else if (event.values [1]< -0.04)
                {
                    text3.setText("Backwards");
                    backWardTrue = true;
                    forwardTrue = false;
                }
                else
                {
                    text3.setText("Stopped");
                    forwardTrue = false;
                    backWardTrue = false;
                }

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {

        }
    };
}


