package lcwu.fyp.obdcardiagnosticpro;

import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.Toast;
import com.github.anastr.speedviewlib.SpeedView;
import com.github.capur16.digitspeedviewlib.DigitSpeedView;
import com.sohrab.obd.reader.obdCommand.ObdConfiguration;
import com.sohrab.obd.reader.service.ObdReaderService;
import com.sohrab.obd.reader.trip.TripRecord;
import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_OBD_CONNECTION_STATUS;
import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_READ_OBD_REAL_TIME_DATA;

public class LiveData extends AppCompatActivity {
    private SpeedView speedView;
    private DigitSpeedView rpmReading,engineLoad, intakeTemp, engineTemp;
    private LinearLayout progress,main;
    private Menu menu;
    private Session session;
    private final BroadcastReceiver mObdReaderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("MY-OBD","Receiver Starts");
            String action = intent.getAction();
            Log.e("MY-OBD","Action:" + action);
            if (action == null){
                return;
            }
            if(action.equals(ACTION_OBD_CONNECTION_STATUS)){
                String connectionStatusMsg = intent.getStringExtra(ObdReaderService.INTENT_OBD_EXTRA_DATA);
                Toast.makeText(LiveData.this,connectionStatusMsg, Toast.LENGTH_SHORT).show();
                if(connectionStatusMsg == null){
                    getMenuInflater().inflate(R.menu.dashboard_menu,menu);
                    progress.setVisibility(View.INVISIBLE);
                    main.setVisibility(View.VISIBLE);
                }
                else if(connectionStatusMsg.equals(getString(R.string.obd_connected))) {
                    getMenuInflater().inflate(R.menu.dashboard_connected_menu,menu);
                    progress.setVisibility(View.INVISIBLE);
                    main.setVisibility(View.VISIBLE);
                }

                else if (connectionStatusMsg.equals(getString(R.string.connect_lost))){
                    getMenuInflater().inflate(R.menu.dashboard_menu,menu);
                    main.setVisibility(View.INVISIBLE);
                    progress.setVisibility(View.VISIBLE);
                }else {

                }
            }else if (action.equals(ACTION_READ_OBD_REAL_TIME_DATA)) {
                TripRecord tripRecord = TripRecord.getTripRecode(LiveData.this);
                int speed = tripRecord.getSpeed();
                String strRPM = tripRecord.getEngineRpm();
                String strEngineLoad = tripRecord.getmEngineLoad();
                String strInTakeTemp = tripRecord.getmAmbientAirTemp();
                String strEngineCoolantTemp = tripRecord.getmEngineCoolantTemp();
                session.setRPM("Speed: " + speed + "\n RPM: " + strRPM + "\n Engine Load: " + strEngineLoad + "\n Intake Temp: " + strEngineLoad + "\n Coolant Temp: " + strEngineCoolantTemp);
                speedView.speedTo(speed, 3000);
                try{
                    rpmReading.updateSpeed(Integer.parseInt(strRPM));
                    engineLoad.updateSpeed(Integer.parseInt(strEngineLoad));
                    intakeTemp.updateSpeed(Integer.parseInt(strInTakeTemp));
                    engineTemp.updateSpeed(Integer.parseInt(strEngineCoolantTemp));
                }
                catch (Exception e){
                    Log.e("LiveData", "String to int parsing error");
                }
            }
        };

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_data);
        progress = findViewById(R.id.progress);
        main =findViewById(R.id.main);
        main.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);
        speedView = findViewById(R.id.speedView);
        rpmReading = findViewById(R.id.rpmReading);
        engineLoad = findViewById(R.id.engineLoad);
        engineTemp = findViewById(R.id.engineTemp);
        intakeTemp = findViewById(R.id.intakeTemp);
        session = new Session(LiveData.this);

        ObdConfiguration.setmObdCommands(LiveData.this,null);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_READ_OBD_REAL_TIME_DATA);
        intentFilter.addAction(ACTION_OBD_CONNECTION_STATUS);
        registerReceiver(mObdReaderReceiver,intentFilter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.dashboard_menu,menu);
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
