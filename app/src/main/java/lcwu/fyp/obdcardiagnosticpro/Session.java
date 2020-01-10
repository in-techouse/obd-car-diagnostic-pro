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
        str = "Old Value: " + getRPM() + "\n" + str;
        editor.putString("rpm", str);
        editor.commit();
    }

    public String getRPM(){
        return preferences.getString("rpm", "*");
    }

    public void setSpeed(String str){
        str = "Old Value: " + getSpeed() + "\n" + str;
        editor.putString("speed", str);
        editor.commit();
    }

    public String getSpeed(){
        return preferences.getString("speed", "*");
    }
}
