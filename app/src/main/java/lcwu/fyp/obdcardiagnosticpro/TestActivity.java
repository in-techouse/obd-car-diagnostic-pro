package lcwu.fyp.obdcardiagnosticpro;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TestActivity extends AppCompatActivity {
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        text = findViewById(R.id.text);
        Session session = new Session(TestActivity.this);
        String str = session.getRPM();
        String str1 = session.getSpeed();

        text.setText("Final RPM: " + str + "\n\nFinal SPEED: " + str1);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return true;
    }
}
