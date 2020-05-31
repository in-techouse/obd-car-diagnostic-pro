package lcwu.fyp.obdcardiagnosticpro;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.sohrab.obd.reader.application.ObdPreferences;
import com.sohrab.obd.reader.obdCommand.ObdConfiguration;
import com.sohrab.obd.reader.service.ObdReaderService;
import com.sohrab.obd.reader.trip.TripRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import lcwu.fyp.obdcardiagnosticpro.adapters.ODBBluetoothAdapter;
import lcwu.fyp.obdcardiagnosticpro.dialogue_box.InfoDialogue;
import lcwu.fyp.obdcardiagnosticpro.dialogue_box.MeterDialogue;
import lcwu.fyp.obdcardiagnosticpro.director.Helpers;
import lcwu.fyp.obdcardiagnosticpro.model.BluetoothObject;

import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_OBD_CONNECTION_STATUS;
import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_READ_OBD_REAL_TIME_DATA;

public class Dashboard extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_ENABLE_BT = 1; // Unique request code
    private List<BluetoothObject> data;
    private ODBBluetoothAdapter adapter;
    private boolean isConnected;
    private Helpers helper;
    private ArrayList<String> deviceStrs = new ArrayList();
    private ArrayList<String> devices = new ArrayList();
    private MenuItem item;
    private int count = 0;
    private ProgressDialog progressDialog;
    private TripRecord tripRecord;
    private Session session;
    private int engineTemperature, airIntakeTemperature;

    private final BroadcastReceiver mObdReaderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            if (action.equals(ACTION_OBD_CONNECTION_STATUS)) {
                String connectionStatusMsg = intent.getStringExtra(ObdReaderService.INTENT_OBD_EXTRA_DATA);
                if (connectionStatusMsg == null) {
                    item.setTitle("NOT CONNECTED");
                    isConnected = false;
                } else if (connectionStatusMsg.equals(getString(R.string.obd_connected))) {
                    isConnected = true;
                    item.setTitle("OBD CONNECTED");
                    progressDialog.dismiss();
                } else if (connectionStatusMsg.equals(getString(R.string.connect_lost))) {
                    item.setTitle("NOT CONNECTED");
                    isConnected = false;
                } else {
                    if (count > 5) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                helper.showError(Dashboard.this, "ERROR!", "The device isn't responding.\nPlease verify, the device is connected correctly to the car.");
                            }
                        }, 5000);
                        unregisterReceiver(mObdReaderReceiver);
                        stopService(new Intent(Dashboard.this, ObdReaderService.class));
                        ObdPreferences.get(Dashboard.this).setServiceRunningStatus(false);
                        isConnected = false;
                        item.setTitle("NOT CONNECTED");
                        count = 0;
                    } else {
                        count++;
                    }
                }
            } else if (action.equals(ACTION_READ_OBD_REAL_TIME_DATA)) {
                isConnected = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                }, 5000);
                tripRecord = TripRecord.getTripRecode(Dashboard.this);
                Date d = new Date();
                try {
                    String str = tripRecord.getmEngineCoolantTemp();
                    if (str != null && !str.equals("null")) {
                        String[] temp = str.split("C");
                        if (temp.length > 0 && engineTemperature < 1) {
                            engineTemperature = Integer.parseInt(temp[0]);
                        }
                    }
                } catch (Exception e) {
                    Log.e("Dashboard", "Exception Occur: " + e.getMessage());
                    helper.showError(Dashboard.this, "ERROR!", "Something went wrong.\nPlease try again later.");
                }

                String airResult = "\nDashboard, Air InTake Temperature, Time: " + d.toString() + " ";
                try {
                    String str1 = tripRecord.getmAmbientAirTemp();
                    if (str1 != null && !str1.equals("null")) {
                        String[] temp = str1.split("%");
                        if (temp.length > 0 && airIntakeTemperature < 1) {
                            double value = Double.parseDouble(temp[0]);
                            airIntakeTemperature = (int) value;
                            airResult = airResult + "Value is: " + airIntakeTemperature;
                            session.setRPM(airResult);
                        } else {
                            airResult = airResult + "Parsing return array less than 1 or value is already assigned.";
                            session.setRPM(airResult);
                        }
                    } else {
                        airResult = airResult + "Value is null";
                        session.setRPM(airResult);
                    }
                } catch (Exception e) {
                    airResult = airResult + "Exception Occur: " + e.getMessage();
                    session.setRPM(airResult);
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Button connect = findViewById(R.id.connect);
        Button scan = findViewById(R.id.scan);
        Button cancel = findViewById(R.id.cancel);
        CardView dashboard = findViewById(R.id.dashboard);
        CardView livedata = findViewById(R.id.live_data);
        CardView allsensor = findViewById(R.id.all_sensor);
        CardView accelerationTests = findViewById(R.id.AccelerationTests);
        CardView airIntakeTemp = findViewById(R.id.AirIntakeTemp);
        CardView diagnosticTrouble = findViewById(R.id.DiagnosticTrouble);
        CardView card_rpm = findViewById(R.id.rpm);
        CardView card_speed = findViewById(R.id.speed);
        CardView enginetemprature = findViewById(R.id.engine_temperature);
        Button testActivity = findViewById(R.id.testActivity);

        isConnected = false;
        helper = new Helpers();
        data = new ArrayList<>();
        adapter = new ODBBluetoothAdapter(Dashboard.this);
        getBluetoothDevices();
        connect.setOnClickListener(this);
        scan.setOnClickListener(this);
        cancel.setOnClickListener(this);
        dashboard.setOnClickListener(this);
        livedata.setOnClickListener(this);
        allsensor.setOnClickListener(this);
        card_rpm.setOnClickListener(this);
        card_speed.setOnClickListener(this);
        enginetemprature.setOnClickListener(this);
        accelerationTests.setOnClickListener(this);
        airIntakeTemp.setOnClickListener(this);
        diagnosticTrouble.setOnClickListener(this);
        testActivity.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);

        session = new Session(getApplicationContext());

        engineTemperature = -1;
        airIntakeTemperature = -1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        item = menu.findItem(R.id.connection);
        return super.onCreateOptionsMenu(menu);
    }

    private void getBluetoothDevices() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }
        for (BluetoothDevice bt : pairedDevices) {
            BluetoothObject object = new BluetoothObject();
            object.setAddress(bt.getAddress());
            object.setName(bt.getName());
            data.add(object);
        }
        adapter.setBluetoothList(data);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.connect: {
                ConnectDevice();
                break;
            }

            case R.id.scan: {
                ScanDevice();
                break;
            }
            case R.id.cancel: {
                CancelDevice();
                break;
            }
            case R.id.dashboard: {
                if (!isConnected) {
                    helper.showError(Dashboard.this, "ERROR!", "No OBD is connected.\nPlease connect your OBD first.");
                    return;
                }
                Intent intent = new Intent(Dashboard.this, MainDashboard.class);
                startActivity(intent);
                break;
            }
            case R.id.all_sensor: {
                if (!isConnected) {
                    helper.showError(Dashboard.this, "ERROR!", "No OBD is connected.\nPlease connect your OBD first.");
                    return;
                }
                Intent intent = new Intent(Dashboard.this, AllSensors.class);
                startActivity(intent);
                break;
            }
            case R.id.live_data: {
                if (!isConnected) {
                    helper.showError(Dashboard.this, "ERROR!", "No OBD is connected.\nPlease connect your OBD first.");
                    return;
                }
                Intent intent = new Intent(Dashboard.this, LiveData.class);
                startActivity(intent);
                break;
            }
            case R.id.rpm: {
                if (!isConnected) {
                    helper.showError(Dashboard.this, "ERROR!", "No OBD is connected.\nPlease connect your OBD first.");
                    return;
                }
                if (tripRecord != null) {
                    // Show RPM
                    try {
                        String str = tripRecord.getEngineRpm();
                        if (str != null && !str.equals("null")) {
                            InfoDialogue rpmDialogue = new InfoDialogue(Dashboard.this, "CAR ENGINE RPM", Integer.parseInt(tripRecord.getEngineRpm()));
                            rpmDialogue.show();
                        }
                    } catch (Exception e) {
                        helper.showError(Dashboard.this, "ERROR!", "Something went wrong.\nPlease try again later.");
                    }
                }
                break;
            }
            case R.id.speed: {
                if (!isConnected) {
                    helper.showError(Dashboard.this, "ERROR!", "No OBD is connected.\nPlease connect your OBD first.");
                    return;
                }
                if (tripRecord != null) {
                    // Show SPEED
                    MeterDialogue dialogue = new MeterDialogue(Dashboard.this, "CAR SPEED", tripRecord.getSpeed());
                    dialogue.show();
                }
                break;
            }
            case R.id.engine_temperature: {
                // Reading is not correct.
                if (!isConnected) {
                    helper.showError(Dashboard.this, "ERROR!", "No OBD is connected.\nPlease connect your OBD first.");
                    return;
                }
                if (tripRecord != null) {
                    try {
                        if (engineTemperature > 0) {
                            // Show Engine Temperature
                            InfoDialogue engineTempDialogue = new InfoDialogue(Dashboard.this, "CAR ENGINE TEMPERATURE", engineTemperature);
                            engineTempDialogue.show();
                        }
                    } catch (Exception e) {
                        helper.showError(Dashboard.this, "ERROR!", "Something went wrong.\nPlease try again later.");
                    }
                }
                break;
            }
            case R.id.AccelerationTests: {
                if (!isConnected) {
                    helper.showError(Dashboard.this, "ERROR!", "No OBD is connected.\nPlease connect your OBD first.");
                    return;
                }
                if (tripRecord != null) {
                    // Show Acceleration Test
                    Intent it = new Intent(Dashboard.this, AccelerationTest.class);
                    startActivity(it);
                }
                break;
            }
            case R.id.AirIntakeTemp: {
                // Reading is not correct
                if (!isConnected) {
                    helper.showError(Dashboard.this, "ERROR!", "No OBD is connected.\nPlease connect your OBD first.");
                    return;
                }
                if (tripRecord != null) {
                    try {
                        if (airIntakeTemperature > 1) {
                            // Show Air in take Temperature
                            InfoDialogue airInTakeTempDialouge = new InfoDialogue(Dashboard.this, "CAR AIR INTAKE TEMPERATURE", airIntakeTemperature);
                            airInTakeTempDialouge.show();
                        } else {
                            helper.showError(Dashboard.this, "ERROR", "Air Intake Temp is less than 1");
                        }
                    } catch (Exception e) {
                        helper.showError(Dashboard.this, "ERROR", e.getMessage());
                    }
                } else {
                    helper.showError(Dashboard.this, "ERROR", "Trip Record is NULL");
                }
                break;
            }
            case R.id.DiagnosticTrouble: {
                if (!isConnected) {
                    helper.showError(Dashboard.this, "ERROR!", "No OBD is connected.\nPlease connect your OBD first.");
                    return;
                }
                Intent it = new Intent(Dashboard.this, DiagnosticTroubleCodes.class);
                startActivity(it);
                break;
            }
            case R.id.testActivity: {
                Intent it = new Intent(Dashboard.this, TestActivity.class);
                startActivity(it);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                getBluetoothDevices();
            } else if (resultCode == RESULT_CANCELED) {
                // Bluetooth was not enabled
                helper.showError(Dashboard.this, "ERROR", "Bluetooth is not enabled.");
            }
        }
    }

    private void ConnectDevice() {
        if (isConnected) {
            helper.showError(Dashboard.this, "ERROR", "Already Connected");
            return;
        }
        getAllDevices();
    }

    private void getAllDevices() {
        devices.clear();
        deviceStrs.clear();
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                deviceStrs.add(device.getName() + "\n" + device.getAddress());
                devices.add(device.getAddress());
            }
        }

        // show list
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Dashboard.this);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice, deviceStrs.toArray(new String[deviceStrs.size()]));

        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                String deviceAddress = devices.get(which);
                // TODO save deviceAddress
                BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);

                progressDialog.setTitle("CONNECTING!");
                progressDialog.setMessage("Connecting the OBD device.\nPlease wait...!");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.show();


                ObdConfiguration.setmObdCommands(Dashboard.this, null);
                float gasPrice = 7;
                ObdPreferences.get(Dashboard.this).setGasPrice(gasPrice);
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(ACTION_READ_OBD_REAL_TIME_DATA);
                intentFilter.addAction(ACTION_OBD_CONNECTION_STATUS);
                registerReceiver(mObdReaderReceiver, intentFilter);
                startService(new Intent(Dashboard.this, ObdReaderService.class));
            }
        });

        alertDialog.setTitle("Choose Bluetooth device");
        alertDialog.show();
    }

    private void ScanDevice() {
        if (!isConnected) {
            helper.showError(Dashboard.this, "ERROR", "YOU ARE NOT CONNECTED TO DEVICE");
            return;
        }
        progressDialog.setTitle("SCANNING CAR");
        progressDialog.setMessage("Accessing the ECU.\nPlease wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void CancelDevice() {
        if (!isConnected) {
            helper.showError(Dashboard.this, "ERROR", "YOU ARE NOT CONNECTED TO DEVICE");
            return;
        }
        unregisterReceiver(mObdReaderReceiver);
        stopService(new Intent(this, ObdReaderService.class));
        ObdPreferences.get(this).setServiceRunningStatus(false);
        isConnected = false;
        item.setTitle("NOT CONNECTED");
        count = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregister receiver
        unregisterReceiver(mObdReaderReceiver);
        //stop service
        stopService(new Intent(this, ObdReaderService.class));
        // This will stop background thread if any running immediately.
        ObdPreferences.get(this).setServiceRunningStatus(false);
    }

}
