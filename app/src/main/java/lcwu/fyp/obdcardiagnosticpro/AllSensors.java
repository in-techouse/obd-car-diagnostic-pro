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
            dtcNumber, engineOilTemp, equivRatio, fuelConsumptionRate, fuelPressure, fuelRailPressure, idlingFuelConsumption, ignitionMonitor,
            insFuelConsumption, rapidAccTimes, rapidDeclTimes, throttlePos, relThottlePos, tripIdentifier, timingAdvance, wideBandAirFuelRatio;
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

                        vehicleIdentificationNumber.setText(tripRecord.getmVehicleIdentificationNumber());

                        drivingDuration.setText(tripRecord.getDrivingDuration() + " minutes");

                        idlingDuration.setText(tripRecord.getIdlingDuration() + "");

                        result = result + "\nAir Temperature: " + tripRecord.getmAmbientAirTemp();
                        engineCoolantTemperature.setText(tripRecord.getmEngineCoolantTemp());

                        airTemperature.setText(tripRecord.getmAmbientAirTemp());

                        engineRpm.setText(tripRecord.getEngineRpm());

                        engineRpmMax.setText(tripRecord.getEngineRpmMax() + "");

                        engineRuntime.setText(tripRecord.getEngineRuntime());

                        result = result + "\nAbs Load: " + tripRecord.getmAbsLoad();
                        absLoad.setText(tripRecord.getmAbsLoad());

                        result = result + "\nAir Fuel Ratio: " + tripRecord.getmAirFuelRatio();
                        airFuelRation.setText(tripRecord.getmAmbientAirTemp());

                        barometricPressure.setText(tripRecord.getmBarometricPressure());

                        controlModuleVoltage.setText(tripRecord.getmControlModuleVoltage());

                        describeProtocol.setText(tripRecord.getmDescribeProtocol());

                        describeProtocolNumber.setText(tripRecord.getmDescribeProtocolNumber());

                        distanceTravel.setText(tripRecord.getmDistanceTravel() + "");

                        distanceTravelMilOn.setText(tripRecord.getmDistanceTraveledMilOn());

                        drivingFuelConsumption.setText(tripRecord.getmDrivingFuelConsumption() + "");

                        result = result + "\nEngine Fuel Rate: " + tripRecord.getmEngineFuelRate();
                        engineFuelRate.setText(tripRecord.getmEngineFuelRate());

                        dtcNumber.setText(tripRecord.getmDtcNumber());

                        result = result + "\nEngine Oil Temp: " + tripRecord.getmEngineOilTemp();
                        engineOilTemp.setText(tripRecord.getmEngineOilTemp());

                        result = result + "\nEquiv Ratio: " + tripRecord.getmEquivRatio();
                        equivRatio.setText(tripRecord.getmEquivRatio());

                        result = result + "\nFuel Consumption Rate:" + tripRecord.getmFuelConsumptionRate();
                        fuelConsumptionRate.setText(tripRecord.getmFuelConsumptionRate());

                        result = result + "\nFuel Pressure: " + tripRecord.getmFuelPressure();
                        fuelPressure.setText(tripRecord.getmFuelPressure());

                        result = result + "\nFuel Rail Pressure: " + tripRecord.getmFuelRailPressure();
                        fuelRailPressure.setText(tripRecord.getmFuelRailPressure());

                        idlingFuelConsumption.setText(tripRecord.getmIdlingFuelConsumption() + "");

                        ignitionMonitor.setText(tripRecord.getmIgnitionMonitor());

                        insFuelConsumption.setText(tripRecord.getmInsFuelConsumption() + "");

                        rapidAccTimes.setText(tripRecord.getmRapidAccTimes() + "");

                        rapidDeclTimes.setText(tripRecord.getmRapidDeclTimes() + "");

                        throttlePos.setText(tripRecord.getmThrottlePos());

                        result = result + "\nRel Thottle Pos: " + tripRecord.getmRelThottlePos();
                        relThottlePos.setText(tripRecord.getmRelThottlePos());

                        tripIdentifier.setText(tripRecord.getmTripIdentifier());

                        timingAdvance.setText(tripRecord.getmTimingAdvance());

                        result = result + "\nWide Band Air Fuel Ratio: " + tripRecord.getmWideBandAirFuelRatio();
                        wideBandAirFuelRatio.setText(tripRecord.getmWideBandAirFuelRatio());

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
        engineOilTemp = findViewById(R.id.engineOilTemp);
        equivRatio = findViewById(R.id.equivRatio);
        fuelConsumptionRate = findViewById(R.id.fuelConsumptionRate);
        fuelPressure = findViewById(R.id.fuelPressure);
        fuelRailPressure = findViewById(R.id.fuelRailPressure);
        idlingFuelConsumption = findViewById(R.id.idlingFuelConsumption);
        ignitionMonitor = findViewById(R.id.ignitionMonitor);
        insFuelConsumption = findViewById(R.id.insFuelConsumption);
        rapidAccTimes = findViewById(R.id.rapidAccTimes);
        rapidDeclTimes = findViewById(R.id.rapidDeclTimes);
        throttlePos = findViewById(R.id.throttlePos);
        relThottlePos = findViewById(R.id.relthottlePos);
        tripIdentifier = findViewById(R.id.tripIdentifier);
        timingAdvance = findViewById(R.id.timingAdvance);
        wideBandAirFuelRatio = findViewById(R.id.wideBandAirRatio);

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
