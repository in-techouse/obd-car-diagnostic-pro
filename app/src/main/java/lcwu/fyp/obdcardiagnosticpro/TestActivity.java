package lcwu.fyp.obdcardiagnosticpro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class TestActivity extends AppCompatActivity {
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        text=findViewById(R.id.text);
        Session session = new Session(TestActivity.this);
        String str = session.getRPM();
        String str1 = session.getSpeed();

        text.setText("RPM: " + str + "\n\nSPEED: " + str1);
    }
}
