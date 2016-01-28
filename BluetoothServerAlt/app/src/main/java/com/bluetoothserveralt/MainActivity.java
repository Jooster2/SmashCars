package com.bluetoothserveralt;



        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.bluetooth.BluetoothManager;
        import android.bluetooth.BluetoothServerSocket;
        import android.bluetooth.BluetoothSocket;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.TextView;

        import java.io.BufferedInputStream;
        import java.io.BufferedReader;
        import java.io.DataInputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.util.UUID;

/**
 * Class for testing the bluetooth code for the Android device. It uses the "BlueBird" class to
 *  receive data and then prints it in a specified textview.
 */
public class MainActivity extends AppCompatActivity
{
    BlueBird bB;
    TextView view;
    BluetoothServerSocket btSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = (TextView) findViewById(R.id.text);

    }

    @Override
    protected void onResume ()
    {
        super.onResume();
        //bB= new BlueBird (this);
        //bB.run();
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

    /**
     * Method called when button "Create socket" is pressed.
     * Creates new BlueBird object and starts the listening thread.
     * @param v
     */
    public void doRun (View v)
{
    bB= new BlueBird (this);
    bB.startThread();

    }

    /**
     * Method called when button "Close socket" is pressed.
     * Closes sockets and input streams.
     * @param v
     */
    public void stopRun (View v)
    {

        bB.stopIt();

    }

    /**
     * Puts data in textview.
     * @param s
     */
    public void putData (String s)
    {
        view.setText(s);
    }
}
