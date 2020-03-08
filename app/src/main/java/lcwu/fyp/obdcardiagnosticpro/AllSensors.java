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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sohrab.obd.reader.application.ObdPreferences;
import com.sohrab.obd.reader.obdCommand.ObdConfiguration;
import com.sohrab.obd.reader.service.ObdReaderService;
import com.sohrab.obd.reader.trip.TripRecord;

import lcwu.fyp.obdcardiagnosticpro.director.Helpers;

import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_OBD_CONNECTION_STATUS;
import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_READ_OBD_REAL_TIME_DATA;

public class AllSensors extends AppCompatActivity {
    private TextView airTemperature, engineCoolantTemperature, engineRpm, engineRpmMax, drivingDuration, engineRuntime;
    private LinearLayout main, connecting;
    private MenuItem item;
    private Helpers helpers;


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
                Toast.makeText(AllSensors.this, connectionStatusMsg, Toast.LENGTH_SHORT).show();
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

                if (tripRecord == null) {
                    helpers.showError(AllSensors.this, "ERROR!", "Something went wrong.\nPlease try again later.");
                    return;
                }

                drivingDuration.setText(tripRecord.getDrivingDuration() + "");
                airTemperature.setText(tripRecord.getmAmbientAirTemp());
                engineCoolantTemperature.setText(tripRecord.getmEngineCoolantTemp());
                engineRpm.setText(tripRecord.getEngineRpm());
                engineRpmMax.setText(tripRecord.getEngineRpmMax());
                engineRuntime.setText(tripRecord.getEngineRuntime());

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

        helpers = new Helpers();

        ObdConfiguration.setmObdCommands(AllSensors.this, null);
        float gasPrice = 7;
        ObdPreferences.get(AllSensors.this).setGasPrice(gasPrice);
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
