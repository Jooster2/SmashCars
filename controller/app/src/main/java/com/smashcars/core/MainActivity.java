package com.smashcars.core;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.smashcars.utils.CircularArray;
import com.smashcars.R;
import com.smashcars.joystick.JoystickFragment;

public class MainActivity extends AppCompatActivity
{
    private static final int REQUEST_ENABLE_BT = 1;
    private int hitpoints;
    private boolean immortal = false;
    private static final String TAG = "MainActivity";


    CircularArray<Short> commandBuffer;
    FragmentManager fragmentManager;
    JoystickFragment joystickFragment;
    CarListFragment carListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        commandBuffer = new CircularArray<> (10);
        //Initiate the BluetoothHandler
        BluetoothHandler.getInstance().setActivity(this);
        hitpoints = 3;
        fragmentManager = getFragmentManager();
        carListFragment = new CarListFragment();
        fragmentManager.beginTransaction()
                .add(R.id.activity_main, carListFragment, "carListFragment").commit();

        joystickFragment = new JoystickFragment();



        Log.i(TAG, "onCreate done");
    }

    @Override
    protected void onPause() {
        super.onPause();
        BluetoothHandler.getInstance().disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothHandler.getInstance().disconnect();
    }

    //TODO is this method necessary?
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //TODO is this method necessary?
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

    /**
     * Called by BluetoothHandler when bluetooth gets enabled. Also calls connectnow to continue
     * with the connect attempt
     * @param requestCode request code
     * @param resultCode result code
     * @param data unused data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "Got activity result");
        if(resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT)
            connectNow(null);
    }

    public void resultFromBluetooth(char fromCar) {
        if(fromCar == 'L') {
            changeHitpoints(-1);
            if(hitpoints <= 0);
                //TODO end the game
        }
        else {
            joystickFragment.setPowerup(fromCar);
        }
    }

    public void changeHitpoints(int delta) {
        if(delta < 0 && immortal)
            return;
        hitpoints += delta;
    }

    public void setImmortal(boolean immortal) {
        this.immortal = immortal;
    }

    public void stopPowerup(char powerup) {
        joystickFragment.stopPowerup(powerup);
    }



    /**
     * Called by button press or onActivityResult method
     * Enables bluetooth, and calls the connect method in the BluetoothHandler with the MAC-address
     * specified in textfield as argument (can be null)
     * @param v view that called this
     */
    public void connectNow(View v) {
        Log.i(TAG, "connect-button pressed");
        if(!BluetoothHandler.getInstance().isEnabled()) {
            Log.i(TAG, "Bluetooth disabled, enabling it now");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        } else {
            String macAddress = carListFragment.getMacAddress();
            //fragmentManager.beginTransaction().remove(carListFragment).commit();
            macAddress = "00:06:66:7B:AB:CA";
            if(macAddress != null) {
                BluetoothHandler.getInstance().connect(macAddress);

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                fragmentManager.beginTransaction().add(R.id.activity_main, joystickFragment).commit();


            }
        }
    }

    public void disconnect(View v) {
        Log.i(TAG, "disconnect-button pressed");
        BluetoothHandler.getInstance().disconnect();
    }

    public synchronized void addCommand(int cmd)
    {
        Log.i(TAG, "Adding command to buffer");
        commandBuffer.add((short) cmd);


    }
    /**
     * Returns, in the correct order, what is currently in the circular buffer
     * @return char-array with commands
     */

    public Short getControllerCommand() {
        //Log.i(TAG, "Returning controller command");
        return commandBuffer.getNext();
    }

    /**
     * Stops the servo by clearing the command buffer and then setting the two next values to
     * 90 for servo reset, and last used motor value from parameter
     * @param motorValue the last used value for the motor
     */
    public void stopServo(int motorValue) {
        commandBuffer.clear();
        commandBuffer.add((short) 90);
        commandBuffer.add((short)motorValue);
        Log.i(TAG, "Stopped servo");
    }

    /**
     * Stops the motor by clearing the command buffer and then setting the two next values to
     * 256 for motor reset, and last used servo value from parameter
     * @param servoValue the last used value for the servo
     */
    public void stopMotor(int servoValue) {
        commandBuffer.clear();
        commandBuffer.add((short) 256);
        commandBuffer.add((short)servoValue);
        Log.i(TAG, "Stopped motor");
    }
}
