package com.smashcars;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MyActivity";

    private BluetoothAdapter bluetoothAdapter;
    public static ProgressDialog progressDialog;
    private Set<BluetoothDevice> bondedDevices; // Lists all bonded bluetooth devices
    private ArrayList<BluetoothDevice> foundDevices;     // Lists all found bluetooth devices

    Button on,off,visible,list,find;
    ListView bondedListView, foundListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        on = (Button)findViewById(R.id.onButton);
        off = (Button)findViewById(R.id.offButton);
        visible = (Button)findViewById(R.id.visibleButton);
        list = (Button)findViewById(R.id.listButton);
        find = (Button)findViewById(R.id.findButton);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),
                    "Bluetooth not available on this device", Toast.LENGTH_LONG).show();
            finish();
        }

        bondedListView = (ListView)findViewById(R.id.listView);
        foundListView = (ListView)findViewById(R.id.foundListView);

        bondedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.v(TAG, "Bluetooth device selected");
                BluetoothDevice selectedDevice = (BluetoothDevice)bondedListView
                        .getItemAtPosition(position);
                connectToDevice(selectedDevice);
            }
        });

        foundListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.v(TAG, "Bluetooth device selected");
                BluetoothDevice selectedDevice = (BluetoothDevice)foundListView
                        .getItemAtPosition(position);
                connectToDevice(selectedDevice);
            }
        });

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Find");
                bluetoothAdapter.startDiscovery();
            }
        });

        // Filter for the broadcast service
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction("android.bleutooth.device.action.UUID");
        // Register the broadcast receiver
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);

    }

    public void turnOn (View v) {
        Log.v(TAG, "Inside method turnOn");

        if (!bluetoothAdapter.isEnabled()) {
            Intent turnOnBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnBluetooth, 0);
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already enabled",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void turnOff (View v) {
        Log.v(TAG, "Inside method turnOff");

        bluetoothAdapter.disable();
    }

    public void makeVisible(View v) {
        Log.v(TAG, "Inside method makeVisible");

        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
    }

    public void listDevices (View v) {
        Log.v(TAG, "Inside method listDevices");

        bondedDevices = bluetoothAdapter.getBondedDevices();
        ArrayList<BluetoothDevice> list = new ArrayList<BluetoothDevice>();

        if(bondedDevices.size() > 0) {
            for (BluetoothDevice device : bondedDevices)
                list.add(device);

            Toast.makeText(getApplicationContext(), "Showing paired devices",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "No paired bluetooth devices found",
                    Toast.LENGTH_LONG).show();
        }
        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        bondedListView.setAdapter(adapter);
    }

    public void showFoundDevices () {
        Log.v(TAG, "Inside method showFoundDevices");

        System.out.println(foundDevices.size());

        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, foundDevices);
        foundListView.setAdapter(adapter);
    }

    public void connectToDevice (BluetoothDevice device) {
        Log.v(TAG, "Inside method connectToDevice");

        ParcelUuid[] uuids = device.getUuids();
        for(ParcelUuid x : uuids) {
            Log.i("UUID", x.toString());
        }

        ConnectionActivity connectionActivity = new ConnectionActivity(this, device);
        connectionActivity.execute();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver () {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_ON) {
                    Toast.makeText(getApplicationContext(),"Bluetooth enabled",
                            Toast.LENGTH_LONG).show();
                }
                else if(state == BluetoothAdapter.STATE_OFF) {
                    Toast.makeText(getApplicationContext(), "Bluetooth disabled",
                            Toast.LENGTH_LONG).show();
                }
            } else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                progressDialog = ProgressDialog.show(context, "Please wait...",
                        "Searching for bluetooth devices...");
                foundDevices = new ArrayList<BluetoothDevice>();
            } else if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                System.out.println("FOUND DEVICE");
                BluetoothDevice foundDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                foundDevices.add(foundDevice);
            } else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                progressDialog.dismiss();
                showFoundDevices();
            } else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                Toast.makeText(getApplicationContext(), "paired", Toast.LENGTH_LONG).show();
            } else if("android.bleutooth.device.action.UUID".equals(action)) {
                for(Parcelable x : intent.getParcelableArrayExtra("android.bluetooth.device.extra.UUID")) {
                    ParcelUuid uuid = (ParcelUuid)x;
                    Log.i("UUID", uuid.toString());
                }
            }
        }
    };
}
