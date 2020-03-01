package lcwu.fyp.obdcardiagnosticpro.dialogue_box;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.capur16.digitspeedviewlib.DigitSpeedView;

import lcwu.fyp.obdcardiagnosticpro.R;

public class InfoDialogue extends Dialog implements View.OnClickListener {
    private String heading;
    private int speed;

    public InfoDialogue(@NonNull Activity activity, String h, int s) {
        super(activity);
        heading = h;
        speed = s;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.info_dialoguebox);
        Button close = findViewById(R.id.close);
        close.setOnClickListener(this);
        TextView label = findViewById(R.id.label);
        label.setText(heading);
        DigitSpeedView speedView = findViewById(R.id.speedView);
        speedView.hideUnit();
        speedView.updateSpeed(speed);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                dismiss();
                break;
        }


    }
}
