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

import com.github.anastr.speedviewlib.SpeedView;
import com.github.capur16.digitspeedviewlib.DigitSpeedView;
import com.sohrab.obd.reader.application.ObdPreferences;
import com.sohrab.obd.reader.service.ObdReaderService;
import com.sohrab.obd.reader.trip.TripRecord;

import java.util.Date;

import lcwu.fyp.obdcardiagnosticpro.director.Helpers;

import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_OBD_CONNECTION_STATUS;
import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_READ_OBD_REAL_TIME_DATA;

public class MainDashboard extends AppCompatActivity {
    // No reading, except speed.
    private SpeedView speedView;
    private DigitSpeedView rpmReading, engineLoad, intakeTemp, engineTemp;
    private LinearLayout progress, main;
    private Session session;
    private MenuItem item;
    private Helpers helpers;
    private TextView drivingDuration, drivingDistance, drivingIdleDuration, drivingFuelConsumption;
    private String strDrivingDuration, strDrivingDistance, strDrivingIdleDuration, strDrivingFuelConsumption;
    private int intSpeed, intRpm, intEngineLoad, intIntakeTemp, intEngineTemp;

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
                    progress.setVisibility(View.VISIBLE);
                    main.setVisibility(View.GONE);
                } else if (connectionStatusMsg.equals(getString(R.string.obd_connected))) {
                    item.setTitle("OBD CONNECTED");
                    progress.setVisibility(View.GONE);
                    main.setVisibility(View.VISIBLE);
                } else if (connectionStatusMsg.equals(getString(R.string.connect_lost))) {
                    item.setTitle("NOT CONNECTED");
                    main.setVisibility(View.GONE);
                    progress.setVisibility(View.VISIBLE);
                }
            } else if (action.equals(ACTION_READ_OBD_REAL_TIME_DATA)) {
                item.setTitle("OBD CONNECTED");
                progress.setVisibility(View.GONE);
                main.setVisibility(View.VISIBLE);
                TripRecord tripRecord = TripRecord.getTripRecode(MainDashboard.this);
                if (tripRecord == null) {
                    helpers.showError(MainDashboard.this, "ERROR!", "Something went wrong.\nPlease try again later.");
                    return;
                }
                if (strDrivingDuration.length() < 1) {
                    strDrivingDuration = tripRecord.getDrivingDuration() + " Minutes";
                    drivingDuration.setText(strDrivingDuration);
                }
                if (strDrivingDistance.length() < 1) {
                    strDrivingDistance = tripRecord.getmDistanceTravel() + " KM";
                    drivingDistance.setText(strDrivingDistance);
                }
                if (strDrivingIdleDuration.length() < 1) {
                    strDrivingIdleDuration = tripRecord.getIdlingDuration() + " Minutes";
                    drivingIdleDuration.setText(strDrivingIdleDuration);
                }
                if (strDrivingFuelConsumption.length() < 1) {
                    strDrivingFuelConsumption = tripRecord.getmDrivingFuelConsumption() + "";
                    drivingFuelConsumption.setText(strDrivingFuelConsumption);
                }
                if (intSpeed < 1) {
                    intSpeed = tripRecord.getSpeed();
                    speedView.speedTo(intSpeed, 3000);
                }
                if (intRpm < 1) {
                    String strRPM = tripRecord.getEngineRpm();
                    if (strRPM != null && !strRPM.equals("null")) {
                        intRpm = Integer.parseInt(strRPM);
                        rpmReading.updateSpeed(intRpm);
                    }
                }

                String strEngineLoad = tripRecord.getmEngineLoad();
                String strInTakeTemp = tripRecord.getmAmbientAirTemp();
                String strEngineCoolantTemp = tripRecord.getmEngineCoolantTemp();
                Date d = new Date();

                try {
                    String result = "\nMain Dashboard " + d.toString() + "";
                    result = result + "\nIntake Temp: " + strInTakeTemp;
                    session.setRPM(result);

                    if (intEngineLoad < 1) {
                        if (strEngineLoad != null && !strEngineLoad.equals("null")) {
                            String[] temp = strEngineLoad.split("%");
                            if (temp.length > 0) {
                                double value = Double.parseDouble(temp[0]);
                                intEngineLoad = (int) value;
                                engineLoad.updateSpeed(intEngineLoad);
                            }
                        }
                    }

                    if (intIntakeTemp < 1) {
                        if (strInTakeTemp != null && !strInTakeTemp.equals("null")) {
                            String[] temp = strInTakeTemp.split("%");
                            if (temp.length > 0) {
                                double value = Double.parseDouble(temp[0]);
                                intIntakeTemp = (int) value;
                                result = "\nMain Dashboard " + d.toString() + " Air Intake Temp, Parsed successfully, Value is: " + intIntakeTemp;
                                session.setRPM(result);
                                intakeTemp.updateSpeed(intIntakeTemp);
                            } else {
                                result = "\nMain Dashboard " + d.toString() + " Air Intake Temp, Split Array length is less than 1";
                                session.setRPM(result);
                            }
                        } else {
                            result = "\nMain Dashboard " + d.toString() + " Air Intake Temp value is null";
                            session.setRPM(result);
                        }
                    }

                    if (intEngineTemp < 1) {
                        if (strEngineCoolantTemp != null && !strEngineCoolantTemp.equals("null")) {
                            String[] temp = strEngineCoolantTemp.split("C");
                            if (temp.length > 0) {
                                intEngineTemp = Integer.parseInt(temp[0]);
                                engineTemp.updateSpeed(intEngineTemp);
                            }
                        }
                    }

                } catch (Exception e) {
                    String result = "Main Dashboard " + d.toString();
                    result = result + "\nException: " + e.getMessage();
                    session.setRPM(result);
                }
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);

        progress = findViewById(R.id.progress);
        main = findViewById(R.id.main);
        main.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);
        speedView = findViewById(R.id.speedView);
        rpmReading = findViewById(R.id.rpmReading);
        engineLoad = findViewById(R.id.engineLoad);
        engineTemp = findViewById(R.id.engineTemp);
        intakeTemp = findViewById(R.id.intakeTemp);
        drivingDuration = findViewById(R.id.drivingDuration);
        drivingDistance = findViewById(R.id.drivingDistance);
        drivingIdleDuration = findViewById(R.id.drivingIdleDuration);
        drivingFuelConsumption = findViewById(R.id.drivingFuelConsumption);

        session = new Session(MainDashboard.this);

        helpers = new Helpers();

        rpmReading.updateSpeed(0);
        engineLoad.updateSpeed(0);
        intakeTemp.updateSpeed(0);
        engineTemp.updateSpeed(0);

        strDrivingDistance = strDrivingDuration = strDrivingFuelConsumption = strDrivingIdleDuration = "";
        intSpeed = intRpm = intEngineLoad = intIntakeTemp = intEngineTemp = -1;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_READ_OBD_REAL_TIME_DATA);
        intentFilter.addAction(ACTION_OBD_CONNECTION_STATUS);
        registerReceiver(mObdReaderReceiver, intentFilter);
        startService(new Intent(MainDashboard.this, ObdReaderService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mObdReaderReceiver);
        stopService(new Intent(this, ObdReaderService.class));
        ObdPreferences.get(this).setServiceRunningStatus(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        item = menu.findItem(R.id.connection);
        return super.onCreateOptionsMenu(menu);
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
