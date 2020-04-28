package lcwu.fyp.obdcardiagnosticpro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class AccelerationTest extends AppCompatActivity {
    private LinearLayout speed040,speed060,speed080,speed0100,speed0120;
    private ImageView reset040,reset060,reset080,reset0100,reset0120;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceleration_test);
        speed040=findViewById(R.id.speed040);
        speed060=findViewById(R.id.speed060);
        speed080=findViewById(R.id.speed080);
        speed0100=findViewById(R.id.speed0100);
        speed0120=findViewById(R.id.speed0120);
        reset040=findViewById(R.id.reset040);
        reset060=findViewById(R.id.reset060);
        reset080=findViewById(R.id.reset080);
        reset0100=findViewById(R.id.reset0100);
        reset0120=findViewById(R.id.reset0120);

        reset060.setOnClickListener(this);
        reset040.setOnClickListener(this);
        reset080.setOnClickListener(this);
        reset0100.setOnClickListener(this);
        reset0120.setOnClickListener(this);
    }
}
