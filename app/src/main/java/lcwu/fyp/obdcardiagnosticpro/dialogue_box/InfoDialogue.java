package lcwu.fyp.obdcardiagnosticpro.dialogue_box;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import lcwu.fyp.obdcardiagnosticpro.R;

public class InfoDialogue extends Dialog implements View.OnClickListener {
    public InfoDialogue(@NonNull Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.info_dialoguebox);
    }

    @Override
    public void onClick(View v) {

    }
}
