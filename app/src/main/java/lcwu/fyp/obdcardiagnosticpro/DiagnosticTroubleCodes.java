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
import java.util.Map;

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
                        String tripFaultCodes = tripRecord.getmFaultCodes();
                        String tripPendingCodes = tripRecord.getmPendingTroubleCode();
                        String tripPermanentCodes = tripRecord.getmPermanentTroubleCode();
                        String fCodes = "";
                        String pCodes = "";
                        String perCodes = "";
                        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                            String key = entry.getKey().toLowerCase();
                            String value = entry.getValue();
                            if (tripFaultCodes != null && tripFaultCodes.toLowerCase().contains(key)) {
                                fCodes = fCodes + "" + key.toUpperCase() + "\n" + value + "\n";
                            }
                            if (tripPendingCodes != null && tripPendingCodes.toLowerCase().contains(key)) {
                                pCodes = pCodes + "" + key.toUpperCase() + "\n" + value + "\n";
                            }
                            if (tripPermanentCodes != null && tripPermanentCodes.toLowerCase().contains(key)) {
                                perCodes = perCodes + "" + key.toUpperCase() + "\n" + value + "\n";
                            }
                        }
                        faultCodes.setText(fCodes);
                        pendingCodes.setText(pCodes);
                        permanentCodes.setText(perCodes);
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
        hashMap.put("P0101", "Mass or Volume Air flow Circuit Range/Performance Problem");
        hashMap.put("P0102", "Mass or Volume Air Flow Circuit low Input");
        hashMap.put("P0103", "Mass or Volume Air flow Circuit High Input");
        hashMap.put("P0104", "Mass or Volume Air flow Circuit Intermittent");
        hashMap.put("P0105", "Manifold Absolute Pressure/Barometric Pressure Circuit Malfunction");
        hashMap.put("P0106", "Manifold Absolute Pressure/Barometric Pressure Circuit Range/Performance Problem");
        hashMap.put("P0107", "Manifold Absolute Pressure/Barometric Pressure Circuit Low Input");
        hashMap.put("P0108", "Manifold Absolute Pressure/Barometric Pressure Circuit High Input");
        hashMap.put("P0109", "Manifold Absolute Pressure/Barometric Pressure Circuit Intermittent");
        hashMap.put("P0110", "Intake Air Temperature Circuit Malfunction");
        hashMap.put("P0111", "Intake Air Temperature Circuit Range/Performance Problem");
        hashMap.put("P0112", "Intake Air Temperature Circuit Low Input");
        hashMap.put("P0113", "Intake Air Temperature Circuit High Input");
        hashMap.put("P0114", "Intake Air Temperature Circuit Intermittent");
        hashMap.put("P0115", "Engine Coolant Temperature Circuit Malfunction");
        hashMap.put("P0116", "Engine Coolant Temperature Circuit Range/Performance Problem");
        hashMap.put("P0117", "Engine Coolant Temperature Circuit Low Input");
        hashMap.put("P0118", "Engine Coolant Temperature Circuit High Input");
        hashMap.put("P0119", "Engine Coolant Temperature Circuit Intermittent");
        hashMap.put("P0120", "Throttle Pedal Position Sensor/Switch A Circuit Malfunction");
        hashMap.put("P0121", "Throttle/Pedal Position Sensor/Switch A Circuit Range/Performance Problem");
        hashMap.put("P0122", "Throttle/Pedal Position Sensor/Switch A Circuit Low Input");
        hashMap.put("P0123", "Throttle/Pedal Position Sensor/Switch A Circuit High Input");
        hashMap.put("P0124", "Throttle/Pedal Position Sensor/Switch A Circuit Intermittent");
        hashMap.put("P0125", "Insufficient Coolant Temperature for Closed Loop Fuel Control");
        hashMap.put("P0126", "Insufficient Coolant Temperature for Stable Operation");
        hashMap.put("P0127", "Intake Air Temperature Too High");
        hashMap.put("P0128", "Coolant Thermostat (Coolant Temp Below Thermostat Regulating Temperature)");
        hashMap.put("P0129", "Barometric Pressure Too Low");
        hashMap.put("P0130", "O2 Sensor Circuit Malfunction (Bank 2 Sensor 1)");
        hashMap.put("P0131", "O2 Sensor Circuit Low Voltage (Bank 2 Sensor 1)");
        hashMap.put("P0132", "O2 Sensor Circuit High Voltage (Bank 2 Sensor 1)");
        hashMap.put("P0133", "O2 Sensor Circuit Slow Response (Bank 2 Sensor 1)");
        hashMap.put("P0134", "O2 Sensor Circuit No Activity Detected (Bank 2 Sensor 1)");
        hashMap.put("P0135", "O2 Sensor Heater Circuit Malfunction (Bank 2 Sensor 1)");
        hashMap.put("P0136", "O2 Sensor Circuit Malfunction (Bank 1 Sensor 2)");
        hashMap.put("P0137", "O2 Sensor Circuit Low Voltage (Bank 1 Sensor 2)");
        hashMap.put("P0138", "O2 Sensor Circuit High Voltage (Bank 1 Sensor 2)");
        hashMap.put("P0139", "O2 Sensor Circuit Slow Response (Bank 1 Sensor 2)");
        hashMap.put("P0140", "O2 Sensor Circuit No Activity Detected (Bank 2 Sensor 2)");
        hashMap.put("P0141", "O2 Sensor Heater Circuit Malfunction (Bank 2 Sensor 2)");
        hashMap.put("P0142", "O2 Sensor Circuit Malfunction (Bank 2 Sensor 3)");
        hashMap.put("P0143", "O2 Sensor Circuit Low Voltage (Bank 2 Sensor 3)");
        hashMap.put("P0144", "O2 Sensor Circuit High Voltage (Bank 2 Sensor 3)");
        hashMap.put("P0145", "O2 Sensor Circuit Slow Response (Bank 2 Sensor 3)");
        hashMap.put("P0146", "O2 Sensor Circuit No Activity Detected (Bank 2 Sensor 3)");
        hashMap.put("P0147", "O2 Sensor Heater Circuit Malfunction (Bank 2 Sensor 3)");
        hashMap.put("P0148", "Fuel Delivery Error");
        hashMap.put("P0149", "Fuel Timing Error");
        hashMap.put("P0150", "O2 Sensor Circuit Malfunction (Bank 2 Sensor 1)");
        hashMap.put("P0151", "O2 Sensor Circuit Low Voltage (Bank 2 Sensor 1)");
        hashMap.put("P0152", "O2 Sensor Circuit High Voltage (Bank 2 Sensor 1)");
        hashMap.put("P0153", "O2 Sensor Circuit Slow Response (Bank 2 Sensor 1)");
        hashMap.put("P0154", "O2 Sensor Circuit No Activity Detected (Bank 2 Sensor 1)");
        hashMap.put("P0155", "O2 Sensor Heater Circuit Malfunction (Bank 2 Sensor 1)");
        hashMap.put("P0156", "O2 Sensor Circuit Malfunction (Bank 2 Sensor 2)");
        hashMap.put("P0157", "O2 Sensor Circuit Low Voltage (Bank 2 Sensor 2)");
        hashMap.put("P0158", "O2 Sensor Circuit High Voltage (Bank 2 Sensor 2)");
        hashMap.put("P0159", "O2 Sensor Circuit Slow Response (Bank 2 Sensor 2)");
        hashMap.put("P0160", "O2 Sensor Circuit No Activity Detected (Bank 2 Sensor 2)");
        hashMap.put("P0161", "O2 Sensor Heater Circuit Malfunction (Bank 2 Sensor 2)");
        hashMap.put("P0162", "O2 Sensor Circuit Malfunction (Bank 2 Sensor 3)");
        hashMap.put("P0163", "O2 Sensor Circuit Low Voltage (Bank 2 Sensor 3)");
        hashMap.put("P0164", "O2 Sensor Circuit High Voltage (Bank 2 Sensor 3)");
        hashMap.put("P0165", "O2 Sensor Circuit Slow Response (Bank 2 Sensor 3)");
        hashMap.put("P0166", "O2 Sensor Circuit No Activity Detected (Bank 2 Sensor 3)");
        hashMap.put("P0167", "O2 Sensor Heater Circuit Malfunction (Bank 2 Sensor 3)");
        hashMap.put("P0168", "Engine Fuel Temperature Too High");
        hashMap.put("P0169", "Incorrect Fuel Composition");
        hashMap.put("P0170", "Fuel Trim Malfunction (Bank 2)");
        hashMap.put("P0171", "System Too Lean (Bank 2)");
        hashMap.put("P0172", "System Too Rich (Bank 2)");
        hashMap.put("P0173", "Fuel Trim Malfunction (Bank 2)");
        hashMap.put("P0174", "System Too Lean (Bank 2)");
        hashMap.put("P0175", "System Too Rich (Bank 2)");
        hashMap.put("P0176", "Fuel Composition Sensor Circuit Malfunction");
        hashMap.put("P0177", "Fuel Composition Sensor Circuit Range/Performance");
        hashMap.put("P0178", "Fuel Composition Sensor Circuit Low Input");
        hashMap.put("P0179", "Fuel Composition Sensor Circuit High Input");
        hashMap.put("P0180", "Fuel Temperature Sensor A Circuit Malfunction");
        hashMap.put("P0181", "Fuel Temperature Sensor A Circuit Performance");
        hashMap.put("P0182", "Fuel Temperature Sensor A Circuit low Input");
        hashMap.put("P0183", "Fuel Temperature Sensor A Circuit Intermittent");
        hashMap.put("P0184", "Fuel Temperature Sensor A Circuit Intermittent");
        hashMap.put("P0185", "Fuel Temperature Sensor B Circuit Malfunction");
        hashMap.put("P0186", "Fuel Temperature Sensor B Circuit Range/Performance");
        hashMap.put("P0187", "Fuel Temperature Sensor B Circuit Low Input");
        hashMap.put("P0188", "Fuel Temperature Sensor B Circuit High Input");
        hashMap.put("P0189", "Fuel Temperature Sensor B Circuit Intermittent");
        hashMap.put("P0190", "Fuel Rail Pressure Sensor Circuit Malfunction");
        hashMap.put("P0191", "Fuel Rail Pressure Sensor Circuit Range/Performance");
        hashMap.put("P0192", "Fuel Rail Pressure Sensor Circuit Low Input");
        hashMap.put("P0193", "Fuel Rail Pressure Sensor Circuit High Input");
        hashMap.put("P0194", "Fuel Rail Pressure Sensor Circuit Intermittent");
        hashMap.put("P0195", "Engine Oil Temperature Sensor Malfunction");
        hashMap.put("P0196", "Engine Oil Temperature Sensor Range/Performance");
        hashMap.put("P0197", "Engine Oil Temperature Sensor Low");
        hashMap.put("P0198", "Engine Oil Temperature Sensor High");
        hashMap.put("P0199", "Engine Oil Temperature Sensor Intermittent");
        hashMap.put("P0200", "Injector Circuit Malfunction");
        hashMap.put("P0201", "Injector Circuit Malfunction - Cylinder 1");
        hashMap.put("P0202", "Injector Circuit Malfunction - Cylinder 2");
        hashMap.put("P0203", "Injector Circuit Malfunction - Cylinder 3");
        hashMap.put("P0204", "Injector Circuit Malfunction - Cylinder 4");
        hashMap.put("P0205", "Injector Circuit Malfunction - Cylinder 5");
        hashMap.put("P0206", "Injector Circuit Malfunction - Cylinder 6");
        hashMap.put("P0207", "Injector Circuit Malfunction - Cylinder 7");
        hashMap.put("P0208", "Injector Circuit Malfunction - Cylinder 8");
        hashMap.put("P0209", "Injector Circuit Malfunction - Cylinder 9");
        hashMap.put("P0210", "Injector Circuit Malfunction - Cylinder 10");
        hashMap.put("P0211", "Injector Circuit Malfunction - Cylinder 11");
        hashMap.put("P0212", "Injector Circuit Malfunction - Cylinder 12\n");
        hashMap.put("P0213", "Cold Start Injector 1 Malfunction");
        hashMap.put("P0214", "Cold Start Injector 2 Malfunction");
        hashMap.put("P0215", "Engine Shutoff Solenoid Malfunction");
        hashMap.put("P0216", "Injection Timing Control Circuit Malfunction");
        hashMap.put("P0217", "Engine Overtemp Condition");
        hashMap.put("P0218", "Transmission Over Temperature Condition");
        hashMap.put("P0219", "Engine Over Speed Condition");
        hashMap.put("P0220", "Throttle/Pedal Position Sensor/Switch B Circuit Malfunction");
        hashMap.put("P0221", "Throttle/Pedal Position Sensor/Switch B Circuit Range/Performance Problem");
        hashMap.put("P0222", "Throttle/Pedal Position Sensor/Switch B Circuit Low Input");
        hashMap.put("P0223", "Throttle/Pedal Position Sensor/Switch B Circuit High Input");
        hashMap.put("P0224", "Throttle/Pedal Position Sensor/Switch B Circuit Intermittent");
        hashMap.put("P0225", "Throttle/Pedal Position Sensor/Switch C Circuit Malfunction");
        hashMap.put("P0226", "Throttle/Pedal Position Sensor/Switch C Circuit Range/Performance Problem");
        hashMap.put("P0227", "Throttle/Pedal Position Sensor/Switch C Circuit Low Input");
        hashMap.put("P0228", "Throttle/Pedal Position Sensor/Switch C Circuit High Input");
        hashMap.put("P0229", "Throttle/Pedal Position Sensor/Switch C Circuit Intermittent");
        hashMap.put("P0230", "Fuel Pump Primary Circuit Malfunction");
        hashMap.put("P0231", "Fuel Pump Secondary Circuit Low");
        hashMap.put("P0232", "Fuel Pump Secondary Circuit Intermittent");
        hashMap.put("P0233", "Fuel Pump Secondary Circuit Intermittent");
        hashMap.put("P0234", "Engine Overboost Condition");
        hashMap.put("P0235", "Turbocharger Boost Sensor A Circuit Malfunction");
        hashMap.put("P0236", "Turbocharger Boost Sensor A Circuit Range/Performance.");
        hashMap.put("P0237", "Turbocharger Boost Sensor A Circuit Low");
        hashMap.put("P0238", "Turbocharger Boost Sensor A Circuit High");
        hashMap.put("P0239", "Turbocharger Boost Sensor B Circuit Malfunction");
        hashMap.put("P0240", "Turbocharger Boost Sensor B Circuit Range/Performance");
        hashMap.put("P0241", "Turbocharger Boost Sensor B Circuit Low");
        hashMap.put("P0242", "Turbocharger Boost Sensor B Circuit High");
        hashMap.put("P0243", "Turbocharger Wastegate Solenoid A Malfunction");
        hashMap.put("P0244", "Turbocharger Wastegate Solenoid A Range/Performance");
        hashMap.put("P0245", "Turbocharger Wastegate Solenoid A low");
        hashMap.put("P0246", "Turbocharger Wastegate Solenoid A High");
        hashMap.put("P0247", "Turbocharger Wastegate Solenoid B Malfunction");
        hashMap.put("P0248", "Turbocharger Wastegate Solenoid B Range/Performance");
        hashMap.put("P0249", "Turbocharger Wastegate Solenoid B Low");
        hashMap.put("P0250", "Turbocharger Wastegate Solenoid B High");
        hashMap.put("P0251", "Injection Pump Fuel Metering Control A Malfunction (Cam/Rotor/Injector)");
        hashMap.put("P0252", "Injection Pump Fuel Metering Control A Range/Performance (Cam/Rotor/Injector)");
        hashMap.put("P0253", "Injection Pump Fuel Metering Control A Low (Cam/Rotor/Injector)");
        hashMap.put("P0254", "Injection Pump Fuel Metering Control A High (Cam/Rotor/Injector)");
        hashMap.put("P0255", "Injection Pump Fuel Metering Control A Intermittent (Cam/Rotor/Injector)");
        hashMap.put("P0256", "Injection Pump Fuel Metering Control B Malfunction (Cam/Rotor/Injector)");
        hashMap.put("P0257", "Injection Pump Fuel Metering Control B Low (Cam/Rotor/Injector)");
        hashMap.put("P0258", "Injection Pump Fuel Metering Control B Low (Cam/Rotor/Injector)");
        hashMap.put("P0259", "Injection lump Fuel Metering Control B High (Cam/Rotor/Injector)");
        hashMap.put("P0260", "Injection Pump Fuel Metering Control B Intermittent (Cam/Rotor/Injector)");
        hashMap.put("P0261", "Cylinder 1 Injector Circuit Low");
        hashMap.put("P0262", "Cylinder 1 Injector Circuit High");
        hashMap.put("P0263", "Cylinder 1 Contribution/Balance Fault");
        hashMap.put("P0264", "Cylinder 2 Injector Circuit Low");
        hashMap.put("P0265", "Cylinder 2 Injector Circuit High");
        hashMap.put("P0266", "Cylinder 2 Contribution/Balance Fault");
        hashMap.put("P0267", "Cylinder 3 Injector Circuit Low");
        hashMap.put("P0268", "Cylinder 3 Injector Circuit High");
        hashMap.put("P0269", "Cylinder 3 Contribution/Balance Fault");
        hashMap.put("P0270", "Cylinder 4 Injector Circuit Low");
        hashMap.put("P0271", "Cylinder 4 Injector Circuit High");
        hashMap.put("P0272", "Cylinder 4 Contribution/Balance Fault");
        hashMap.put("P0273", "Cylinder 5 Injector Circuit Low");
        hashMap.put("P0274", "Cylinder 5 Injector Circuit High");
        hashMap.put("P0275", "Cylinder 5 Contribution/Balance Fault");
        hashMap.put("P0276", "Cylinder 6 Injector Circuit Low");
        hashMap.put("P0277", "Cylinder 6 Injector Circuit High");
        hashMap.put("P0278", "Cylinder 6 Contribution/Balance Fault");
        hashMap.put("P0279", "Cylinder 7 Injector Circuit Low");
        hashMap.put("P0280", "Cylinder 7 Injector Circuit High");
        hashMap.put("P0281", "Cylinder 7 Contribution/Balance Fault");
        hashMap.put("P0282", "Cylinder 8 Injector Circuit Low");
        hashMap.put("P0283", "Cylinder 8 Injector Circuit High");
        hashMap.put("P0284", "Cylinder 8 Contribution/Balance Fault");
        hashMap.put("P0285", "Cylinder 9 Injector Circuit Low");
        hashMap.put("P0286", "Cylinder 9 Injector Circuit High");
        hashMap.put("P0287", "Cylinder 9 Contribution/Balance Fault");
        hashMap.put("P0288", "Cylinder 10 Injector Circuit Low");
        hashMap.put("P0289", "Cylinder 10 Injector Circuit High");
        hashMap.put("P0290", "Cylinder 10 Contribution/Balance Fault");
        hashMap.put("P0291", "Cylinder 11 Injector Circuit Low");
        hashMap.put("P0292", "Cylinder 11 Injector Circuit High");
        hashMap.put("P0293", "Cylinder 11 Contribution/Balance Fault");
        hashMap.put("P0294", "Cylinder 12 Injector Circuit Low");
        hashMap.put("P0295", "Cylinder 12 Injector Circuit High");
        hashMap.put("P0296", "Cylinder 12 Contribution/Balance Fault");
        hashMap.put("P0297", "Vehicle Overspeed Condition");
        hashMap.put("P0298", "Engine Oil Over Temperature");
        hashMap.put("P0299", "Turbo / Super Charger Underboost");
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
