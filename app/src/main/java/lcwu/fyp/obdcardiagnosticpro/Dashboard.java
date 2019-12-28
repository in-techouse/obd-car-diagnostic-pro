package lcwu.fyp.obdcardiagnosticpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lcwu.fyp.obdcardiagnosticpro.adapters.ODBBluetoothAdapter;
import lcwu.fyp.obdcardiagnosticpro.model.BluetoothObject;

public class Dashboard extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
   private Button connect,scan,cancel;
    private RecyclerView list;
    private SwipeRefreshLayout refreshLayout;
    private List<BluetoothObject> data;
    private ODBBluetoothAdapter adapter;
    private boolean isConnected;

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
    private void ConnectDevice(){
     if (isConnected){
         showNoDeviceConnectedError("ERROR","Already Connected");

     }
    }
    private void ScanDevice() {
        if (!isConnected ) {
            showNoDeviceConnectedError("ERROR","YOU ARE NOT CONNECTED TO DEVICE");



        }
    }
    private void CancelDevice(){
      if(!isConnected)
        {
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
