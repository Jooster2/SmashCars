package com.smashcars.core;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import com.smashcars.R;

public class CarListActivity extends AppCompatActivity {
    private Button connectButton;
    private RadioButton car1Button;
    private RadioButton car2Button;

    private String macAddress;

    private static final String CAR_1_MAC_ADDRESS = "00:06:66:7B:AC:2C";
    private static final String CAR_2_MAC_ADDRESS = "00:06:66:7B:AB:CA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);

        connectButton = (Button)findViewById(R.id.connectButton);
        car1Button = (RadioButton)findViewById(R.id.car1Button);
        car2Button = (RadioButton)findViewById(R.id.car2Button);

        car1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                macAddress = CAR_1_MAC_ADDRESS;
            }

        });

        car2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                macAddress = CAR_2_MAC_ADDRESS;
            }
        });

    }

    public void connect(View v) {
        Intent result = new Intent();
        result.putExtra("result", macAddress);
        setResult(RESULT_OK, result);
        this.finish();
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_car_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
