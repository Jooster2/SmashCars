package com.smashcars.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Joakim Schmidt
 */
public class BluetoothHandler {
    private static final int LATENCY = 50;
    private static final String TAG = "bthandler";

    private MainActivity mainActivity;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice btServer;
    private ActiveThread activeThread;
    private boolean connecting;

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
        connecting = false;
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
    public void connect(String macAddress) {
        connecting = true;
        Log.i(TAG, "Starting connection procedure");
        Log.i(TAG, "MAC-address is " + macAddress);
        btServer = btAdapter.getRemoteDevice(macAddress);
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
        try {
            activeThread.disconnect();
        } catch(NullPointerException e) {
            Log.i(TAG, "Failed disconnecting due to no activeThread object");
        }
    }

    public boolean isConnected() {
        //return true;
        try {
            return activeThread.isConnected();
        } catch(NullPointerException e) {
            return false;
        }
    }

    public boolean isConnecting() {
        return connecting;
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

        private boolean isConnected() {
            return isConnected;
        }

        /**
         * Opens the connection, and an outputstream to the socket.
         * Sends one char over the connection every LATENCY milliseconds
         * Flushes and closes both stream and socket when stopped via disconnect method
         */
        @Override
        @SuppressWarnings("unchecked")
        public void run() {
            Log.i(TAG, "Run starting");
            BluetoothSocket socket = null;

            //Create a socket aimed for the server device
            try {
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                Log.i(TAG, "UUID: " + uuid.toString());
                Log.i(TAG, "Attempting to create socket");
                socket = btServer.createRfcommSocketToServiceRecord(uuid);
            } catch (NullPointerException | IOException e) {
                Log.i(TAG, e.getLocalizedMessage());
            }
            //Attempt to connect to the server device
            try {
                Log.i(TAG, "Created socket");
                btAdapter.cancelDiscovery();
                socket.connect();
                isConnected = true;
                connecting = false;
                Log.i(TAG, "Connected");
            } catch (Exception e) {

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
                Log.i(TAG, "Attempting to open streams");
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                Short toCar;
                char fromCar;
                while(isConnected) {
                    try {
                        if(dis.available() != 0) {
                            fromCar = (char) dis.readByte();
                            Log.i(TAG, "Read from car: " + fromCar);
                            final char fromCar2 = fromCar;
                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mainActivity.resultFromBluetooth(fromCar2);
                                }
                            });

                        }
                    } catch (IOException e) {
                        Log.i(TAG, e.getLocalizedMessage());
                    }
                    try {
                        //Read a char from the commandbuffer
                        if((toCar = mainActivity.getControllerCommand()) == null) {
                            //if command is null, there was no command so we sleep
                            sleep(LATENCY);
                            continue;
                        }
                        else {
                            //If command is valid write it to the stream
                            Log.i(TAG, "Writing to socket stream: " + toCar);
                            dos.writeShort(toCar);
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

                //Send command to close socket on car-side
                Log.i(TAG, "Sending socket-close code");
                dos.writeShort((short)2048);
                try {
                    sleep(50);
                } catch (InterruptedException e) {}
                Log.i(TAG, "Socket-close code sent");

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
