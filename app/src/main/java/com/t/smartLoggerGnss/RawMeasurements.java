package com.t.smartLoggerGnss;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GnssClock;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class RawMeasurements extends Observer {

    //variables
    public LocationManager locationManager;
    Context context;
    boolean stateSensor;
    boolean stateLocation;
    boolean stateGnss;
    boolean stateNavigationMessage;
    float[] mgravity = new float[3];
    float[] geomagnetic = new float[3];
    float[] I = new float[16];
    float[] R = new float[16];
    float[] mOrientation = new float[3];
    private ArrayList csvSensor = new ArrayList();
    private List<Float> sensorMeasurement = new ArrayList<Float>();
    private List<String> sensorLog = new ArrayList<String>();
    private ArrayList csvLocation = new ArrayList();
    private ArrayList csvMeasurement = new ArrayList();
    private ArrayList csvNavigationMessage = new ArrayList();
    private StringBuilder gnssBuilder;
    private StringBuilder locationBuilder;
    private StringBuilder navigationMessageBuilder;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor gravity;
    private Sensor acceleration;
    private Sensor magnetometer;
    private Sensor ambientTemperature;
    private Sensor lightLevel;
    private Sensor pressure;
    private Sensor relativeHumidity;

    @SuppressLint("MissingPermission")
    public RawMeasurements(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }


    public SensorEventListener sensorEventListener = new SensorEventListener() {

        /**
         * Automatically retrieves sensor values when there is a change.
         * Writes the values in 2 variables.
         * The variable sensorMeasurement will be used for the file to be saved and the variable sensorLog for the display.
         * To display the data on the view, you have to call the notifySensorObserver() function
         * @param sensorEvent
         */
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Sensor sensor = sensorEvent.sensor;

            Date date = new Date();

            csvSensor.add(date.getTime());

            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                sensorMeasurement.set(1, sensorEvent.values[0]);
                sensorMeasurement.set(2, sensorEvent.values[1]);
                sensorMeasurement.set(3, sensorEvent.values[2]);

                sensorLog.set(1, " AccelerometerX = " + sensorMeasurement.get(1));
                sensorLog.set(2, "\n  AccelerometerY = " + sensorMeasurement.get(2));
                sensorLog.set(3, "\n  AccelerometerZ = " + sensorMeasurement.get(3));
            }

            if (sensor.getType() == Sensor.TYPE_GRAVITY) {

                sensorMeasurement.set(4, sensorEvent.values[0]);
                sensorMeasurement.set(5, sensorEvent.values[1]);
                sensorMeasurement.set(6, sensorEvent.values[2]);

                sensorLog.set(4, "\n  GravityX = " + sensorMeasurement.get(4));
                sensorLog.set(5, "\n  GravityY = " + sensorMeasurement.get(5));
                sensorLog.set(6, "\n  GravityZ = " + sensorMeasurement.get(6));
            }

            if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {

                sensorMeasurement.set(7, sensorEvent.values[0]);
                sensorMeasurement.set(8, sensorEvent.values[1]);
                sensorMeasurement.set(9, sensorEvent.values[2]);

                sensorLog.set(7, "\n  GyroscopeX = " + sensorMeasurement.get(7));
                sensorLog.set(8, "\n  GyroscopeY = " + sensorMeasurement.get(8));
                sensorLog.set(9, "\n  GyroscopeZ = " + sensorMeasurement.get(9));
            }

            if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

                sensorMeasurement.set(10, sensorEvent.values[0]);
                sensorMeasurement.set(11, sensorEvent.values[1]);
                sensorMeasurement.set(12, sensorEvent.values[2]);

                sensorLog.set(10, "\n  LinearAccelerationX = " + sensorMeasurement.get(10));
                sensorLog.set(11, "\n  LinearAccelerationY = " + sensorMeasurement.get(11));
                sensorLog.set(12, "\n  LinearAccelerationZ = " + sensorMeasurement.get(12));
            }

            if (SensorManager.getRotationMatrix(R, I, mgravity, geomagnetic)) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                mOrientation[0] = (float) Math.toDegrees(orientation[0]);
                mOrientation[1] = (float) Math.toDegrees(orientation[1]);
                mOrientation[2] = (float) Math.toDegrees(orientation[2]);

                sensorMeasurement.set(13, mOrientation[0]);
                sensorMeasurement.set(14, mOrientation[1]);
                sensorMeasurement.set(15, mOrientation[2]);

                sensorLog.set(13, "\n  OrientationX = " + sensorMeasurement.get(13));
                sensorLog.set(14, "\n  OrientationY = " + sensorMeasurement.get(14));
                sensorLog.set(15, "\n  OrientationZ = " + sensorMeasurement.get(15));
            }

            if (sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                sensorMeasurement.set(16, sensorEvent.values[0]);
                sensorLog.set(16, "\n  AmbientTemperature = " + sensorMeasurement.get(16));
            }

            if (sensor.getType() == Sensor.TYPE_LIGHT) {
                sensorMeasurement.set(17, sensorEvent.values[0]);
                sensorLog.set(17, "\n  LightLevel = " + sensorMeasurement.get(17));
            }

            if (sensor.getType() == Sensor.TYPE_PRESSURE) {
                sensorMeasurement.set(18, sensorEvent.values[0]);
                sensorLog.set(18, "\n  Pressure = " + sensorMeasurement.get(18));
            }

            if (sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
                sensorMeasurement.set(19, sensorEvent.values[0]);
                sensorLog.set(19, "\n  RelativeHumidity = " + sensorMeasurement.get(19));
            }

            for (int i = 1; i < 19; i++) {
                csvSensor.add(sensorMeasurement.get(i));
            }

            csvSensor.add("end");

            notifySensorObserver();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };


    private final GnssMeasurementsEvent.Callback gnssEventListener = new GnssMeasurementsEvent.Callback() {

        /**
         *  Automatically retrieves gnss values when there is a change.
         *  calls the toStringMeasurement() and toStringClock() functions that displays and stores the measurements retrieved
         *  To display the data on the view, you have to call the  notifyGnssObserver() function
         * @param eventArgs
         */
        @Override
        public void onGnssMeasurementsReceived(GnssMeasurementsEvent eventArgs) {
            StringBuilder builder = new StringBuilder();

            for (GnssMeasurement measurement : eventArgs.getMeasurements()) {
                builder.append(toStringClock(eventArgs.getClock()));

                builder.append(toStringMeasurement(measurement));
                builder.append("\n");
            }

            builder.append("]");
            setGnssBuilder(builder);
            notifyGnssObserver();
        }

        @Override
        public void onStatusChanged(int status) {
            super.onStatusChanged(status);
        }
    };


    private final GnssNavigationMessage.Callback gnssNavigationMessageListener = new GnssNavigationMessage.Callback() {

        /**
         * Automatically retrieves gnss navigation message values when there is a change.
         * calls the toStringNavigationMessage() function that displays and stores the measurements retrieved
         * To display the data on the view, you have to call the  notifyStatusObserver() function
         * @param event
         */
        @Override
        public void onGnssNavigationMessageReceived(GnssNavigationMessage event) {
            StringBuilder builder = new StringBuilder();
            builder.append(toStringNavigationMessage(event));
            setNavigationMessageBuilder(builder);
            notifyNavigationMessageObserver();
        }

        @Override
        public void onStatusChanged(int status) {
        }
    };


    private LocationListener locationListener = new LocationListener() {

        /**
         * Automatically retrieves location values when there is a change.
         * calls the toStringLocation() function that displays and stores the measurements retrieved
         * To display the data on the view, you have to call the  notifyLocationObserver() function
         * @param location
         */
        @Override
        public void onLocationChanged(Location location) {
            StringBuilder builder = new StringBuilder();
            builder.append(toStringLocation(location));
            setLocationBuilder(builder);
            notifyLocationObserver();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    /**
     * Retrieve a clock element, add it to the csvMeasurement variable which will then be present in the file to be saved.
     * and formats it to display it on the app.
     * @param gnssClock
     */
    public String toStringClock(GnssClock gnssClock) {
        final String format = "   %-4s = %s\n";
        StringBuilder builder = new StringBuilder("GnssClock:\n");
        DecimalFormat numberFormat = new DecimalFormat("#0.000");

        csvMeasurement.add("Raw");

        builder.append(
                String.format(format, "ElapsedRealtimeMillis", SystemClock.elapsedRealtime()));
        csvMeasurement.add(SystemClock.elapsedRealtime());

        builder.append(String.format(format, "TimeNanos", gnssClock.getTimeNanos()));
        csvMeasurement.add(gnssClock.getTimeNanos());

        if (gnssClock.hasLeapSecond()) {
            builder.append(String.format(format, "LeapSecond", gnssClock.getLeapSecond()));
            csvMeasurement.add(gnssClock.getLeapSecond());

        } else csvMeasurement.add("");

        if (gnssClock.hasTimeUncertaintyNanos()) {
            builder.append(
                    String.format(format, "TimeUncertaintyNanos", gnssClock.getTimeUncertaintyNanos()));
            csvMeasurement.add(gnssClock.getTimeUncertaintyNanos());

        } else csvMeasurement.add("");

        if (gnssClock.hasFullBiasNanos()) {
            builder.append(String.format(format, "FullBiasNanos", gnssClock.getFullBiasNanos()));
            csvMeasurement.add(gnssClock.getFullBiasNanos());

        } else csvMeasurement.add("");

        if (gnssClock.hasBiasNanos()) {
            builder.append(String.format(format, "BiasNanos", gnssClock.getBiasNanos()));
            csvMeasurement.add(gnssClock.getBiasNanos());

        } else csvMeasurement.add("");

        if (gnssClock.hasBiasUncertaintyNanos()) {
            builder.append(
                    String.format(format, "BiasUncertaintyNanos", numberFormat.format(gnssClock.getBiasUncertaintyNanos())));
            csvMeasurement.add(gnssClock.getBiasUncertaintyNanos());

        } else csvMeasurement.add("");

        if (gnssClock.hasDriftNanosPerSecond()) {
            builder.append(
                    String.format(format, "DriftNanosPerSecond", numberFormat.format(gnssClock.getDriftNanosPerSecond())));
            csvMeasurement.add(gnssClock.getDriftNanosPerSecond());

        } else csvMeasurement.add("");

        if (gnssClock.hasDriftUncertaintyNanosPerSecond()) {
            builder.append(
                    String.format(format, "DriftUncertaintyNanosPerSecond", numberFormat.format(gnssClock.getDriftUncertaintyNanosPerSecond())));
            csvMeasurement.add(gnssClock.getDriftUncertaintyNanosPerSecond());

        } else csvMeasurement.add("");

        builder.append(
                String.format(format, "HardwareClockDiscontinuityCount", gnssClock.getHardwareClockDiscontinuityCount()));
        csvMeasurement.add(gnssClock.getHardwareClockDiscontinuityCount());

        return builder.toString();
    }

    /**
     * Retrieve a gnss measure element, add it to the csvMeasurement variable which will then be present in the file to be saved.
     * and formats it to display it on the app.
     * @param measurement
     * @return
     */
    private String toStringMeasurement(GnssMeasurement measurement) {
        final String format = "   %-4s = %s\n";
        StringBuilder builder = new StringBuilder("GnssMeasurement:\n");
        DecimalFormat numberFormat = new DecimalFormat("#0.000");
        DecimalFormat numberFormat1 = new DecimalFormat("#0.000E00");

        builder.append(String.format(format, "Svid", measurement.getSvid()));
        csvMeasurement.add(measurement.getSvid());

        builder.append(String.format(format, "TimeOffsetNanos", measurement.getTimeOffsetNanos()));
        csvMeasurement.add(measurement.getTimeOffsetNanos());

        builder.append(String.format(format, "State", measurement.getState()));
        csvMeasurement.add(measurement.getState());

        builder.append(
                String.format(format,
                        "ReceivedSvTimeNanos",
                        measurement.getReceivedSvTimeNanos()));
        csvMeasurement.add(measurement.getReceivedSvTimeNanos());

        builder.append(
                String.format(format,
                        "ReceivedSvTimeUncertaintyNanos",
                        measurement.getReceivedSvTimeUncertaintyNanos()));
        csvMeasurement.add(measurement.getReceivedSvTimeUncertaintyNanos());

        builder.append(
                String.format(format,
                        "Cn0DbHz",
                        numberFormat.format(measurement.getCn0DbHz())));
        csvMeasurement.add(measurement.getCn0DbHz());

        builder.append(
                String.format(format,
                        "PseudorangeRateMetersPerSecond",
                        numberFormat.format(measurement.getPseudorangeRateMetersPerSecond())));
        csvMeasurement.add(measurement.getPseudorangeRateMetersPerSecond());

        builder.append(
                String.format(format,
                        "PseudorangeRateUncertaintyMetersPerSeconds",
                        numberFormat.format(measurement.getPseudorangeRateUncertaintyMetersPerSecond())));
        csvMeasurement.add(measurement.getPseudorangeRateUncertaintyMetersPerSecond());

        builder.append(
                String.format(format,
                        "AccumulatedDeltaRangeState",
                        measurement.getAccumulatedDeltaRangeState()));
        csvMeasurement.add(measurement.getAccumulatedDeltaRangeState());

        builder.append(
                String.format(format,
                        "AccumulatedDeltaRangeMeters",
                        numberFormat.format(measurement.getAccumulatedDeltaRangeMeters())));
        csvMeasurement.add(measurement.getAccumulatedDeltaRangeMeters());

        builder.append(
                String.format(format,
                        "AccumulatedDeltaRangeUncertaintyMeters",
                        numberFormat1.format(measurement.getAccumulatedDeltaRangeUncertaintyMeters())));
        csvMeasurement.add(measurement.getAccumulatedDeltaRangeUncertaintyMeters());

        if (measurement.hasCarrierFrequencyHz()) {
            builder.append(
                    String.format(format,
                            "CarrierFrequencyHz",
                            measurement.getCarrierFrequencyHz()));
            csvMeasurement.add(measurement.getCarrierFrequencyHz());
        } else csvMeasurement.add("");

        if (measurement.hasCarrierCycles()) {
            builder.append(
                    String.format(format,
                            "CarrierCycles",
                            measurement.getCarrierCycles()));
            csvMeasurement.add(measurement.getCarrierCycles());
        } else csvMeasurement.add("");

        if (measurement.hasCarrierPhase()) {
            builder.append(
                    String.format(format,
                            "CarrierPhase",
                            measurement.getCarrierPhase()));
            csvMeasurement.add(measurement.getCarrierPhase());
        } else csvMeasurement.add("");

        if (measurement.hasCarrierPhaseUncertainty()) {
            builder.append(
                    String.format(format,
                            "CarrierPhaseUncertainty",
                            measurement.getCarrierPhaseUncertainty()));
            csvMeasurement.add(measurement.getCarrierPhaseUncertainty());
        } else csvMeasurement.add("");

        builder.append(
                String.format(format,
                        "MultipathIndicator",
                        measurement.getMultipathIndicator()));
        csvMeasurement.add(measurement.getMultipathIndicator());


        if (measurement.hasSnrInDb()) {
            builder.append(String.format(format, "SnrInDb", measurement.getSnrInDb()));
            csvMeasurement.add(measurement.getSnrInDb());
        } else csvMeasurement.add("");

        builder.append(String.format(format, "ConstellationType", measurement.getConstellationType()));
        csvMeasurement.add(measurement.getConstellationType());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (measurement.hasAutomaticGainControlLevelDb()) {
                builder.append(
                        String.format(format, "AgcDb", measurement.getAutomaticGainControlLevelDb()));
                csvMeasurement.add(measurement.getAutomaticGainControlLevelDb());

            } else csvMeasurement.add("");
            if (measurement.hasCarrierFrequencyHz()) {
                builder.append(String.format(format, "CarrierFreqHz", measurement.getCarrierFrequencyHz()));
                csvMeasurement.add(measurement.getCarrierFrequencyHz());

            } else csvMeasurement.add("");
        }

        csvMeasurement.add("end");

        return builder.toString();
    }

    /**
     * Retrieve a location element, add it to the csvLocation variable which will then be present in the file to be saved.
     * and formats it to display it on the app.
     * @param location
     */
    public String toStringLocation(Location location) {
        StringBuilder builder = new StringBuilder();

        Date date = new Date();

        csvLocation.add(date.getTime());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            builder.append("  GNSSHardwareModelName = " + locationManager.getGnssHardwareModelName());
            csvLocation.add(locationManager.getGnssHardwareModelName().replace(',', ' '));

            builder.append("\n  GNSSYearOfHardware = " + locationManager.getGnssYearOfHardware());
            csvLocation.add(locationManager.getGnssYearOfHardware());
        } else {
            csvLocation.add(",");
        }
        builder.append("\n  ElapsedRealtimeNanos = " + location.getElapsedRealtimeNanos());
        csvLocation.add(location.getElapsedRealtimeNanos());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (location.hasElapsedRealtimeUncertaintyNanos()) {
                builder.append("\n  ElapsedRealtimeUncertaintyNanos = " + location.getElapsedRealtimeUncertaintyNanos());
                csvLocation.add(location.getElapsedRealtimeUncertaintyNanos());

            } else {
                csvLocation.add("");
            }
        } else {
            csvLocation.add("");
        }
        builder.append("\n  Time = " + location.getTime());
        csvLocation.add(location.getTime());

        if (location.isFromMockProvider() == false) {
            builder.append("\n  Latitude = " + location.getLatitude());
            csvLocation.add(location.getLatitude());

            builder.append("\n  Longitude = " + location.getLongitude());
            csvLocation.add(location.getLongitude());

            if (location.hasAltitude()) {
                builder.append("\n  Altitude = " + location.getAltitude());
                csvLocation.add(location.getAltitude());

            } else {
                csvLocation.add("");
            }
            if (location.hasBearing()) {
                builder.append("\n  Bearing = " + location.getBearing());
                csvLocation.add(location.getBearing());

            } else {
                csvLocation.add("");
            }
            if (location.hasSpeed()) {
                builder.append("\n  Speed = " + location.getSpeed());
                csvLocation.add(location.getSpeed());

            } else {
                csvLocation.add("");
            }
            if (location.hasAccuracy()) {
                builder.append("\n  Accuracy = " + location.getAccuracy());
                csvLocation.add(location.getAccuracy());

            } else {
                csvLocation.add("");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (location.hasVerticalAccuracy()) {
                    builder.append("\n  VerticalAccuracy = " + location.getVerticalAccuracyMeters());
                    csvLocation.add(location.getVerticalAccuracyMeters());

                } else {
                    csvLocation.add("");
                }
                if (location.hasBearingAccuracy()) {
                    builder.append("\n  BearingAccuracy" + location.getBearingAccuracyDegrees());
                    csvLocation.add(location.getBearingAccuracyDegrees());

                } else {
                    csvLocation.add("");
                }
                if (location.hasSpeedAccuracy()) {
                    builder.append("\n  SpeedAccuracy = " + location.getSpeedAccuracyMetersPerSecond());
                    csvLocation.add(location.getSpeedAccuracyMetersPerSecond());

                } else {
                    csvLocation.add("");
                }
            } else {
                csvLocation.add("");
            }

            csvLocation.add("end");
        }

        return builder.toString();
    }

    /**
     * Retrieve a navigation message element, add it to the csvNavigationMessage variable which will then be present in the file to be saved.
     * and formats it to display it on the app.
     * @param message
     * @return
     */
    private String toStringNavigationMessage(GnssNavigationMessage message) {
        StringBuilder builder = new StringBuilder("");
        if (message != null) {

            Date date = new Date();

            csvNavigationMessage.add(date.getTime());

            builder.append("  Type = " + getGnssNavigationMessageType(message.getType()));
            csvNavigationMessage.add(getGnssNavigationMessageType(message.getType()));

            builder.append("\n  Svid = " + message.getSvid());
            csvNavigationMessage.add(message.getSvid());

            builder.append("\n  Status = " + getGnssNavigationMessageStatus(message.getStatus()));
            csvNavigationMessage.add(getGnssNavigationMessageStatus(message.getStatus()));

            builder.append("\n  MessageId = " + message.getMessageId());
            csvNavigationMessage.add(message.getMessageId());

            builder.append("\n  SubmessageId = " + message.getSubmessageId());
            csvNavigationMessage.add(message.getSubmessageId());

            byte[] data = message.getData();
            String datadecode = "";
            for (byte word : data) {
                datadecode += word + " ; ";
            }

            builder.append("\n  Data = { " + datadecode + " }");
            csvNavigationMessage.add(datadecode);

            builder.append("\n  DescribeContents = " + message.describeContents());
            csvNavigationMessage.add(message.describeContents());

            csvNavigationMessage.add("end");
        }
        return builder.toString();
    }

    /**
     * Associates the message id with its constellation type
     * @param type
     * @return
     */
    private String getGnssNavigationMessageType(int type) {
        switch (type) {
            case GnssNavigationMessage.TYPE_BDS_D1:
                return "Beidou D1";
            case GnssNavigationMessage.TYPE_BDS_D2:
                return "Beidou D2";
            case GnssNavigationMessage.TYPE_GAL_F:
                return "Galileo F/NAV";
            case GnssNavigationMessage.TYPE_GAL_I:
                return "Galileo I/NAV";
            case GnssNavigationMessage.TYPE_GLO_L1CA:
                return "Glonass L1 CA ";
            case GnssNavigationMessage.TYPE_GPS_CNAV2:
                return "GPS CNAV-2";
            case GnssNavigationMessage.TYPE_GPS_L1CA:
                return "GPS L1 C/A";
            case GnssNavigationMessage.TYPE_GPS_L2CNAV:
                return "GPS L2-CNAV";
            case GnssNavigationMessage.TYPE_GPS_L5CNAV:
                return "GPS L5-CNAV";
            default:
                return "<Unknown>";
        }
    }

    /**
     * Associates the message id with its status
     * @param status
     * @return
     */
    private String getGnssNavigationMessageStatus(int status) {
        switch (status) {
            case GnssNavigationMessage.STATUS_UNKNOWN:
                return "Status Unknown";
            case GnssNavigationMessage.STATUS_PARITY_PASSED:
                return "ParityPassed";
            case GnssNavigationMessage.STATUS_PARITY_REBUILT:
                return "ParityRebuilt";
            default:
                return "<Unknown>";
        }
    }

    /**
     * starts location data recovery
     */
    @SuppressLint("MissingPermission")
    public void starLocation() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        stateLocation = true;
    }

    /**
     * Stops location data recovery
     */
    public void stopLocation() {
        locationManager.removeUpdates(locationListener);
        stateLocation = false;
        notifyDisableObserver();
    }

    /**
     * Starts gnss measure data recovery
     */
    @SuppressLint("MissingPermission")
    public void startGnss() {
        locationManager.registerGnssMeasurementsCallback(gnssEventListener);
        stateGnss = true;
    }

    /**
     * Stops  gnss measure data recovery
     */
    public void stopGnss() {
        csvMeasurement.clear();
        locationManager.unregisterGnssMeasurementsCallback(gnssEventListener);
        locationManager.removeUpdates(locationListener);
        stateGnss = false;
        notifyDisableObserver();
    }

    /**
     * Starts navigation message data recovery
     */
    @SuppressLint("MissingPermission")
    public void startNavigationMessage() {
        locationManager.registerGnssNavigationMessageCallback(gnssNavigationMessageListener);
        stateNavigationMessage = true;
    }

    /**
     * Stops navigation message data recovery
     */
    public void stopNavigationMessage() {
        locationManager.unregisterGnssNavigationMessageCallback(gnssNavigationMessageListener);
        stateNavigationMessage = false;
        notifyDisableObserver();
    }

    /**
     * Starts sensors data recovery
     */
    public void startSensor() {

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        acceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        ambientTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        lightLevel = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        relativeHumidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

        if (sensorMeasurement.size() != 19 || sensorLog.size() != 19) {
            for (int i = 0; i < 19; i++) {
                sensorMeasurement.add((float) 0);
                sensorLog.add("");

            }
        }
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, gravity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, acceleration, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, ambientTemperature, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, lightLevel, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, pressure, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, relativeHumidity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);

        stateSensor = true;
    }

    /**
     * Stops sensors data recovery
     */
    public void stopSensor() {
        sensorManager.unregisterListener(sensorEventListener);
        stateSensor = false;
        notifyDisableObserver();
    }


    public StringBuilder getLocationBuilder() { return locationBuilder; }

    public void setLocationBuilder(StringBuilder locationBuilder) { this.locationBuilder = locationBuilder; }

    public StringBuilder getGnssBuilder() { return gnssBuilder; }

    public void setGnssBuilder(StringBuilder gnssBuilder) { this.gnssBuilder = gnssBuilder; }

    public StringBuilder getNavigationMessageBuilder() { return navigationMessageBuilder; }

    public void setNavigationMessageBuilder(StringBuilder navigationMessageBuilder) { this.navigationMessageBuilder = navigationMessageBuilder; }

    public ArrayList getCsvSensor() { return csvSensor; }

    public ArrayList getCsvNavigationMessage() { return csvNavigationMessage; }

    public ArrayList getCsvMeasurement() { return csvMeasurement; }

    public ArrayList getCsvLocation() { return csvLocation; }

    public List<String> getSensorLog() { return sensorLog; }
}

