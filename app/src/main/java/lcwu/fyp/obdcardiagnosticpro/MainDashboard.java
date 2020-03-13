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
import com.sohrab.obd.reader.obdCommand.ObdConfiguration;
import com.sohrab.obd.reader.service.ObdReaderService;
import com.sohrab.obd.reader.trip.TripRecord;

import lcwu.fyp.obdcardiagnosticpro.director.Helpers;

import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_OBD_CONNECTION_STATUS;
import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_READ_OBD_REAL_TIME_DATA;

public class MainDashboard extends AppCompatActivity {

    private SpeedView speedView;
    private DigitSpeedView rpmReading, engineLoad, intakeTemp, engineTemp;
    private LinearLayout progress, main;
    private Session session;
    private MenuItem item;
    private Helpers helpers;
    private TextView drivingDuration, drivingDistance, drivingIdleDuration, drivingFuelConsumption;

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
                int speed = tripRecord.getSpeed();
                String strRPM = tripRecord.getEngineRpm();
                String strEngineLoad = tripRecord.getmEngineLoad();
                String strInTakeTemp = tripRecord.getmAmbientAirTemp();
                String strEngineCoolantTemp = tripRecord.getmEngineCoolantTemp();
                session.setRPM("Speed: " + speed + "\n RPM: " + strRPM + "\n Engine Load: " + strEngineLoad + "\n Intake Temp: " + strEngineLoad + "\n Coolant Temp: " + strEngineCoolantTemp);
                speedView.speedTo(speed, 3000);
                try {
                    rpmReading.updateSpeed(Integer.parseInt(strRPM));
                    engineLoad.updateSpeed(Integer.parseInt(strEngineLoad));
                    intakeTemp.updateSpeed(Integer.parseInt(strInTakeTemp));
                    engineTemp.updateSpeed(Integer.parseInt(strEngineCoolantTemp));
                    drivingDuration.setText(tripRecord.getDrivingDuration() + " minutes");
                    drivingDistance.setText(tripRecord.getmDistanceTravel() + "");
                    drivingIdleDuration.setText(tripRecord.getIdlingDuration() + "");
                    drivingFuelConsumption.setText(tripRecord.getmDrivingFuelConsumption() + "");
                } catch (Exception e) {
                    Log.e("MainDashboard", "String to int parsing error");
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

        ObdConfiguration.setmObdCommands(MainDashboard.this, null);
        float gasPrice = 7;
        ObdPreferences.get(MainDashboard.this).setGasPrice(gasPrice);
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
