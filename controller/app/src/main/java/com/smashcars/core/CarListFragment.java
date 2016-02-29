package com.smashcars.core;

import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.smashcars.R;

public class CarListFragment extends Fragment {
    private Button connectButton;
    private RadioButton car1Button;
    private RadioButton car2Button;

    private String macAddress;

    private static final String CAR_1_MAC_ADDRESS = "00:06:66:7B:AC:2C";
    private static final String CAR_2_MAC_ADDRESS = "00:06:66:7B:AB:CA";

    public CarListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        macAddress = new String();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_car_list, container, false);
        connectButton = (Button)view.findViewById(R.id.connectButton);
        car1Button = (RadioButton)view.findViewById(R.id.car1Button);
        car2Button = (RadioButton)view.findViewById(R.id.car2Button);

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

        return view;
    }

    public String getMacAddress() {
        return macAddress;
    }




}
