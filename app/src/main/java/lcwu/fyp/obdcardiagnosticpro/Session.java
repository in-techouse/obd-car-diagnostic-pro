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

    public void setAcc040(AccelerationTestObject acc040){
        String value = gson.toJson(acc040);
        editor.putString("acc040", value);
        editor.commit();
    }

    public AccelerationTestObject getAcc040(){
        AccelerationTestObject acc040;
        try{

            String value = preferences.getString("acc040", "*");
            if(value == null || value.equals("*")){
                acc040 = new AccelerationTestObject();
            }
            else{
                acc040 = gson.fromJson(value, AccelerationTestObject.class);
            }
        }
        catch (Exception e){
            acc040 = new AccelerationTestObject();
        }
        return acc040;
    }
}
