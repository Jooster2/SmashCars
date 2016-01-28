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
    private static final int LATENCY = 50;
    //private static final UUID _UUID = java.util.UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
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
     * Attempts to connect to the SERVER_MAC address
     */
    public void connect() {
        Log.i(TAG, "Starting connection procedure");
        btServer = btAdapter.getRemoteDevice(SERVER_MAC);
        if(btServer != null) {
            Log.i(TAG, "Found server");
            //Success, device found. Now send the device to a thread and start it
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
        private boolean isConnected = false;

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
         * Sends one char over the connection every LATENCY milliseconds
         * Flushes and closes both stream and socket when stopped via disconnect method
         */
        @Override
        @SuppressWarnings("unchecked")
        public void run() {
            BluetoothSocket socket = null;

            //Create a socket aimed for the server device
            try {
                socket = btServer.createInsecureRfcommSocketToServiceRecord(btServer.getUuids()[0].getUuid());
                Log.i(TAG, "UUID: " + btServer.getUuids()[0].getUuid().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Attempt to connect to the server device
            try {
                Log.i(TAG, "Created socket");
                btAdapter.cancelDiscovery();
                socket.connect();
                isConnected = true;
                Log.i(TAG, "Connected");
            } catch (IOException e) {

                //If the connection failed, attempt fallback method instead
                try {
                    Log.i(TAG, "Attempting fallback");
                    socket = (BluetoothSocket) btServer.getClass().getMethod("createInsecureRfcommSocket", new Class[]{int.class}).invoke(btServer, 1);
                    socket.connect();
                    isConnected = true;
                    Log.i(TAG, "Fallback connected");
                } catch (Exception ex) {
                    Log.i(TAG, ex.getLocalizedMessage());
                }
            }
            //Create the datastream, and start sending commands (from mainactivitys circularbuffer)
            //Send one char at a time, with LATENCY milliseconds between
            try {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                char cmd;
                while(isConnected) {
                    try {
                        //Read a char from the commandbuffer
                        if((cmd = mainActivity.getControllerCommand()) == '-') {
                            //If the command is '-' it is no command, so we sleep and then do it all
                            //over again
                            sleep(LATENCY);
                            continue;
                        }
                        else {
                            //If command is valid write it to the stream
                            Log.i(TAG, "Writing to socket stream");
                            dos.writeChar(cmd);
                        }
                    } catch(IOException | InterruptedException e) {
                        Log.i(TAG, e.getLocalizedMessage());
                        Log.i(TAG, "Error writing to stream, disconnecting");
                        isConnected = false;
                    }
                    //TODO not sure if this is necessary anymore
                    try {
                        Log.i(TAG, "Going to sleep");
                        sleep(LATENCY);
                    } catch (InterruptedException e) {
                        Log.i(TAG, "Sleep interrupted");
                    }
                }

                //Clean up
                Log.i(TAG, "Closing stream and socket");
                dos.flush();
                dos.close();
                socket.close();
                Log.i(TAG, "Stream and socket closed");

            } catch(IOException e) {
                Log.i(TAG, e.getLocalizedMessage());
            }
        }
    }
}
