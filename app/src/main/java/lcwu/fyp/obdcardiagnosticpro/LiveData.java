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
import com.sohrab.obd.reader.obdCommand.ObdConfiguration;
import com.sohrab.obd.reader.service.ObdReaderService;
import com.sohrab.obd.reader.trip.TripRecord;

import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_OBD_CONNECTION_STATUS;
import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_READ_OBD_REAL_TIME_DATA;

public class LiveData extends AppCompatActivity{
    private LinearLayout progress,main;
    private Menu menu;
    private final BroadcastReceiver mObdReaderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("MY-OBD","Receiver Starts");
            String action = intent.getAction();
            Log.e("MY-OBD","Action:" + action);
            if (action==null){
                return;
            }
            if(action.equals(ACTION_OBD_CONNECTION_STATUS)){
                String connectionStatusMsg = intent.getStringExtra(ObdReaderService.INTENT_OBD_EXTRA_DATA);
                Toast.makeText(LiveData.this,connectionStatusMsg, Toast.LENGTH_SHORT).show();
                if(connectionStatusMsg.equals("OBD Connected")) {
                    getMenuInflater().inflate(R.menu.dashboard_connected_menu,menu);
                    progress.setVisibility(View.INVISIBLE);
                    main.setVisibility(View.VISIBLE);
                }

                else if (connectionStatusMsg.equals("Connect Lost")){
                    getMenuInflater().inflate(R.menu.dashboard_menu,menu);
                    main.setVisibility(View.INVISIBLE);
                    progress.setVisibility(View.VISIBLE);
                }else {
                }
            }else if (action.equals(ACTION_READ_OBD_REAL_TIME_DATA)) {
                TripRecord tripRecord = TripRecord.getTripRecode(LiveData.this);

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
        ObdConfiguration.setmObdCommands(LiveData.this,null);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_READ_OBD_REAL_TIME_DATA);
        intentFilter.addAction(ACTION_OBD_CONNECTION_STATUS);
        registerReceiver(mObdReaderReceiver,intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
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
