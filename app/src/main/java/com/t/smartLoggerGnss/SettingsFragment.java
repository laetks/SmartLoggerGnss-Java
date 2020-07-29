package com.t.smartLoggerGnss;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;



public class SettingsFragment extends Fragment {

    private RawMeasurements rawMeasurements;

    Dialog helpDialog;
    Button help;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag1_layout, container, false /* attachToRoot */);

        final Switch registerMeasurement = (Switch) view.findViewById(R.id.measurements);
        registerMeasurement.setChecked(false);

        helpDialog = new Dialog(getContext());

        help = view.findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowHelpPopup();
            }
        });

        registerMeasurement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            // @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    getRawMeasurements().startGnss();
                } else {
                    getRawMeasurements().stopGnss();
                }
            }
        });

        Switch registerLocation = (Switch) view.findViewById(R.id.location);
        registerLocation.setChecked(false);
        registerLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            // @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    getRawMeasurements().starLocation();
                } else {
                    getRawMeasurements().stopLocation();
                }
            }
        });

        Switch registerMessage = (Switch) view.findViewById(R.id.navigationMessage);
        registerMessage.setChecked(false);
        registerMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            // @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    getRawMeasurements().startNavigationMessage();
                } else {
                    getRawMeasurements().stopNavigationMessage();
                }
            }
        });


        Switch registerSensor = (Switch) view.findViewById(R.id.sensors);
        registerSensor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getRawMeasurements().startSensor();
                } else {
                    getRawMeasurements().stopSensor();
                }
            }
        });

        return view;
    }

    /**
     * Generate a help window on the operation of the app.
     */
    private void ShowHelpPopup() {
        helpDialog.setContentView(R.layout.help_popup);
        ImageView close = (ImageView) helpDialog.findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpDialog.dismiss();
            }
        });
        helpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        helpDialog.show();
    }


    public RawMeasurements getRawMeasurements() {
        return rawMeasurements;
    }

 
    public void setRawMeasurements(RawMeasurements rawMeasurements) {
        this.rawMeasurements = rawMeasurements;
    }

}
