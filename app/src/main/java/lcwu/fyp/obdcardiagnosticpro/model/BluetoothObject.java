package lcwu.fyp.obdcardiagnosticpro.model;

import android.widget.TextView;

import java.io.Serializable;

public class BluetoothObject implements Serializable {
    private String name, address;

    public BluetoothObject() {
    }

    public BluetoothObject(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
