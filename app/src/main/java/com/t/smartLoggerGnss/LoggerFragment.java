package com.t.smartLoggerGnss;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;



public class LoggerFragment extends Fragment implements IObserver {

    //variables
    Button start, stop;
    TextView time;
    LinearLayout linearLayout;
    TextView sensorView, locationView, NMView, GNSSView;

    public RawMeasurements rawMeasurements;
    private Chronometer stopwatch;
    private boolean running;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag2_layout, container, false);

        //add observer to the Observer class
        getRawMeasurements().addObserver(this);

        //associate variables with elements of the view
        start = view.findViewById(R.id.start);
        start.setOnClickListener(starLogListener);

        stop = view.findViewById(R.id.stop);
        stop.setOnClickListener(stopLogListener);

        linearLayout = (LinearLayout) view.findViewById(R.id.dataView);
        time = view.findViewById(R.id.time);

        stopwatch = view.findViewById(R.id.chronometer);
        stopwatch.setFormat("%s");
        stopwatch.setBase(SystemClock.elapsedRealtime());

        return view;
    }

    /**
     * Start the timing and recording of the available data
     */
    private View.OnClickListener starLogListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!running) {
                stopwatch.setBase(SystemClock.elapsedRealtime());
                stopwatch.start();
                running = true;
                stop.setEnabled(true);
                start.setEnabled(false);

            }
            getRawMeasurements().getCsvMeasurement().clear();

        }
    };

    /**
     * Stop the timing, the recording and call save() method
     */
    private final View.OnClickListener stopLogListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            running = false;
            stopwatch.stop();
            save();
            stop.setEnabled(false);
            start.setEnabled(true);
        }
    };

    /**
     * Check which data is enable, and store it in internal storage
     */
    public void save() {
        String state;
        state = Environment.getExternalStorageState();

        boolean stateSave = false ;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());

        if (Environment.MEDIA_MOUNTED.equals(state)) {

            //create folder SmartLoggerGnss
            File folder = new File(Environment.getExternalStorageDirectory() + "/SmartLoggerGnss");
            if (!folder.exists()) {
                folder.mkdir();
            }

            //create folder with date + time
            File subFolder = new File(Environment.getExternalStorageDirectory() + "/SmartLoggerGnss/RawMeasurement" + currentDateAndTime);
            if (!subFolder.exists()) {
                subFolder.mkdir();
            }

            //create gnss file
            if (getRawMeasurements().stateGnss == true) {

                stateSave = true ;

                File file = new File(subFolder, "GnssLog" + currentDateAndTime + ".txt");
                BufferedWriter currentFileWriter;
                try {
                    currentFileWriter = new BufferedWriter(new FileWriter(file));
                } catch (IOException e) {
                    logException("Could not open file: ", e);
                    return;
                }

                try {
                    ArrayList raw = getRawMeasurements().getCsvMeasurement();

                    ArrayList header = new ArrayList();
                    header.add("#");
                    header.add("# Header Description:");
                    header.add("#");
                    header.add("# Version: v2.0.0.1" + " Platform: " + Build.VERSION.RELEASE + " Manufacturer: " + Build.MANUFACTURER + " Model: " + Build.MODEL );
                    header.add("#");
                    header.add("# Raw,ElapsedRealtimeMillis,TimeNanos,LeapSecond,TimeUncertaintyNanos,FullBiasNanos,BiasNanos," +
                            "BiasUncertaintyNanos,DriftNanosPerSecond,DriftUncertaintyNanosPerSecond,HardwareClockDiscontinuityCount,Svid," +
                            "TimeOffsetNanos,State,ReceivedSvTimeNanos,ReceivedSvTimeUncertaintyNanos,Cn0DbHz,PseudorangeRateMetersPerSecond," +
                            "PseudorangeRateUncertaintyMetersPerSecond,AccumulatedDeltaRangeState,AccumulatedDeltaRangeMeters,AccumulatedDeltaRangeUncertaintyMeters," +
                            "CarrierFrequencyHz,CarrierCycles,CarrierPhase,CarrierPhaseUncertainty,MultipathIndicator,SnrInDb,ConstellationType,AgcDb,CarrierFrequencyHz");
                    header.add("#");
                    header.add("# ,[ms],[ms],[ns],[s],[ns],[ns],[ns],[ns],[ns/s],[ns/s],,,[ns],,[ns],[ns],[dBHz],[m/s],[m/s],,[m],[m],[Hz],,,,[Db],,[Db],[Hz]");
                    header.add("#");
                    header.add("#");

                    for (int i = 0; i < header.size(); i++) {
                        currentFileWriter.write(header.get(i).toString());
                        currentFileWriter.newLine();
                    }

                    for (int i = 0; i < raw.size(); i++) {
                        if (raw.get(i).toString() == "Raw") {
                            currentFileWriter.write( raw.get(i).toString());

                        }
                        else if (raw.get(i).toString() != "end") {
                            currentFileWriter.write("," + raw.get(i).toString());

                        } else {
                            currentFileWriter.newLine();

                        }
                    }

                    currentFileWriter.newLine();
                    currentFileWriter.close();
                    Toast.makeText(getContext(), "file saved at " + subFolder.getAbsolutePath(), Toast.LENGTH_LONG).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //create navigation message file
            if (getRawMeasurements().stateNavigationMessage == true) {
                stateSave = true ;

                File file = new File(subFolder, "NavigationMessage" + currentDateAndTime + ".csv");
                BufferedWriter currentFileWriter;
                try {
                    currentFileWriter = new BufferedWriter(new FileWriter(file));
                } catch (IOException e) {
                    logException("Could not open file: ", e);
                    return;
                }

                try {
                    ArrayList raw = getRawMeasurements().getCsvNavigationMessage();

                    ArrayList arrayList = new ArrayList();
                    arrayList.add("TimeSince01/01/1970, Type, Svid, Status ,"
                            + "MessageId, SubmessageId ,Data, DescribeContents");
                    arrayList.add("[ms]");

                    currentFileWriter.write(arrayList.get(0).toString());
                    currentFileWriter.newLine();
                    currentFileWriter.write(arrayList.get(1).toString());
                    currentFileWriter.newLine();

                    for (int i = 0; i < raw.size(); i++) {

                        if (raw.get(i).toString() != "end") {
                            currentFileWriter.write(raw.get(i).toString() + ",");

                        } else {
                            currentFileWriter.newLine();
                        }
                    }
                    currentFileWriter.newLine();
                    currentFileWriter.close();
                    Toast.makeText(getContext(), "file saved at " + subFolder.getAbsolutePath(), Toast.LENGTH_LONG).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //create location file
            if (getRawMeasurements().stateLocation == true) {
                stateSave = true ;

                File file = new File(subFolder, "Location" + currentDateAndTime + ".csv");
                BufferedWriter currentFileWriter;
                try {
                    currentFileWriter = new BufferedWriter(new FileWriter(file));
                } catch (IOException e) {
                    logException("Could not open file: ", e);
                    return;
                }
                try {
                    ArrayList raw = getRawMeasurements().getCsvLocation();

                    ArrayList arrayList = new ArrayList();
                    arrayList.add("TimeSince01/01/1970, GNSSHardwareModelName, GNSSYearOhHardware, ElapsedRealtimeMillis, "
                            + "ElapsedRealtimeUncertaintyNanos, Time, Latitude, Longitude, Altitude,"
                            + " Bearing, Speed, Accuracy, VerticalAccuracy, BearingAccuracy, SpeedAccuracy, Extra ");
                    arrayList.add("[ms],,[ns],[ns],[ms],[°],[°],[m],[°],[m/s],[m],[m],[°],[m/s]");

                    currentFileWriter.write(arrayList.get(0).toString());
                    currentFileWriter.newLine();
                    currentFileWriter.write(arrayList.get(1).toString());
                    currentFileWriter.newLine();

                    for (int i = 0; i < raw.size(); i++) {

                        if (raw.get(i).toString() != "end") {
                            currentFileWriter.write(raw.get(i).toString() + ",");
                        } else {
                            currentFileWriter.newLine();
                        }
                    }
                    currentFileWriter.newLine();
                    currentFileWriter.close();
                    Toast.makeText(getContext(), "file saved at " + subFolder.getAbsolutePath(), Toast.LENGTH_LONG).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //create sensor file
            if (getRawMeasurements().stateSensor == true) {
                stateSave = true ;

                File file = new File(subFolder, "Sensor" + currentDateAndTime + ".csv");
                BufferedWriter currentFileWriter;
                try {
                    currentFileWriter = new BufferedWriter(new FileWriter(file));
                } catch (IOException e) {
                    logException("Could not open file: ", e);
                    return;
                }
                try {
                    List raw = getRawMeasurements().getCsvSensor();

                    ArrayList arrayList = new ArrayList();
                    arrayList.add("TimeSince01/01/1970, AccelerometerX, AccelerometerY, AccelerometerZ, GravityX, GravityY, GravityZ,"
                            + " GyroscopeX, GyroscopeY, GyroscopeZ, LinearAccelerationX, LinearAccelerationY, LinearAccelerationZ,"
                            + "Azimuth, Pitch, Roll, AmbientTemperature, LightLevel, Pressure, RelativeHumidity");
                    arrayList.add("[ms],[m/s2],[m/s2],[m/s2],[m/s2],[m/s2],[m/s2],[rad/s],[rad/s],[rad/s],[m/s2],[m/s2],[m/s2],[°],[°],[°],[°C],[lx],[hPa],[%]");

                    currentFileWriter.write(arrayList.get(0).toString());
                    currentFileWriter.newLine();
                    currentFileWriter.write(arrayList.get(1).toString());
                    currentFileWriter.newLine();

                    for (int i = 0; i < raw.size(); i++) {

                        if (raw.get(i).toString() != "end") {
                            currentFileWriter.write(raw.get(i).toString() + ",");
                        } else {
                            currentFileWriter.newLine();
                        }
                    }
                    currentFileWriter.newLine();
                    currentFileWriter.close();
                    Toast.makeText(getContext(), "file saved at " + subFolder.getAbsolutePath(), Toast.LENGTH_LONG).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //delete the date + time folder if no category is activated
            if(stateSave == false ){
                subFolder.delete();
                Toast.makeText(getContext(), "Nothing to save !", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(getContext(), "SD card not found", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Show error message
     * @param errorMessage
     * @param exception
     */
    private void logException(String errorMessage, Exception exception) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    /**
     * Show gnss data
     */
    @Override
    public void notifyGnss() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (GNSSView == null) {
                    GNSSView = new TextView(getActivity());
                    LinearLayout.LayoutParams params =  new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT );
                    params.setMargins(0,0,0,20);
                    GNSSView.setLayoutParams(params);
                    GNSSView.setBackgroundResource(R.drawable.my_border);
                    GNSSView.setMaxLines(30);
                    GNSSView.setElevation(2);
                    linearLayout.addView(GNSSView);

                }
                GNSSView.setEnabled(true);

                SpannableString ss = new SpannableString("GnssMeasurements:\n");
                StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                ss.setSpan(boldSpan, 0, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new RelativeSizeSpan(1.1f), 0, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                GNSSView.setText(ss);
                GNSSView.append(getRawMeasurements().getGnssBuilder());
            }
        });
    }

    /**
     * Show navigation message data
     */
    @Override
    public void notifyNavigationMessage() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (NMView == null) {
                NMView = new TextView(getActivity());
                LinearLayout.LayoutParams params =  new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT );
                params.setMargins(0,0,0,20);
                NMView.setLayoutParams(params);
                NMView.setElevation(2);
                NMView.setBackgroundResource(R.drawable.my_border);
                linearLayout.addView(NMView);

            }
                NMView.setEnabled(true);

                SpannableString ss = new SpannableString("GnssNavigationMessage:\n");
                StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                ss.setSpan(boldSpan, 0, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new RelativeSizeSpan(1.1f), 0, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                NMView.setText(ss);
                NMView.append(getRawMeasurements().getNavigationMessageBuilder());
            }
        });
    }

    /**
     * Show location data
     */
    @Override
    public void notifyLocation() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (locationView == null) {
                    locationView = new TextView(getActivity());
                    LinearLayout.LayoutParams params =  new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT );
                    params.setMargins(0,0,0,20);
                    locationView.setLayoutParams(params);
                    locationView.setBackgroundResource(R.drawable.my_border);
                    locationView.setElevation(2);
                    linearLayout.addView(locationView);
                }
                locationView.setEnabled(true);

                SpannableString ss = new SpannableString("Location:\n");
                StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                ss.setSpan(boldSpan, 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new RelativeSizeSpan(1.1f), 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                locationView.setText(ss);
                locationView.append(getRawMeasurements().getLocationBuilder());

            }
        });
    }


    /**
     * Show sensor data
     */
    @Override
    public void notifySensor() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (sensorView == null) {
                sensorView = new TextView(getActivity());
                    LinearLayout.LayoutParams params =  new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT );
                    params.setMargins(0,0,0,20);
                    sensorView.setElevation(2);
                    sensorView.setLayoutParams(params);
                sensorView.setBackgroundResource(R.drawable.my_border);
                linearLayout.addView(sensorView);
            }
                sensorView.setEnabled(true);

                SpannableString ss = new SpannableString("Sensors:\n");
                StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                ss.setSpan(boldSpan, 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new RelativeSizeSpan(1.1f), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                sensorView.setText(ss);
                sensorView.append(getRawMeasurements().getSensorLog().toString().replace("[", "")
                        .replace("]", "")
                        .replace(",", ""));

            }

        });
    }

    //

    /**
     * Check if the measurement categories are activated, otherwise gray their display
     */
    @Override
    public void notifyDisable() {

        if (getRawMeasurements().stateLocation == false && locationView != null){
            locationView.setEnabled(false);
        }
        if (getRawMeasurements().stateGnss == false && GNSSView != null){
            GNSSView.setEnabled(false);
        }
        if (getRawMeasurements().stateSensor == false && sensorView != null){
            sensorView.setEnabled(false);
        }
        if (getRawMeasurements().stateNavigationMessage == false && NMView != null){
            NMView.setEnabled(false);
        }
    }



    public RawMeasurements getRawMeasurements() {
        return rawMeasurements;
    }


    public void setRawMeasurements(RawMeasurements rawMeasurements) {
        this.rawMeasurements = rawMeasurements;
    }

}
