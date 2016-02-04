package com.smashcars;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "mainactivity";
    //private CircleBuffer commandBuffer;
    CircularArray<Integer> commandBuffer;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        commandBuffer = new CircularArray<> (10);
        //Initiate the BluetoothHandler
        BluetoothHandler.getInstance().setActivity(this);
        Log.i(TAG, "oncreate done");
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    /**
     * Called by button press or onActivityResult method
     * Enables bluetooth, and calls the connect method in the BluetoothHandler with the MAC-address
     * specified in textfield as argument (can be null)
     * @param v view that called this
     */
    public void connectNow(View v) {
        Log.i(TAG, "connectbutton pressed");
        if(!BluetoothHandler.getInstance().isEnabled()) {
            Log.i(TAG, "Bluetooth disabled, enabling it now");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            BluetoothHandler.getInstance().connect();
        }
    }

    public void addCommand(int cmd)
    {
        Log.i(TAG, "Adding command to buffer");
        commandBuffer.add(cmd);


    }
    /**
     * Returns, in the correct order, what is currently in the circular buffer
     * @return char-array with commands
     */

    public int getControllerCommand() {
        Log.i(TAG, "Returning controller command");
        return commandBuffer.getNext();
    }
}
