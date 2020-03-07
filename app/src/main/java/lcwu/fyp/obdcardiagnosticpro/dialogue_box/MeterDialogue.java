package lcwu.fyp.obdcardiagnosticpro.dialogue_box;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.anastr.speedviewlib.SpeedView;

import lcwu.fyp.obdcardiagnosticpro.R;

public class MeterDialogue extends Dialog implements View.OnClickListener{

    private String heading;
    private int speed;

    public MeterDialogue(@NonNull Context context, String h, int s) {
        super(context);
        heading = h;
        speed =  s;
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.meter_dialogue);
        Button close = findViewById(R.id.close);
        close.setOnClickListener(this);
        TextView label = findViewById(R.id.label);
        label.setText(heading);
        SpeedView speedView = findViewById(R.id.speedView);
        speedView.setSpeedAt(speed);

    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
