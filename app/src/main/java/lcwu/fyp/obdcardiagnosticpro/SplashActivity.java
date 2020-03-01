package lcwu.fyp.obdcardiagnosticpro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SplashActivity extends Activity {

    ImageView application_logo;
    TextView application_name;
    Animation upToDown, downToUp;
    RelativeLayout imageUpper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        application_logo = findViewById(R.id.application_logo);
        application_name = findViewById(R.id.application_name);
        imageUpper = findViewById(R.id.imageUpper);

        upToDown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        downToUp = AnimationUtils.loadAnimation(this, R.anim.downtoup);


        imageUpper.setAnimation(downToUp);
        application_name.setAnimation(upToDown);

        RotateAnimation rotate = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(3000);
        rotate.setInterpolator(new LinearInterpolator());
        application_logo.startAnimation(rotate);


        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(4000);
                    Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}
