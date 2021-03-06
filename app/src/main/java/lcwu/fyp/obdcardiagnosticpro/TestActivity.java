package lcwu.fyp.obdcardiagnosticpro;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TestActivity extends AppCompatActivity {
    private TextView text;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        text = findViewById(R.id.text);
        session = new Session(TestActivity.this);
        new FetchData().execute();
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

    class FetchData extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("TestActivity", "onPreExecute");
        }

        @Override
        protected String doInBackground(Void... voids) {
            String str = session.getRPM();
            String str1 = session.getSpeed();
            str = "Final RPM: " + str + "\n\nFinal SPEED: " + str1;
            Log.e("TestActivity", "doInBackground");
            Log.e("TestActivity", "doInBackground, Str is: " + str);
            return str;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            text.setText(s);
            Log.e("TestActivity", "onPostExecute");
        }
    }
}
