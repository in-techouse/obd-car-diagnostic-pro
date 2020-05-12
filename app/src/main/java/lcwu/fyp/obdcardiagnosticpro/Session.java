package lcwu.fyp.obdcardiagnosticpro;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import lcwu.fyp.obdcardiagnosticpro.model.AccelerationTestObject;


public class Session {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public Session(Context c) {
        preferences = PreferenceManager.getDefaultSharedPreferences(c);
        editor = preferences.edit();
        gson = new Gson();
    }

    public void setRPM(String str) {
        str = "Old Value: " + getRPM() + "\nNew Value: " + str;
        editor.putString("rpm", str);
        editor.commit();
    }

    public String getRPM() {
        return preferences.getString("rpm", "");
    }

    public void setSpeed(String str) {
        str = "Old Value: " + getSpeed() + "\nNew Value: " + str;
        editor.putString("speed", str);
        editor.commit();
    }

    public String getSpeed() {
        return preferences.getString("speed", "*");
    }

    public void setAcc(AccelerationTestObject acc, String key) {
        String value = gson.toJson(acc);
        editor.putString(key, value);
        editor.commit();
    }

    public AccelerationTestObject getAcc(String key) {
        AccelerationTestObject acc;
        try {
            String value = preferences.getString(key, "*");
            if (value == null || value.equals("*")) {
                acc = new AccelerationTestObject();
            } else {
                acc = gson.fromJson(value, AccelerationTestObject.class);
            }
        } catch (Exception e) {
            acc = new AccelerationTestObject();
        }
        return acc;
    }
}
