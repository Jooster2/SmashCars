package com.bluetoothserver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.UUID;
/**
 * Created by Carl-Henrik Hult on 2016-01-26.
 */
public class BlueBird
{
    MainActivity mA;
    BluetoothServerSocket btSocket;
    private PassiveThread listenerThread;
    public BlueBird (MainActivity mA){
        this.mA = mA;

    BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
    try
    {
        btSocket = bta.listenUsingInsecureRfcommWithServiceRecord("hej", UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));


    } catch(IOException e)
    {
        e.printStackTrace();
    }

}


    public void startThread()
    {
        if(listenerThread == null || !listenerThread.isAlive())
        {
            listenerThread = new PassiveThread();
            listenerThread.start();
        }
    }

   private class PassiveThread extends Thread
    {
        BluetoothServerSocket serverSocket = null;

        /**
         * This runnable is the core of the thread. Preforms the actual network operations.
         */
        @Override
        @SuppressWarnings("unchecked")
        public void run()
        {
            while(true)
            {
                try
                {
                    //Set up socket



                    //Accept connection and set up streams
                    BluetoothSocket client = btSocket.accept();
                    ObjectInputStream ois = new ObjectInputStream(client.getInputStream());


                    //Receive, and then send data
                    final String receivedData = (String)ois.readObject();
                    System.out.println (receivedData);

                    //Clean up
                    ois.close();

                    client.close();
                    btSocket.close();



                    mA.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mA.putData(receivedData);
                        }
                    });
                }
                catch(IOException | ClassNotFoundException e)
                {
                    Log.i("myTag", e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        /**
         * Interrupts the thread and stops it from listening for connections
         */
        public void stopThread()
        {
            try
            {
                if (serverSocket != null)
                {
                    serverSocket.close();
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                interrupt();
            }
        }
    }

}


