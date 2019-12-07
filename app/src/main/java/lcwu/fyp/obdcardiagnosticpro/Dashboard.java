package lcwu.fyp.obdcardiagnosticpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Dashboard extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ListView listView;
    private SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        listView = findViewById(R.id.listView);
        refreshLayout = findViewById(R.id.refreshLayout);

        refreshLayout.setOnRefreshListener(this);

        getBluetoothDevices();
    }


    @Override
    public void onRefresh() {
        getBluetoothDevices();
        refreshLayout.setRefreshing(false);
    }

    private void getBluetoothDevices(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        List<String> s = new ArrayList<>();
        for(BluetoothDevice bt : pairedDevices) {
            s.add(bt.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.bluetooth_item, s);
        listView.setAdapter(adapter);
    }
}
