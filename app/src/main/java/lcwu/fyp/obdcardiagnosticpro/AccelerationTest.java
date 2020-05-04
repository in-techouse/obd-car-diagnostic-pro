package lcwu.fyp.obdcardiagnosticpro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sohrab.obd.reader.application.ObdPreferences;
import com.sohrab.obd.reader.service.ObdReaderService;
import com.sohrab.obd.reader.trip.TripRecord;

import java.util.Date;

import lcwu.fyp.obdcardiagnosticpro.director.Helpers;
import lcwu.fyp.obdcardiagnosticpro.model.AccelerationTestObject;

import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_OBD_CONNECTION_STATUS;
import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_READ_OBD_REAL_TIME_DATA;

public class AccelerationTest extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout speed040, speed060, speed080, speed0100, speed0120, connecting, main;
    private TextView time040, time060, time080, time0100, time0120;
    private ImageView reset040, reset060, reset080, reset0100, reset0120;
    private AccelerationTestObject test040, test060, test080, test0100, test0120;
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
                TripRecord tripRecord = TripRecord.getTripRecode(AccelerationTest.this);
                item.setTitle("OBD CONNECTED");
                connecting.setVisibility(View.GONE);
                main.setVisibility(View.VISIBLE);

                Date d = new Date();
                String result = "\nAcceleration Test: " + d.toString() + "";
                try {
                    if (tripRecord == null) {
                        helpers.showError(AccelerationTest.this, "ERROR!", "Something went wrong.\nPlease try again later.");
                        result = result + " Trip record is null";
                        session.setRPM(result);
                    } else {
                        result = result + "\nSpeed: " + tripRecord.getSpeed();
                        final int speed = tripRecord.getSpeed();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // From here, we're getting speed from the OBD, we will do our custom logic here.
                                // Acceleration Test 0-40KM/hr
                                if (speed >= 0 && speed <= 40) {
                                    int sec = test040.getSecond();
                                    int min = test040.getMinute();
                                    int hour = test040.getHour();
                                    if (sec == 59) {
                                        sec = 0;
                                        min++;
                                    } else {
                                        sec++;
                                    }
                                    if (min == 59) {
                                        min = 0;
                                        hour++;
                                    } else {
                                        min++;
                                    }
                                    test040.setSecond(sec);
                                    test040.setMinute(min);
                                    test040.setHour(hour);
                                    time040.setText(hour + ":" + min + ":" + sec);
                                }
                                // Acceleration Test 0-60KM/hr
                                if (speed >= 0 && speed <= 60) {

                                }
                                // Acceleration Test 0-80KM/hr
                                if (speed >= 0 && speed <= 80) {

                                }
                                // Acceleration Test 0-100KM/hr
                                if (speed >= 0 && speed <= 100) {

                                }
                                // Acceleration Test 0-120KM/hr
                                if (speed >= 0 && speed <= 100) {

                                }
                            }
                        }, 1000);
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
        setContentView(R.layout.activity_acceleration_test);


        helpers = new Helpers();
        session = new Session(getApplicationContext());

        connecting = findViewById(R.id.connecting);
        main = findViewById(R.id.main);
        speed040 = findViewById(R.id.speed040);
        speed060 = findViewById(R.id.speed060);
        speed080 = findViewById(R.id.speed080);
        speed0100 = findViewById(R.id.speed0100);
        speed0120 = findViewById(R.id.speed0120);
        reset040 = findViewById(R.id.reset040);
        reset060 = findViewById(R.id.reset060);
        reset080 = findViewById(R.id.reset080);
        reset0100 = findViewById(R.id.reset0100);
        reset0120 = findViewById(R.id.reset0120);

        time040 = findViewById(R.id.time040);
        time060 = findViewById(R.id.time060);
        time080 = findViewById(R.id.time080);
        time0100 = findViewById(R.id.time0100);
        time0120 = findViewById(R.id.time0120);

        reset040.setOnClickListener(this);
        reset060.setOnClickListener(this);
        reset080.setOnClickListener(this);
        reset0100.setOnClickListener(this);
        reset0120.setOnClickListener(this);

        test040 = new AccelerationTestObject();
        test060 = new AccelerationTestObject();
        test080 = new AccelerationTestObject();
        test0100 = new AccelerationTestObject();
        test0120 = new AccelerationTestObject();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_READ_OBD_REAL_TIME_DATA);
        intentFilter.addAction(ACTION_OBD_CONNECTION_STATUS);
        registerReceiver(mObdReaderReceiver, intentFilter);
        startService(new Intent(AccelerationTest.this, ObdReaderService.class));
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
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.reset040: {
                test040.setHour(0);
                test040.setMinute(0);
                test040.setSecond(0);
                time040.setText("000.000.000");
                break;
            }
            case R.id.reset060: {
                test060.setHour(0);
                test060.setMinute(0);
                test060.setSecond(0);
                time060.setText("000.000.000");
                break;
            }
            case R.id.reset080: {
                test080.setHour(0);
                test080.setMinute(0);
                test080.setSecond(0);
                time080.setText("000.000.000");
                break;
            }
            case R.id.reset0100: {
                test0100.setHour(0);
                test0100.setMinute(0);
                test0100.setSecond(0);
                time0100.setText("000.000.000");
                break;
            }
            case R.id.reset0120: {
                test0120.setHour(0);
                test0120.setMinute(0);
                test0120.setSecond(0);
                time0120.setText("000.000.000");
                break;
            }
        }
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
