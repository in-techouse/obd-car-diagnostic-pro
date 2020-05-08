package lcwu.fyp.obdcardiagnosticpro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sohrab.obd.reader.application.ObdPreferences;
import com.sohrab.obd.reader.service.ObdReaderService;
import com.sohrab.obd.reader.trip.TripRecord;

import java.util.Date;

import lcwu.fyp.obdcardiagnosticpro.director.Helpers;

import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_OBD_CONNECTION_STATUS;
import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_READ_OBD_REAL_TIME_DATA;

public class AllSensors extends AppCompatActivity {
    // OBD is disconnecting,
    private TextView airTemperature, engineCoolantTemperature, engineRpm, engineRpmMax, drivingDuration, engineRuntime,
            idlingDuration, vehicleIdentificationNumber, absLoad, airFuelRation, barometricPressure, controlModuleVoltage,
            describeProtocol, describeProtocolNumber, distanceTravel, distanceTravelMilOn, drivingFuelConsumption, engineFuelRate,
            dtcNumber;
    private LinearLayout main, connecting;
    private MenuItem item;
    private Helpers helpers;
    private Session session;

    private final BroadcastReceiver mObdReaderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("MY-OBD", "Receiver Starts");
            String action = intent.getAction();
            Log.e("MY-OBD", "Action:" + action);
            if (action == null) {
                return;
            }
            if (action.equals(ACTION_OBD_CONNECTION_STATUS)) {
                String connectionStatusMsg = intent.getStringExtra(ObdReaderService.INTENT_OBD_EXTRA_DATA);
                if (connectionStatusMsg == null) {
                    item.setTitle("NOT CONNECTED");
                    connecting.setVisibility(View.VISIBLE);
                    main.setVisibility(View.GONE);
                } else if (connectionStatusMsg.equals(getString(R.string.obd_connected))) {
                    item.setTitle("OBD CONNECTED");
                    connecting.setVisibility(View.GONE);
                    main.setVisibility(View.VISIBLE);
                } else if (connectionStatusMsg.equals(getString(R.string.connect_lost))) {
                    item.setTitle("NOT CONNECTED");
                    main.setVisibility(View.GONE);
                    connecting.setVisibility(View.VISIBLE);
                }
            } else if (action.equals(ACTION_READ_OBD_REAL_TIME_DATA)) {
                TripRecord tripRecord = TripRecord.getTripRecode(AllSensors.this);
                item.setTitle("OBD CONNECTED");
                connecting.setVisibility(View.GONE);
                main.setVisibility(View.VISIBLE);

                Date d = new Date();
                String result = "\nAll Sensors" + d.toString() + "";
                try {
                    if (tripRecord == null) {
                        helpers.showError(AllSensors.this, "ERROR!", "Something went wrong.\nPlease try again later.");
                        result = result + " Trip record is null";
                        session.setRPM(result);
                    } else {

                        result = result + "\nVehicle Identification Number: " + tripRecord.getmVehicleIdentificationNumber();
                        vehicleIdentificationNumber.setText(tripRecord.getmVehicleIdentificationNumber());

                        result = result + "\nDriving Duration: " + tripRecord.getDrivingDuration();
                        drivingDuration.setText(tripRecord.getDrivingDuration() + " minutes");

                        result = result + "\nIdling Duration: " + tripRecord.getIdlingDuration();
                        idlingDuration.setText(tripRecord.getIdlingDuration() + "");

                        result = result + "\nAir Temperature: " + tripRecord.getmAmbientAirTemp();
                        engineCoolantTemperature.setText(tripRecord.getmEngineCoolantTemp());

                        result = result + "\nEngine Coolant Temperature: " + tripRecord.getmEngineCoolantTemp();
                        airTemperature.setText(tripRecord.getmAmbientAirTemp());

                        result = result + "\nEngine RPM: " + tripRecord.getEngineRpm();
                        engineRpm.setText(tripRecord.getEngineRpm());

                        result = result + "\n Engine RPM Max: " + tripRecord.getEngineRpmMax();
                        engineRpmMax.setText(tripRecord.getEngineRpmMax() + "");

                        result = result + "\nEngine Run Time: " + tripRecord.getEngineRuntime();
                        engineRuntime.setText(tripRecord.getEngineRuntime());

                        result = result + "\nAbs Load: " + tripRecord.getmAbsLoad();
                        absLoad.setText(tripRecord.getmAbsLoad());

                        result = result + "\nAir Fuel Ratio: " + tripRecord.getmAirFuelRatio();
                        airFuelRation.setText(tripRecord.getmAmbientAirTemp());

                        result = result + "\nBarometric Pressure: " + tripRecord.getmBarometricPressure();
                        barometricPressure.setText(tripRecord.getmBarometricPressure());

                        result = result + "\nControlc Module Voltage" + tripRecord.getmControlModuleVoltage();
                        controlModuleVoltage.setText(tripRecord.getmControlModuleVoltage());

                        result = result + "\nDescribe Protocol" + tripRecord.getmDescribeProtocol();
                        describeProtocol.setText(tripRecord.getmDescribeProtocol());

                        result = result + "\nDescribe Protocol Number: " + tripRecord.getmDescribeProtocolNumber();
                        describeProtocolNumber.setText(tripRecord.getmDescribeProtocolNumber());

                        result = result + "\nDistance Travel" + tripRecord.getmDistanceTravel();
                        distanceTravel.setText(tripRecord.getmDistanceTravel() + "");

                        result = result + "\nDistance Travel Mil On" + tripRecord.getmDistanceTraveledMilOn();
                        distanceTravelMilOn.setText(tripRecord.getmDistanceTraveledMilOn());

                        result = result + "\nDriving Fuel Consumption" + tripRecord.getmDrivingFuelConsumption();
                        drivingFuelConsumption.setText(tripRecord.getmDrivingFuelConsumption() + "");

                        result = result + "\nEngine Fuel Rate: " + tripRecord.getmEngineFuelRate();
                        engineFuelRate.setText(tripRecord.getmEngineFuelRate());

                        result = result + "\nDTC number: " + tripRecord.getmDtcNumber();
                        dtcNumber.setText(tripRecord.getmDtcNumber());

                        tripRecord.getmEngineOilTemp();
                        tripRecord.getmEquivRatio();
                        tripRecord.getmFuelConsumptionRate();
                        tripRecord.getmFuelPressure();
                        tripRecord.getmFuelRailPressure();
                        tripRecord.getmIdlingFuelConsumption();
                        tripRecord.getmIgnitionMonitor();
                        tripRecord.getmInsFuelConsumption();
                        tripRecord.getmRapidAccTimes();
                        tripRecord.getmRapidDeclTimes();
                        tripRecord.getmThrottlePos();
                        tripRecord.getmRelThottlePos();
                        tripRecord.getmTripIdentifier();
                        tripRecord.getmTimingAdvance();
                        tripRecord.getmWideBandAirFuelRatio();


                        session.setRPM(result);
                    }
                } catch (Exception e) {
                    result = result + "\nException Occur: " + e.getMessage();
                    session.setRPM(result);
                }
            }
        }

        ;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_sensors);
        main = findViewById(R.id.main);
        connecting = findViewById(R.id.connecting);
        connecting.setVisibility(View.VISIBLE);
        main.setVisibility(View.GONE);

        airTemperature = findViewById(R.id.airTemperature);
        engineCoolantTemperature = findViewById(R.id.engineCoolantTemperature);
        engineRpm = findViewById(R.id.engineRpm);
        engineRpmMax = findViewById(R.id.engineRpmMax);
        drivingDuration = findViewById(R.id.drivingDuration);
        engineRuntime = findViewById(R.id.engineRuntime);
        idlingDuration = findViewById(R.id.idlingDuration);
        absLoad = findViewById(R.id.absLoad);
        vehicleIdentificationNumber = findViewById(R.id.vehicleIdentificationNumber);
        airFuelRation = findViewById(R.id.airFuelRation);
        barometricPressure = findViewById(R.id.barometricPressure);
        controlModuleVoltage = findViewById(R.id.controlModuleVoltage);
        describeProtocol = findViewById(R.id.describeProtocol);
        describeProtocolNumber = findViewById(R.id.describeProtocolNumber);
        distanceTravel = findViewById(R.id.distanceTravel);
        distanceTravelMilOn = findViewById(R.id.distanceTravelMilOn);
        drivingFuelConsumption = findViewById(R.id.drivingFuelConsumption);
        engineFuelRate = findViewById(R.id.engineFuelRate);
        dtcNumber = findViewById(R.id.dtcNumber);


        helpers = new Helpers();
        session = new Session(AllSensors.this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_READ_OBD_REAL_TIME_DATA);
        intentFilter.addAction(ACTION_OBD_CONNECTION_STATUS);
        registerReceiver(mObdReaderReceiver, intentFilter);
        startService(new Intent(AllSensors.this, ObdReaderService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        item = menu.findItem(R.id.connection);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mObdReaderReceiver);
        stopService(new Intent(this, ObdReaderService.class));
        ObdPreferences.get(this).setServiceRunningStatus(false);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return true;
    }
}
