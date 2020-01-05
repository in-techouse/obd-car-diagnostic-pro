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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import lcwu.fyp.obdcardiagnosticpro.dialogue_box.InfoDialogue;
import lcwu.fyp.obdcardiagnosticpro.director.Helpers;
import lcwu.fyp.obdcardiagnosticpro.model.BluetoothObject;

public class Dashboard extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_ENABLE_BT = 1; // Unique request code
    private Button connect,scan,cancel;
    private List<BluetoothObject> data;
    private ODBBluetoothAdapter adapter;
    private boolean isConnected;
    private Helpers helper;
    private BluetoothSocket socket = null;
    private ArrayList<String> deviceStrs = new ArrayList();
    private ArrayList<String> devices = new ArrayList();
    private String rpm,speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        connect=findViewById(R.id.connect);
        scan=findViewById(R.id.scan);
        cancel=findViewById(R.id.cancel);
        isConnected = false;
        helper = new Helpers();

//           list = findViewById(R.id.list);

//        list.setHasFixedSize(true);
//        list.setLayoutManager(new LinearLayoutManager(this));
        data = new ArrayList<>();
        adapter = new ODBBluetoothAdapter(Dashboard.this);
//        refreshLayout.setOnRefreshListener(this);
//        list.setAdapter(adapter);
        getBluetoothDevices();
        connect.setOnClickListener(this);
        scan.setOnClickListener(this);
        cancel.setOnClickListener(this);
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
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                try {
                    socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                    socket.connect();
                    if(socket.isConnected()){
                        isConnected = true;
                       helper.showSuccess(Dashboard.this,"SUCCESS", "Successfully Connected");
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
        if (!isConnected ) {
            helper.showError(Dashboard.this,"ERROR","YOU ARE NOT CONNECTED TO DEVICE");
            return;
        }
        readCarData();
        InfoDialogue dialogue = new InfoDialogue(Dashboard.this);
        dialogue.show();
    }
    private void CancelDevice(){
        if(!isConnected){
            helper.showError(Dashboard.this,"ERROR","YOU ARE NOT CONNECTED TO DEVICE");
            return;
        }
    }

    private void readCarData(){
        if(isConnected && socket.isConnected()){
            try {
                rpm = helper.getRPMdata(socket);
                speed = helper.getSpeeddata(socket);
                Session session=new Session(Dashboard.this);
                session.setRPM(rpm);
                session.setSpeed(speed);
                helper.showSuccess(Dashboard.this, "Success", "RPM and Speed Data read success");
            } catch (Exception e) {
                helper.showError(Dashboard.this,"ERROR","Something went wrong.\nPlease try again.\n" + e.getMessage());
            }
        }
        else{
            helper.showError(Dashboard.this,"ERROR","YOU ARE NOT CONNECTED TO DEVICE");
        }
    }

}
