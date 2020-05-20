package lcwu.fyp.obdcardiagnosticpro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sohrab.obd.reader.application.ObdPreferences;
import com.sohrab.obd.reader.service.ObdReaderService;
import com.sohrab.obd.reader.trip.TripRecord;

import java.util.Date;
import java.util.HashMap;

import lcwu.fyp.obdcardiagnosticpro.director.Helpers;

import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_OBD_CONNECTION_STATUS;
import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_READ_OBD_REAL_TIME_DATA;

public class DiagnosticTroubleCodes extends AppCompatActivity {

    private LinearLayout main, connecting;
    private TextView faultCodes, pendingCodes, permanentCodes;
    private MenuItem item;
    private Helpers helpers;
    private Session session;
    private HashMap<String, String> hashMap = new HashMap<>();

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
                TripRecord tripRecord = TripRecord.getTripRecode(DiagnosticTroubleCodes.this);
                item.setTitle("OBD CONNECTED");
                connecting.setVisibility(View.GONE);
                main.setVisibility(View.VISIBLE);

                Date d = new Date();
                String result = "\nTrouble Codes: " + d.toString();
                try {
                    if (tripRecord != null) {
                        result = result + "\nFault Codes: " + tripRecord.getmFaultCodes() + "\nPending Trouble Codes: " + tripRecord.getmPendingTroubleCode() + "\nPermanent Trouble Code: " + tripRecord.getmPermanentTroubleCode();
                        session.setRPM(result);
                        faultCodes.setText(tripRecord.getmFaultCodes());
                        pendingCodes.setText(tripRecord.getmPendingTroubleCode());
                        permanentCodes.setText(tripRecord.getmPermanentTroubleCode());
                    } else {
                        result = result + " Trip Record is null";
                        session.setRPM(result);
                        helpers.showError(DiagnosticTroubleCodes.this, "ERROR!", "Something went wrong.\nPlease try again later.");
                    }
                } catch (Exception e) {
                    result = result + " Exception: " + e.getMessage();
                    session.setRPM(result);
                }
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostic_trouble_codes);

        main = findViewById(R.id.main);
        connecting = findViewById(R.id.connecting);
        connecting.setVisibility(View.VISIBLE);
        main.setVisibility(View.GONE);

        faultCodes = findViewById(R.id.faultCodes);
        pendingCodes = findViewById(R.id.pendingCodes);
        permanentCodes = findViewById(R.id.permamentCodes);

        helpers = new Helpers();
        session = new Session(getApplicationContext());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_READ_OBD_REAL_TIME_DATA);
        intentFilter.addAction(ACTION_OBD_CONNECTION_STATUS);
        registerReceiver(mObdReaderReceiver, intentFilter);
        startService(new Intent(DiagnosticTroubleCodes.this, ObdReaderService.class));

        hashMap.put("P0001", "Fuel Volume Regulator Control Circuit / Open");
        hashMap.put("P0002", "Fuel Volume Regulator Control Circuit Range/Performance");
        hashMap.put("P0003", "Fuel Volume Regulator Control Circuit Low");
        hashMap.put("P0004", "Fuel Volume Regulator Control Circuit High");
        hashMap.put("P0005", "Fuel Shutoff Valve Control Circuit / Open");
        hashMap.put("P0006", "Fuel Shutoff Valve Control Circuit Low");
        hashMap.put("P0007", "Fuel Shutoff Valve Control Circuit High");
        hashMap.put("P0008", "Engine Position System Performance - Bank 2");
        hashMap.put("P0009", "Engine Position System Performance - Bank 2");
        hashMap.put("P0010", "Intake Camshaft Position Actuator Circuit / Open (Bank 2)");
        hashMap.put("P0011", "Intake Camshaft Position Timing - Over-Advanced (Bank 2)");
        hashMap.put("P0012", "Intake Camshaft Position Timing - Over-Retarded (Bank 2)");
        hashMap.put("P0013", "Exhaust Camshaft Position Actuator Circuit / Open (Bank 2)");
        hashMap.put("P0014", "Exhaust Camshaft Position Timing - Over-Advanced (Bank 2)");
        hashMap.put("P0015", "Exhaust Camshaft Position Timing - Over-Retarded (Bank 2)");
        hashMap.put("P0016", "Crankshaft Position Camshaft Position Correlation Bank 2 Sensor A");
        hashMap.put("P0017", "Crankshaft Position Camshaft Position Correlation Bank 2 Sensor B");
        hashMap.put("P0018", "Crankshaft Position Camshaft Position Correlation Bank 2 Sensor A");
        hashMap.put("P0019", "Crankshaft Position Camshaft Position Correlation Bank 2 Sensor B");
        hashMap.put("P0020", "Intake Camshaft Position Actuator Circuit / Open (Bank 2)");
        hashMap.put("P0021", "Intake Camshaft Position Timing - Over-Advanced (Bank 2)");
        hashMap.put("P0022", "Intake Camshaft Position Timing - Over-Retarded (Bank 2)");
        hashMap.put("P0023", "Exhaust Camshaft Position Actuator Circuit / Open (Bank 2)");
        hashMap.put("P0024", "Exhaust Camshaft Position Timing - Over-Advanced (Bank 2)");
        hashMap.put("P0025", "Exhaust Camshaft Position Timing - Over-Retarded (Bank 2)");
        hashMap.put("P0026", "Intake Valve Control Solenoid Circuit Range/Performance (Bank 2)");
        hashMap.put("P0027", "Exhaust Valve Control Solenoid Circuit Range/Performance (Bank 2)");
        hashMap.put("P0028", "Intake Valve Control Solenoid Circuit Range/Performance (Bank 2)");
        hashMap.put("P0029", "Exhaust Valve Control Solenoid Circuit Range/Performance (Bank 2)");
        hashMap.put("P0030", "Heated Oxygen Sensor (H02S) Heater Control Circuit Bank 2 Sensor 1");
        hashMap.put("P0031", "Heated Oxygen Sensor (HO2S) Heater Circuit Low Voltage Bank 2 Sensor 1");
        hashMap.put("P0032", "Heated Oxygen Sensor (HO2S) Heater Circuit High Voltage Bank 2 Sensor 1");
        hashMap.put("P0033", "Turbo/Super Charger Bypass Valve Control Circuit / Open");
        hashMap.put("P0034", "Turbo/Super Charger Bypass Valve Control Circuit Low");
        hashMap.put("P0035", "Turbo/Super Charger Bypass Valve Control Circuit High");
        hashMap.put("P0036", "Heated Oxygen Sensor (HO2S) Heater Control Circuit Bank 2 Sensor 2");
        hashMap.put("P0037", "Heated Oxygen Sensor (HO2S) Heater Circuit Low Voltage Bank 2 Sensor 2");
        hashMap.put("P0038", "Heated Oxygen Sensor (HO2S) Heater Circuit High Voltage Bank 2 Sensor 2");
        hashMap.put("P0039", "Turbo/Super Charger Bypass Valve Control Circuit Range/Performance");
        hashMap.put("P0040", "Oxygen Sensor Signals Swapped Bank 2 Sensor 1 / Bank 2 Sensor 1");
        hashMap.put("P0041", "Oxygen Sensor Signals Swapped Bank 2 Sensor 2 / Bank 2 Sensor 2");
        hashMap.put("P0042", "HO2S Heater Control Circuit (Bank 2, Sensor 3)");
        hashMap.put("P0043", "HO2S Heater Control Circuit Low (Bank 2, Sensor 3)");
        hashMap.put("P0044", "HO2S Heater Control Circuit High (Bank 2, Sensor 3)");
        hashMap.put("P0045", "Turbo/Super Charger Boost Control Solenoid Circuit / Open");
        hashMap.put("P0046", "Turbo/Super Charger Boost Control Solenoid Circuit Range/Performance");
        hashMap.put("P0047", "Turbo/Super Charger Boost Control Solenoid Circuit Low");
        hashMap.put("P0048", "Turbo/Super Charger Boost Control Solenoid Circuit High");
        hashMap.put("P0049", "Turbo/Super Charger Turbine Overspeed");
        hashMap.put("P0050", "Heated Oxygen Sensor (HO2S) Heater Circuit Bank 2 Sensor 1");
        hashMap.put("P0051", "Heated Oxygen Sensor (HO2S) Heater Circuit Low Voltage Bank 2 Sensor 1");
        hashMap.put("P0052", "Heated Oxygen Sensor (HO2S) Heater Circuit High Voltage Bank 2 Sensor 1");
        hashMap.put("P0053", "HO2S Heater Resistance Bank 2 Sensor 1 (PCM)");
        hashMap.put("P0054", "HO2S Heater Resistance Bank 2 Sensor 2 (PCM)");
        hashMap.put("P0055", "HO2S Heater Resistance Bank 2 Sensor 3 (PCM)");
        hashMap.put("P0056", "Heated Oxygen Sensor (HO2S) Heater Circuit Bank 2 Sensor 2");
        hashMap.put("P0057", "Heated Oxygen Sensor (HO2S) Heater Circuit Low Voltage Bank 2 Sensor 2");
        hashMap.put("P0058", "Heated Oxygen Sensor (HO2S) Heater Circuit High Voltage Bank 2 Sensor 2");
        hashMap.put("P0059", "HO2S Heater Resistance (Bank 2, Sensor 1)");
        hashMap.put("P0060", "HO2S Heater Resistance (Bank 2, Sensor 2)");
        hashMap.put("P0061", "HO2S Heater Resistance (Bank 2, Sensor 3)");
        hashMap.put("P0062", "HO2S Heater Control Circuit (Bank 2, Sensor 3)");
        hashMap.put("P0063", "HO2S Heater Control Circuit Low (Bank 2, Sensor 3)");
        hashMap.put("P0064", "HO2S Heater Control Circuit High (Bank 2, Sensor 3)");
        hashMap.put("P0065", "Air Assisted Injector Control Range/Performance");
        hashMap.put("P0066", "Air Assisted Injector Control Circuit or Circuit Low");
        hashMap.put("P0067", "Air Assisted Injector Control Circuit or Circuit High");
        hashMap.put("P0068", "MAP / MAF - Throttle Position Correlation");
        hashMap.put("P0069", "MAP - Barometric Pressure Correlation");
        hashMap.put("P0070", "Ambient Air Temperature Sensor Circuit");
        hashMap.put("P0071", "Ambient Air Temperature Sensor Range/Performance");
        hashMap.put("P0072", "Ambient Air Temperature Sensor Circuit Low Input");
        hashMap.put("P0073", "Ambient Air Temperature Sensor Circuit High Input");
        hashMap.put("P0074", "Ambient Air Temperature Sensor Circuit Intermittent/Erratic");
        hashMap.put("P0075", "Intake Valve Control Circuit (Bank 2)");
        hashMap.put("P0076", "Intake Valve Control Circuit Low (Bank 2)");
        hashMap.put("P0077", "Intake Valve Control Circuit High (Bank 2)");
        hashMap.put("P0078", "Exhaust Valve Control Circuit (Bank 2)");
        hashMap.put("P0079", "Exhaust Valve Control Circuit Low (Bank 2)");
        hashMap.put("P0080", "Exhaust Valve Control Circuit High (Bank 2)");
        hashMap.put("P0081", "Intake Valve Control Circuit (Bank 2)");
        hashMap.put("P0082", "Intake Valve Control Circuit Low (Bank 2)");
        hashMap.put("P0083", "Intake Valve Control Circuit High (Bank 2)");
        hashMap.put("P0084", "Exhaust Valve Control Circuit (Bank 2)");
        hashMap.put("P0085", "Exhaust Valve Control Circuit Low (Bank 2)");
        hashMap.put("P0086", "Exhaust Valve Control Circuit High (Bank 2)");
        hashMap.put("P0087", "Fuel Rail/System Pressure - Too Low");
        hashMap.put("P0088", "Fuel Rail/System Pressure - Too High");
        hashMap.put("P0089", "Fuel Pressure Regulator Performance");
        hashMap.put("P0090", "Fuel Pressure Regulator Control Circuit");
        hashMap.put("P0091", "Fuel Pressure Regulator Control Circuit Low");
        hashMap.put("P0092", "Fuel Pressure Regulator Control Circuit High");
        hashMap.put("P0093", "Fuel System Leak Detected - Large Leak");
        hashMap.put("P0094", "Fuel System Leak Detected - Small Leak");
        hashMap.put("P0095", "Intake Air Temperature Sensor 2 Circuit");
        hashMap.put("P0096", "Intake Air Temperature Sensor 2 Circuit Range/Performance");
        hashMap.put("P0097", "Intake Air Temperature Sensor 2 Circuit Low Input");
        hashMap.put("P0098", "Intake Air Temperature Sensor 2 Circuit High Input");
        hashMap.put("P0099", "Intake Air Temperature Sensor 2 Circuit Intermittent/Erratic");
        hashMap.put("P0100", "Mass or Volume Air flow Circuit Malfunction");
        hashMap.put("P0101", "");
        hashMap.put("P0102", "");
        hashMap.put("P0103", "");
        hashMap.put("P0104", "");
        hashMap.put("P0105", "");
        hashMap.put("P0106", "");
        hashMap.put("P0107", "");
        hashMap.put("P0108", "");
        hashMap.put("P0109", "");
        hashMap.put("P0110", "");
        hashMap.put("P0111", "");
        hashMap.put("P0112", "");
        hashMap.put("P0113", "");
        hashMap.put("P0114", "");
        hashMap.put("P0115", "");
        hashMap.put("P0116", "");
        hashMap.put("P0117", "");
        hashMap.put("P0118", "");
        hashMap.put("P0119", "");
        hashMap.put("P0120", "");
        hashMap.put("P0121", "");
        hashMap.put("P0122", "");
        hashMap.put("P0123", "");
        hashMap.put("P0124", "");
        hashMap.put("P0125", "");
        hashMap.put("P0126", "");
        hashMap.put("P0127", "");
        hashMap.put("P0128", "");
        hashMap.put("P0129", "");
        hashMap.put("P0130", "");
        hashMap.put("P0131", "");
        hashMap.put("P0132", "");
        hashMap.put("P0133", "");
        hashMap.put("P0134", "");
        hashMap.put("P0135", "");
        hashMap.put("P0136", "");
        hashMap.put("P0137", "");
        hashMap.put("P0138", "");
        hashMap.put("P0139", "");
        hashMap.put("P0140", "");
        hashMap.put("P0141", "");
        hashMap.put("P0142", "");
        hashMap.put("P0143", "");
        hashMap.put("P0144", "");
        hashMap.put("P0145", "");
        hashMap.put("P0146", "");
        hashMap.put("P0147", "");
        hashMap.put("P0148", "");
        hashMap.put("P0149", "");
        hashMap.put("P0150", "");
        hashMap.put("P0151", "");
        hashMap.put("P0152", "");
        hashMap.put("P0153", "");
        hashMap.put("P0154", "");
        hashMap.put("P0155", "");
        hashMap.put("P0156", "");
        hashMap.put("P0157", "");
        hashMap.put("P0158", "");
        hashMap.put("P0159", "");
        hashMap.put("P0160", "");
        hashMap.put("P0161", "");
        hashMap.put("P0162", "");
        hashMap.put("P0163", "");
        hashMap.put("P0164", "");
        hashMap.put("P0165", "");
        hashMap.put("P0166", "");
        hashMap.put("P0167", "");
        hashMap.put("P0168", "");
        hashMap.put("P0169", "");
        hashMap.put("P0170", "");
        hashMap.put("P0171", "");
        hashMap.put("P0172", "");
        hashMap.put("P0173", "");
        hashMap.put("P0174", "");
        hashMap.put("P0175", "");
        hashMap.put("P0176", "");
        hashMap.put("P0177", "");
        hashMap.put("P0178", "");
        hashMap.put("P0179", "");
        hashMap.put("P0180", "");
        hashMap.put("P0181", "");
        hashMap.put("P0182", "");
        hashMap.put("P0183", "");
        hashMap.put("P0184", "");
        hashMap.put("P0185", "");
        hashMap.put("P0186", "");
        hashMap.put("P0187", "");
        hashMap.put("P0188", "");
        hashMap.put("P0189", "");
        hashMap.put("P0190", "");
        hashMap.put("P0191", "");
        hashMap.put("P0192", "");
        hashMap.put("P0193", "");
        hashMap.put("P0194", "");
        hashMap.put("P0195", "");
        hashMap.put("P0196", "");
        hashMap.put("P0197", "");
        hashMap.put("P0198", "");
        hashMap.put("P0199", "");
        hashMap.put("P0200", "");
        hashMap.put("P0201", "");
        hashMap.put("P0202", "");
        hashMap.put("P0203", "");
        hashMap.put("P0204", "");
        hashMap.put("P0205", "");
        hashMap.put("P0206", "");
        hashMap.put("P0207", "");
        hashMap.put("P0208", "");
        hashMap.put("P0209", "");
        hashMap.put("P0210", "");
        hashMap.put("P0211", "");
        hashMap.put("P0212", "");
        hashMap.put("P0213", "");
        hashMap.put("P0214", "");
        hashMap.put("P0215", "");
        hashMap.put("P0216", "");
        hashMap.put("P0217", "");
        hashMap.put("P0218", "");
        hashMap.put("P0219", "");
        hashMap.put("P0220", "");
        hashMap.put("P0221", "");
        hashMap.put("P0222", "");
        hashMap.put("P0223", "");
        hashMap.put("P0224", "");
        hashMap.put("P0225", "");
        hashMap.put("P0226", "");
        hashMap.put("P0227", "");
        hashMap.put("P0228", "");
        hashMap.put("P0229", "");
        hashMap.put("P0230", "");
        hashMap.put("P0231", "");
        hashMap.put("P0232", "");
        hashMap.put("P0233", "");
        hashMap.put("P0234", "");
        hashMap.put("P0235", "");
        hashMap.put("P0236", "");
        hashMap.put("P0237", "");
        hashMap.put("P0238", "");
        hashMap.put("P0239", "");
        hashMap.put("P0240", "");
        hashMap.put("P0241", "");
        hashMap.put("P0242", "");
        hashMap.put("P0243", "");
        hashMap.put("P0244", "");
        hashMap.put("P0245", "");
        hashMap.put("P0246", "");
        hashMap.put("P0247", "");
        hashMap.put("P0248", "");
        hashMap.put("P0249", "");
        hashMap.put("P0250", "");
        hashMap.put("P0251", "");
        hashMap.put("P0252", "");
        hashMap.put("P0253", "");
        hashMap.put("P0254", "");
        hashMap.put("P0255", "");
        hashMap.put("P0256", "");
        hashMap.put("P0257", "");
        hashMap.put("P0258", "");
        hashMap.put("P0259", "");
        hashMap.put("P0260", "");
        hashMap.put("P0261", "");
        hashMap.put("P0262", "");
        hashMap.put("P0263", "");
        hashMap.put("P0264", "");
        hashMap.put("P0265", "");
        hashMap.put("P0266", "");
        hashMap.put("P0267", "");
        hashMap.put("P0268", "");
        hashMap.put("P0269", "");
        hashMap.put("P0270", "");
        hashMap.put("P0271", "");
        hashMap.put("P0272", "");
        hashMap.put("P0273", "");
        hashMap.put("P0274", "");
        hashMap.put("P0275", "");
        hashMap.put("P0276", "");
        hashMap.put("P0277", "");
        hashMap.put("P0278", "");
        hashMap.put("P0279", "");
        hashMap.put("P0280", "");
        hashMap.put("P0281", "");
        hashMap.put("P0282", "");
        hashMap.put("P0283", "");
        hashMap.put("P0284", "");
        hashMap.put("P0285", "");
        hashMap.put("P0286", "");
        hashMap.put("P0287", "");
        hashMap.put("P0288", "");
        hashMap.put("P0289", "");
        hashMap.put("P0290", "");
        hashMap.put("P0291", "");
        hashMap.put("P0292", "");
        hashMap.put("P0293", "");
        hashMap.put("P0294", "");
        hashMap.put("P0295", "");
        hashMap.put("P0296", "");
        hashMap.put("P0297", "");
        hashMap.put("P0298", "");
        hashMap.put("P0299", "");
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
