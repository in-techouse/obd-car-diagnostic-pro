package lcwu.fyp.obdcardiagnosticpro;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lcwu.fyp.obdcardiagnosticpro.adapters.ODBBluetoothAdapter;
import lcwu.fyp.obdcardiagnosticpro.dialogue_box.InfoDialogue;
import lcwu.fyp.obdcardiagnosticpro.dialogue_box.MeterDialogue;
import lcwu.fyp.obdcardiagnosticpro.director.Helpers;
import lcwu.fyp.obdcardiagnosticpro.model.BluetoothObject;

import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_OBD_CONNECTION_STATUS;
import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_READ_OBD_REAL_TIME_DATA;

public class Dashboard extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_ENABLE_BT = 1; // Unique request code
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private List<BluetoothObject> data;
    private ODBBluetoothAdapter adapter;
    private boolean isConnected;
    private Helpers helper;
    private BluetoothSocket socket = null;
    private ArrayList<String> deviceStrs = new ArrayList();
    private ArrayList<String> devices = new ArrayList();
    private String rpm, speed;
    private MenuItem item;
    private int count = 0;
    private ProgressDialog progressDialog;
    private TripRecord tripRecord;
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
                        session.setSpeed("Engine RPM: " + tripRecord.getEngineRpm() + "\n");
                        InfoDialogue rpmDialogue = new InfoDialogue(Dashboard.this, "CAR ENGINE RPM", Integer.parseInt(tripRecord.getEngineRpm()));
                        rpmDialogue.show();
                    } catch (Exception e) {
                        session.setSpeed("Engine RPM Exception: " + e.getMessage() + "\n");
                        helper.showError(Dashboard.this, "ERROR", e.getMessage());
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
                        session.setSpeed("Engine Temperature: " + tripRecord.getmEngineCoolantTemp() + "\n");
                        // Show Engine Temperature
                        InfoDialogue engineTempDialogue = new InfoDialogue(Dashboard.this, "CAR ENGINE TEMPERATURE", Integer.parseInt(tripRecord.getmEngineCoolantTemp()));
                        engineTempDialogue.show();
                    } catch (Exception e) {
                        session.setSpeed("Engine Temperature Exception: " + e.getMessage() + "\n");
                        helper.showError(Dashboard.this, "ERROR", e.getMessage());
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
                        session.setSpeed("Air Intake Temperature: " + tripRecord.getmAmbientAirTemp() + "\n");
                        // Show Air in take Temperature
                        InfoDialogue airInTakeTempDialouge = new InfoDialogue(Dashboard.this, "CAR AIR INTAKE TEMPERATURE", Integer.parseInt(tripRecord.getmAmbientAirTemp()));
                        airInTakeTempDialouge.show();
                    } catch (Exception e) {
                        session.setSpeed("Air Intake Temperature Exception: " + e.getMessage() + "\n");
                        helper.showError(Dashboard.this, "ERROR", e.getMessage());
                    }
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

            case R.id.testActivity:{
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
//                ObdConfiguration.setmObdCommands(Dashboard.this,null);
//                IntentFilter intentFilter = new IntentFilter();
//                intentFilter.addAction(ACTION_READ_OBD_REAL_TIME_DATA);
//                intentFilter.addAction(ACTION_OBD_CONNECTION_STATUS);
//                registerReceiver(mObdReaderReceiver,intentFilter);
//                try {
//                    socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
//                    socket.connect();
//                    if(socket.isConnected()){
//                        isConnected = true;
//                       helper.showSuccess(Dashboard.this,"SUCCESS", "Successfully Connected");
//                    }
//                    else{
//                        helper.showError(Dashboard.this,"ERROR", "Error Occur while connecting to the device");
//                    }
//
//                } catch (IOException e) {
//                   helper.showError(Dashboard.this,"ERROR", "Error Occur while connecting to the device: " + e.getMessage());
//                    e.printStackTrace();
//                }
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

//    private void readCarData(){
//        Session session = new Session(Dashboard.this);
////        try {
////            socket.connect();
////            if(socket.isConnected()){
////                isConnected = true;
////                helper.showSuccess(Dashboard.this,"SUCCESS", "Successfully Connected");
////            }
////            else{
////                helper.showError(Dashboard.this,"ERROR", "Error Occur while connecting to the device");
////            }
////
////        } catch (IOException e) {
////            helper.showError(Dashboard.this,"ERROR", "Error Occur while connecting to the device: " + e.getMessage());
////            e.printStackTrace();
////        }
//        if(isConnected){
//            try {
//                rpm = helper.getRPMData(socket);
//                session.setRPM("readCarData: " + rpm);
//                helper.showSuccess(Dashboard.this, "Success", "RPM and Speed Data read success");
//            } catch (Exception e) {
//                helper.showError(Dashboard.this,"ERROR","Something went wrong.\nPlease try again.\n" + e.getMessage());
//                session.setRPM("readCarData RPM ERROR: " + e.getMessage());
//            }
//            try {
//                speed = helper.getSpeedData(socket);
//                session.setSpeed("readCarData: " + speed);
//                helper.showSuccess(Dashboard.this, "Success", "RPM and Speed Data read success");
//            } catch (Exception e) {
//                helper.showError(Dashboard.this,"ERROR","Something went wrong.\nPlease try again.\n" + e.getMessage());
//                session.setSpeed("readCarData SPEED ERROR: " + e.getMessage());
//            }
//        }
//        else{
//            helper.showError(Dashboard.this,"ERROR","YOU ARE NOT CONNECTED TO DEVICE");
//            session.setRPM("readCarData RPM ERROR: Device not connected");
//            session.setSpeed("readCarData SPEED ERROR: Device not connected");
//        }
//        readCarData1();
//    }

//    private void readCarData1(){
//        Session session = new Session(Dashboard.this);
//        if(isConnected){
//            try {
//                rpm = helper.getRPMData(socket);
//                session.setRPM("readCarData1: " + rpm);
//                helper.showSuccess(Dashboard.this, "Success", "RPM and Speed Data read success");
//            } catch (Exception e) {
//                helper.showError(Dashboard.this,"ERROR","Something went wrong.\nPlease try again.\n" + e.getMessage());
//                session.setRPM("readCarData1RPM ERROR: " + e.getMessage());
//            }
//            try {
//                speed = helper.getSpeedData(socket);
//                session.setSpeed("readCarData1: " +speed);
//                helper.showSuccess(Dashboard.this, "Success", "RPM and Speed Data read success");
//            } catch (Exception e) {
//                helper.showError(Dashboard.this,"ERROR","Something went wrong.\nPlease try again.\n" + e.getMessage());
//                session.setSpeed("readCarData1 SPEED ERROR: " + e.getMessage());
//            }
//        }
//        else{
//            helper.showError(Dashboard.this,"ERROR","YOU ARE NOT CONNECTED TO DEVICE");
//            session.setRPM("readCarData1 RPM ERROR: Device not connected");
//            session.setSpeed("readCarData1 SPEED ERROR: Device not connected");
//
//        }
//    }

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
