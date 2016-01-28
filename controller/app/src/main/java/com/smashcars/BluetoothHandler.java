package com.smashcars;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author Joakim Schmidt
 */
public class BluetoothHandler {
    //Carls MAC-address
    private static final String SERVER_MAC = "44:D4:E0:27:8F:AC";
    private static final UUID _UUID = java.util.UUID.fromString("6d357677-c1b7-4789-ac78-8f9f73486c38");
    private static final String TAG = "bthandler";

    private MainActivity mainActivity;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice btServer;
    private ActiveThread activeThread;


    private static BluetoothHandler instance = null;

    /**
     * Returns the instance of this class
     * @return a new instance if none existed, otherwise the existing one
     */
    public static synchronized BluetoothHandler getInstance() {
        if(instance == null)
            return instance = new BluetoothHandler();
        else
            return instance;
    }

    /**
     * Constructor
     */
    private BluetoothHandler() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null)
            Log.i(TAG, "Bluetooth hardware not found");
    }

    /**
     * Sets the owning MainActivity to parameter
     * @param activity owning MainActivity object
     */
    public void setActivity(MainActivity activity) {
        mainActivity = activity;
    }

    /**
     * Returns true if Bluetooth is enabled
     * @return true if Bluetooth is enabled
     */
    public boolean isEnabled() {
        return btAdapter.isEnabled();
    }

    /**
     * Attempts to connect to the MAC-address specified in parameter
     * If parameter is null, connects to SERVER_MAC
     * @param MAC address to connect to
     */
    public void connect(String MAC) {
        //if(MAC == null)
            MAC = SERVER_MAC;
        Log.i(TAG, "Starting connection procedure");
        btServer = btAdapter.getRemoteDevice(MAC);
        if(btServer != null) {
            Log.i(TAG, "Found server");
            activeThread = new ActiveThread(btServer);
            activeThread.start();
        } else {
            Log.i(TAG, "Did not find server");
        }

    }

    /**
     * Closes the current connection
     */
    public void disconnect() {
        activeThread.disconnect();
    }

    /**
     * Opens and manages the connection to another Bluetooth device
     * @author Joakim Schmidt
     */
    private class ActiveThread extends Thread {
        private BluetoothDevice btServer;
        private boolean isConnected = true;

        /**
         * Constructor
         * @param btServer the Bluetooth device which ActiveThread should connect to
         */
        public ActiveThread(BluetoothDevice btServer) {
            this.btServer = btServer;
        }

        /**
         * Closes the current connection
         */
        private void disconnect() {
            isConnected = false;
            Log.i(TAG, "Disconnected");
        }

        /**
         * Opens the connection, and an outputstream to the socket.
         * Every .5 seconds, writes MainActivitys circular buffer as a sequence of chars
         * Flushes and closes both stream and socket when stopped via disconnect method
         */
        @Override
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                BluetoothSocket socket = btServer.createInsecureRfcommSocketToServiceRecord(_UUID);
                Log.i(TAG, "Created socket");
                btAdapter.cancelDiscovery();
                socket.connect();
                Log.i(TAG, "Connected");
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                while(isConnected) {
                    try {
                        Log.i(TAG, "Writing to socket stream");
                        dos.writeChars(Arrays.toString(mainActivity.getControllerCommands()));
                        Log.i(TAG, "Going to sleep");
                        sleep(500);
                    } catch(IOException | InterruptedException e) {
                        Log.i(TAG, e.getLocalizedMessage());
                    }
                }

                Log.i(TAG, "Closing stream and socket");
                dos.flush();
                dos.close();
                socket.close();

            } catch(IOException e) {
                Log.i(TAG, e.getLocalizedMessage());
            }
        }
    }

}
