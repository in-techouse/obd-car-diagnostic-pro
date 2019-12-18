package lcwu.fyp.obdcardiagnosticpro.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import lcwu.fyp.obdcardiagnosticpro.R;
import lcwu.fyp.obdcardiagnosticpro.model.BluetoothObject;

public class ODBBluetoothAdapter extends RecyclerView.Adapter<ODBBluetoothAdapter.BluetoothHolder> {

    private List<BluetoothObject> bluetoothList;
    private Context context;

    public ODBBluetoothAdapter(Context c) {
        bluetoothList = new ArrayList<>();
        context = c;
    }

    @NonNull
    @Override
    public BluetoothHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bluetooth_item, parent, false);
        return new BluetoothHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothHolder holder, int position) {
        BluetoothObject object = bluetoothList.get(position);
        holder.bluetoothName.setText(object.getName());
        holder.bluetoothAddress.setText(object.getAddress());
    }

    @Override
    public int getItemCount() {
        return bluetoothList.size();
    }


    public void setBluetoothList(List<BluetoothObject> bluetoothList) {
        this.bluetoothList = bluetoothList;
        notifyDataSetChanged();
    }

    class BluetoothHolder extends RecyclerView.ViewHolder{
        TextView bluetoothName, bluetoothAddress;
        public BluetoothHolder(@NonNull View itemView) {
            super(itemView);
            bluetoothName = itemView.findViewById(R.id.bluetoothName);
            bluetoothAddress = itemView.findViewById(R.id.bluetoothAddress);
        }
    }
}
