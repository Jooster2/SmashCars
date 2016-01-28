package com.bluetoothserveralt;

/**
 * Created by Carl-Henrik Hult on 2016-01-28.
 */


        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothServerSocket;
        import android.bluetooth.BluetoothSocket;
        import android.util.Log;
        import android.widget.TextView;

        import java.io.BufferedReader;
        import java.io.DataInputStream;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.io.ObjectInputStream;
        import java.util.UUID;
/**
 * Created by Carl-Henrik Hult on 2016-01-26.
 * Creates the bluetooth server socket.
 */
public class BlueBird
{
    boolean isConnected = false;
    MainActivity mA;
    BluetoothServerSocket btSocket;
    private PassiveThread listenerThread;
    public BlueBird (MainActivity mA){
        this.mA = mA;

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
        try
        {
            btSocket = bta.listenUsingInsecureRfcommWithServiceRecord("hej", UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            //00001101-0000-1000-8000-00805F9B34FB  8ce225c0-200a-11e0-ac64-0800200c9a66
            Log.i("BT-SERVER", "socket created");


        } catch(IOException e)
        {
            e.printStackTrace();
        }

    }

    public void stopIt ()
    {
        isConnected = false;
    }


    public void startThread()
    {
        if(listenerThread == null || !listenerThread.isAlive())
        {

            listenerThread = new PassiveThread();
            listenerThread.start();

        }
    }

    /**
     * The thread that listenes for connections and takes care of received data.
     */
    private class PassiveThread extends Thread
    {
        BluetoothServerSocket serverSocket = null;

        /**
         * This runnable is the core of the thread. It receives data and sends it to the main activity.
         */
        @Override
        @SuppressWarnings("unchecked")
        public void run()
        {
            //Accept connection and set up streams
            BluetoothSocket client = null;
            DataInputStream ois = null;
            try
            {
                client = btSocket.accept();
                Log.i("BT-SERVER", "socket accept");
                ois = new DataInputStream(client.getInputStream());
                isConnected = true;
            }
            catch (IOException e){e.printStackTrace();}
            while(isConnected)
            {
                //Log.i("BT-SERVER", "in run");
                try
                {
                    //Receive, and then send data
                    final char receivedData = ois.readChar();
                    Log.i("BT-SERVER", "DATA :" + receivedData);

                    mA.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mA.putData(""+ receivedData);
                        }
                    });
                }
                catch(IOException e)
                {
                    Log.i("myTag", e.getMessage());
                    e.printStackTrace();
                }
            }
            Log.i("BT-SERVER", "CLOSED THREAD;SOCKETS AND STREAM");
            try
            {

                if (btSocket!= null)
                {
                    ois.close();
                    btSocket.close();
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }

        }

    }

}


