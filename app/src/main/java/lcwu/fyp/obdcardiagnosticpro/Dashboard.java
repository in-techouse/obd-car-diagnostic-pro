package lcwu.fyp.obdcardiagnosticpro;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lcwu.fyp.obdcardiagnosticpro.adapters.ODBBluetoothAdapter;
import lcwu.fyp.obdcardiagnosticpro.model.BluetoothObject;

public class Dashboard extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private static final int REQUEST_ENABLE_BT = 1; // Unique request code
    private Button connect,scan,cancel;
    private RecyclerView list;
    private SwipeRefreshLayout refreshLayout;
    private List<BluetoothObject> data;
    private ODBBluetoothAdapter adapter;
    private boolean isConnected;
    private BluetoothSocket socket = null;

    private ArrayList<String> deviceStrs = new ArrayList();
    private ArrayList<String> devices = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        connect=findViewById(R.id.connect);
        scan=findViewById(R.id.scan);
        cancel=findViewById(R.id.cancel);
        isConnected = false;

        list = findViewById(R.id.list);
        refreshLayout = findViewById(R.id.refreshLayout);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));
        data = new ArrayList<>();
        adapter = new ODBBluetoothAdapter(Dashboard.this);
        refreshLayout.setOnRefreshListener(this);
        list.setAdapter(adapter);
        getBluetoothDevices();
        connect.setOnClickListener(this);
        scan.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }


    @Override
    public void onRefresh() {
        data.clear();
        adapter.setBluetoothList(data);
        getBluetoothDevices();
        refreshLayout.setRefreshing(false);
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

        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                ConnectDevice();
            } else if (resultCode == RESULT_CANCELED) {
                // Bluetooth was not enabled
                showNoDeviceConnectedError("ERROR", "Bluetooth is not enabled.");
            }
        }
    }

    private void ConnectDevice(){
        if (isConnected){
            showNoDeviceConnectedError("ERROR","Already Connected");
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
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                try {
                    socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                    socket.connect();
                    if(socket.isConnected()){
                        isConnected = true;
                        showNoDeviceConnectedError("SUCCESS", "Successfully Connected");
                    }

                } catch (IOException e) {
                    showNoDeviceConnectedError("ERROR", "Error Occur while connecting to the device: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        alertDialog.setTitle("Choose Bluetooth device");
        alertDialog.show();
    }

    private void ScanDevice() {
        if (!isConnected ) {
            showNoDeviceConnectedError("ERROR","YOU ARE NOT CONNECTED TO DEVICE");
            return;
        }
        readCarData();
    }
    private void CancelDevice(){
        if(!isConnected){
            showNoDeviceConnectedError("ERROR","YOU ARE NOT CONNECTED TO DEVICE");
            return;
        }
    }

    private void readCarData(){
        if(isConnected && socket.isConnected()){
            try {
                new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                new TimeoutCommand(1).run(socket.getInputStream(), socket.getOutputStream());

                new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
                RPMCommand rpmCommand = new RPMCommand();
                SpeedCommand speedCommand = new SpeedCommand();
                String str = "";
                while (!Thread.currentThread().isInterrupted()) {
                    rpmCommand.run(socket.getInputStream(),socket.getOutputStream());
                    speedCommand.run(socket.getInputStream(),socket.getOutputStream());
                    str = str + "\nRPM: " + rpmCommand.getFormattedResult();
                    str = str + "\nSpeed: " + speedCommand.getFormattedResult();
                    Log.e("OBD","RPM:" + rpmCommand.getFormattedResult());
                    Log.e("OBD","Speed:" + speedCommand.getFormattedResult());
                }
            } catch (Exception e) {
                showNoDeviceConnectedError("ERROR","Something went wrong.\nPlease try again.\n" + e.getMessage());
            }
        }
        else{
            showNoDeviceConnectedError("ERROR","YOU ARE NOT CONNECTED TO DEVICE");
        }
    }

    private void showNoDeviceConnectedError(String title,String Message){
        new FancyAlertDialog.Builder(Dashboard.this)
                .setTitle(title)
                .setBackgroundColor(Color.parseColor("#303F9F"))  //Don't pass R.color.colorvalue
                .setMessage(Message)
                .setNegativeBtnText("Cancel")
                .setPositiveBtnBackground(Color.parseColor("#FF4081"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("Okay")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  //Don't pass R.color.colorvalue
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .setIcon(R.drawable.ic_star_border_black_24dp, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                    }
                })
                .build();
    }
}
