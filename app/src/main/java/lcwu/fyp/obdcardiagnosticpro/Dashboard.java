package lcwu.fyp.obdcardiagnosticpro;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lcwu.fyp.obdcardiagnosticpro.adapters.ODBBluetoothAdapter;
import lcwu.fyp.obdcardiagnosticpro.director.Helpers;
import lcwu.fyp.obdcardiagnosticpro.model.BluetoothObject;

public class Dashboard extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_ENABLE_BT = 1; // Unique request code
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Button connect,scan,cancel;
    private List<BluetoothObject> data;
    private ODBBluetoothAdapter adapter;
    private boolean isConnected;
    private Helpers helper;
    private BluetoothSocket socket = null;
    private ArrayList<String> deviceStrs = new ArrayList();
    private ArrayList<String> devices = new ArrayList();
    private String rpm,speed;
    private CardView dashboard,livedata,allsensor,card_rpm,card_speed,enginetemprature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        connect=findViewById(R.id.connect);
        scan=findViewById(R.id.scan);
        cancel=findViewById(R.id.cancel);
        dashboard=findViewById(R.id.dashboard);
        livedata=findViewById(R.id.live_data);
        allsensor=findViewById(R.id.all_sensor);
        card_rpm=findViewById(R.id.rpm);
        card_speed= findViewById(R.id.speed);
        enginetemprature=findViewById(R.id.engine_temperature);
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
    }

    private void getBluetoothDevices(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }
        for(BluetoothDevice bt : pairedDevices) {
            BluetoothObject object = new BluetoothObject();
            object.setAddress(bt.getAddress());
            object.setName(bt.getName());
            data.add(object);
        }
        adapter.setBluetoothList(data);
    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch(id){
            case R.id.connect:
            {
//                Intent intent = new Intent(Dashboard.this,TestActivity.class);
//                startActivity(intent);
                ConnectDevice();
                break;
            }

            case R.id.scan:
            {
                ScanDevice();
                break;
            }
            case R.id.cancel:
            {
                CancelDevice();
                break;
            }
            case R.id.dashboard:{
                Intent intent=new Intent(Dashboard.this,MainDashboard.class);
                startActivity(intent);
                break;
            }
            case R.id.all_sensor:{
                Intent intent=new Intent(Dashboard.this,AllSensors.class);
                startActivity(intent);
                break;
            }
            case R.id.rpm:{
                break;
            }
            case R.id.engine_temperature:{
                break;
            }
            case R.id.speed:{
                break;
            }
            case R.id.live_data:{
                Intent intent=new Intent(Dashboard.this,LiveData.class);
                startActivity(intent);
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
                helper.showError( Dashboard.this,"ERROR", "Bluetooth is not enabled.");
            }
        }
    }

    private void ConnectDevice(){
        if (isConnected){
            helper.showError(Dashboard.this,"ERROR","Already Connected");
            return;
        }
        getAllDevices();
    }

    private void getAllDevices(){
        devices.clear();
        deviceStrs.clear();
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice device : pairedDevices)
            {
                deviceStrs.add(device.getName() + "\n" + device.getAddress());
                devices.add(device.getAddress());
            }
        }

        // show list
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Dashboard.this);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice, deviceStrs.toArray(new String[deviceStrs.size()]));

        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();

                String deviceAddress = devices.get(which);
                // TODO save deviceAddress
                BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);
                try {
                    socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                    socket.connect();
                    if(socket.isConnected()){
                        isConnected = true;
                       helper.showSuccess(Dashboard.this,"SUCCESS", "Successfully Connected");
                    }
                    else{
                        helper.showError(Dashboard.this,"ERROR", "Error Occur while connecting to the device");
                    }

                } catch (IOException e) {
                   helper.showError(Dashboard.this,"ERROR", "Error Occur while connecting to the device: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        alertDialog.setTitle("Choose Bluetooth device");
        alertDialog.show();
    }

    private void ScanDevice() {
        if (!isConnected) {
            helper.showError(Dashboard.this,"ERROR","YOU ARE NOT CONNECTED TO DEVICE");
            return;
        }
        readCarData();
//        InfoDialogue dialogue = new InfoDialogue(Dashboard.this);
//        dialogue.show();
    }
    private void CancelDevice(){
        if(!isConnected){
            helper.showError(Dashboard.this,"ERROR","YOU ARE NOT CONNECTED TO DEVICE");
            return;
        }
    }

    private void readCarData(){
        Session session = new Session(Dashboard.this);
//        try {
//            socket.connect();
//            if(socket.isConnected()){
//                isConnected = true;
//                helper.showSuccess(Dashboard.this,"SUCCESS", "Successfully Connected");
//            }
//            else{
//                helper.showError(Dashboard.this,"ERROR", "Error Occur while connecting to the device");
//            }
//
//        } catch (IOException e) {
//            helper.showError(Dashboard.this,"ERROR", "Error Occur while connecting to the device: " + e.getMessage());
//            e.printStackTrace();
//        }
        if(isConnected){
            try {
                rpm = helper.getRPMData(socket);
                session.setRPM("readCarData: " + rpm);
                helper.showSuccess(Dashboard.this, "Success", "RPM and Speed Data read success");
            } catch (Exception e) {
                helper.showError(Dashboard.this,"ERROR","Something went wrong.\nPlease try again.\n" + e.getMessage());
                session.setRPM("readCarData RPM ERROR: " + e.getMessage());
            }
            try {
                speed = helper.getSpeedData(socket);
                session.setSpeed("readCarData: " + speed);
                helper.showSuccess(Dashboard.this, "Success", "RPM and Speed Data read success");
            } catch (Exception e) {
                helper.showError(Dashboard.this,"ERROR","Something went wrong.\nPlease try again.\n" + e.getMessage());
                session.setSpeed("readCarData SPEED ERROR: " + e.getMessage());
            }
        }
        else{
            helper.showError(Dashboard.this,"ERROR","YOU ARE NOT CONNECTED TO DEVICE");
            session.setRPM("readCarData RPM ERROR: Device not connected");
            session.setSpeed("readCarData SPEED ERROR: Device not connected");
        }
        readCarData1();
    }

    private void readCarData1(){
        Session session = new Session(Dashboard.this);
        if(isConnected){
            try {
                rpm = helper.getRPMData(socket);
                session.setRPM("readCarData1: " + rpm);
                helper.showSuccess(Dashboard.this, "Success", "RPM and Speed Data read success");
            } catch (Exception e) {
                helper.showError(Dashboard.this,"ERROR","Something went wrong.\nPlease try again.\n" + e.getMessage());
                session.setRPM("readCarData1RPM ERROR: " + e.getMessage());
            }
            try {
                speed = helper.getSpeedData(socket);
                session.setSpeed("readCarData1: " +speed);
                helper.showSuccess(Dashboard.this, "Success", "RPM and Speed Data read success");
            } catch (Exception e) {
                helper.showError(Dashboard.this,"ERROR","Something went wrong.\nPlease try again.\n" + e.getMessage());
                session.setSpeed("readCarData1 SPEED ERROR: " + e.getMessage());
            }
        }
        else{
            helper.showError(Dashboard.this,"ERROR","YOU ARE NOT CONNECTED TO DEVICE");
            session.setRPM("readCarData1 RPM ERROR: Device not connected");
            session.setSpeed("readCarData1 SPEED ERROR: Device not connected");

        }
    }

}
