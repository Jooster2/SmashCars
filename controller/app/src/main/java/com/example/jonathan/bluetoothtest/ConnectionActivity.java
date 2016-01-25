package com.example.jonathan.bluetoothtest;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Jonathan on 2016-01-22.
 */
public class ConnectionActivity extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "ConnectionActivity";

    private boolean isBluetoothConnected = false;
    private boolean connectionSuccess = true;
    private BluetoothSocket bluetoothSocket = null;
    private BluetoothAdapter bluetoothAdapter;
    private UUID deviceUUID = UUID.randomUUID();

    Context context;
    BluetoothDevice chosenDevice;

    public ConnectionActivity (Context context, BluetoothDevice chosenDevice) {
        this.context = context;
        this.chosenDevice = chosenDevice;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    @Override
    protected void onPreExecute()
    {
        MainActivity.progressDialog = ProgressDialog.show(context, "Please wait...", "Connecting to bluetooth device...");
    }

    @Override
    protected Void doInBackground(Void... devices) {
        Log.v(TAG, "Inside Connection");

        try {
            if (!isBluetoothConnected || bluetoothSocket == null) {
                Log.v(TAG, "Inside try");
                System.out.println(deviceUUID.toString());
                bluetoothSocket = chosenDevice.createInsecureRfcommSocketToServiceRecord(deviceUUID);
                if (bluetoothSocket != null)
                    System.out.println("Socket created");
                bluetoothAdapter.cancelDiscovery();
                if
                (!bluetoothAdapter.isDiscovering()) {
                    System.out.println("Connecting");
                    bluetoothSocket.connect();
                }
            }
        } catch (IOException e) {
            connectionSuccess = false;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        if(!connectionSuccess) {
            Toast.makeText(context,"Connection failed!", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(context,"Connection succeded!", Toast.LENGTH_LONG).show();
        }
        MainActivity.progressDialog.dismiss();

    }
}
