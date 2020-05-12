package lcwu.fyp.obdcardiagnosticpro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
    private LinearLayout connecting;
    private LinearLayout main;
    private TextView time040, time060, time080, time0100, time0120;
    private AccelerationTestObject test040, test060, test080, test0100, test0120;
    private MenuItem item;
    private Helpers helpers;
    private Session session;
    private boolean isTaken = false;
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
                        if (!isTaken) {
                            isTaken = true;
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
                                        if (sec > 58) {
                                            sec = 0;
                                            min++;
                                        } else {
                                            sec++;
                                        }
                                        if (min > 58) {
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
                                    else if (speed >= 0 && speed <= 60) {
                                        int sec = test060.getSecond();
                                        int min = test060.getMinute();
                                        int hour = test060.getHour();
                                        if (sec > 58) {
                                            sec = 0;
                                            min++;
                                        } else {
                                            sec++;
                                        }
                                        if (min > 58) {
                                            min = 0;
                                            hour++;
                                        } else {
                                            min++;
                                        }
                                        test060.setSecond(sec);
                                        test060.setMinute(min);
                                        test060.setHour(hour);
                                        time060.setText(hour + ":" + min + ":" + sec);
                                    }
                                    // Acceleration Test 0-80KM/hr
                                    else if (speed >= 0 && speed <= 80) {
                                        int sec = test080.getSecond();
                                        int min = test080.getMinute();
                                        int hour = test080.getHour();
                                        if (sec > 58) {
                                            sec = 0;
                                            min++;
                                        } else {
                                            sec++;
                                        }
                                        if (min > 58) {
                                            min = 0;
                                            hour++;
                                        } else {
                                            min++;
                                        }
                                        test080.setSecond(sec);
                                        test080.setMinute(min);
                                        test080.setHour(hour);
                                        time080.setText(hour + ":" + min + ":" + sec);
                                    }
                                    // Acceleration Test 0-100KM/hr
                                    else if (speed >= 0 && speed <= 100) {
                                        int sec = test0100.getSecond();
                                        int min = test0100.getMinute();
                                        int hour = test0100.getHour();
                                        if (sec > 58) {
                                            sec = 0;
                                            min++;
                                        } else {
                                            sec++;
                                        }
                                        if (min > 58) {
                                            min = 0;
                                            hour++;
                                        } else {
                                            min++;
                                        }
                                        test0100.setSecond(sec);
                                        test0100.setMinute(min);
                                        test0100.setHour(hour);
                                        time0100.setText(hour + ":" + min + ":" + sec);
                                    }
                                    // Acceleration Test 0-120KM/hr
                                    else if (speed >= 0 && speed <= 120) {
                                        int sec = test0120.getSecond();
                                        int min = test0120.getMinute();
                                        int hour = test0120.getHour();
                                        if (sec > 58) {
                                            sec = 0;
                                            min++;
                                        } else {
                                            sec++;
                                        }
                                        if (min > 58) {
                                            min = 0;
                                            hour++;
                                        } else {
                                            min++;
                                        }
                                        test0120.setSecond(sec);
                                        test0120.setMinute(min);
                                        test0120.setHour(hour);
                                        time0120.setText(hour + ":" + min + ":" + sec);
                                    }
                                    isTaken = false;
                                }
                            }, 950);
                            session.setRPM(result);
                        }
                    }
                } catch (Exception e) {
                    result = result + "\nException Occur: " + e.getMessage();
                    session.setRPM(result);
                }
            }
        }

        ;
    };


    private void updateTimer(final int speed) {
        Handler handler = new Handler();
        Log.e("AccelerationTest", "Update Timer Called with speed: " + speed);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("AccelerationTest", "Handler Executed");
                // From here, we're getting speed from the OBD, we will do our custom logic here.
                // Acceleration Test 0-40KM/hr
                if (speed >= 0 && speed <= 40) {
                    int sec = test040.getSecond();
                    int min = test040.getMinute();
                    int hour = test040.getHour();
                    Log.e("AccelerationTest", "Second: " + sec);
                    Log.e("AccelerationTest", "Minute: " + min);
                    Log.e("AccelerationTest", "Hour: " + hour);
                    if (sec < 59) {
                        sec++;
                    } else {
                        sec = 0;
                        min++;
                    }
                    if (min > 58) {
                        hour++;
                    }

//                if (sec > 58) {
//                    sec = 0;
//                    min++;
//                } else {
//                    sec++;
//                }
//                if (min > 58) {
//                    min = 0;
//                    hour++;
//                } else {
//                    min++;
//                }
                    test040.setSecond(sec);
                    test040.setMinute(min);
                    test040.setHour(hour);
                    time040.setText(hour + ":" + min + ":" + sec);
                    updateTimer(35);
                }
            }
        }, 1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceleration_test);


        helpers = new Helpers();
        session = new Session(getApplicationContext());

        connecting = findViewById(R.id.connecting);
        main = findViewById(R.id.main);
        ImageView reset040 = findViewById(R.id.reset040);
        ImageView reset060 = findViewById(R.id.reset060);
        ImageView reset080 = findViewById(R.id.reset080);
        ImageView reset0100 = findViewById(R.id.reset0100);
        ImageView reset0120 = findViewById(R.id.reset0120);

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
        updateTimer(39);
        connecting.setVisibility(View.GONE);
        main.setVisibility(View.VISIBLE);

//        connecting.setVisibility(View.VISIBLE);
//        main.setVisibility(View.GONE);
//
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(ACTION_READ_OBD_REAL_TIME_DATA);
//        intentFilter.addAction(ACTION_OBD_CONNECTION_STATUS);
//        registerReceiver(mObdReaderReceiver, intentFilter);
//        startService(new Intent(AccelerationTest.this, ObdReaderService.class));
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
