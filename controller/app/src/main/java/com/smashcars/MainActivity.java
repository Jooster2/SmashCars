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
    private static final String TAG = "main";
    private CircleBuffer commandBuffer;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        commandBuffer = new CircleBuffer(10);
        BluetoothHandler.getInstance().setActivity(this);
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
     * @param v
     */
    public void connectNow(View v) {
        if(!BluetoothHandler.getInstance().isEnabled()) {
            Log.i(TAG, "Bluetooth disabled, enabling it now");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            String MAC = null;
            if(v != null)
                MAC = ((TextView)v.findViewById(R.id.textMac)).getText().toString();
            Log.i(TAG, "MAC address is " + MAC);
            BluetoothHandler.getInstance().connect(MAC);
        }
    }

    /**
     * Returns, in the correct order, what is currently in the circular buffer
     * @return char-array with commands
     */
    public char[] getControllerCommands() {
        Log.i(TAG, "Returning controller commands");
        return commandBuffer.getArray();
    }
}
