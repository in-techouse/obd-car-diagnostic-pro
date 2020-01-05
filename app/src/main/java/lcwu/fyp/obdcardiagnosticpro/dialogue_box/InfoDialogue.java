package lcwu.fyp.obdcardiagnosticpro.dialogue_box;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import lcwu.fyp.obdcardiagnosticpro.R;

public class InfoDialogue extends Dialog implements View.OnClickListener {
    private Button close;
    private TextView label;
    public InfoDialogue(@NonNull Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.info_dialoguebox);
        close = findViewById(R.id.close);
        close.setOnClickListener(this);
        label=findViewById(R.id.label);
        label.setText("Engine RPM");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close:
                dismiss();
                break;
        }


    }
}
