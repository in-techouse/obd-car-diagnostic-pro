package lcwu.fyp.obdcardiagnosticpro;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class Session {
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    public Session(Context c){
        context = c;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();

    }

    public void setRPM(String str){
        editor.putString("rpm", str);
        editor.commit();
    }

    public String getRPM(){
        String value = preferences.getString("rpm", "*");
        return value;
    }

    public void setSpeed(String str){
        editor.putString("speed", str);
        editor.commit();
    }

    public String getSpeed(){
        String value = preferences.getString("speed", "*");
        return value;
    }
}
