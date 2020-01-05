package lcwu.fyp.obdcardiagnosticpro.director;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.util.Log;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import lcwu.fyp.obdcardiagnosticpro.Dashboard;
import lcwu.fyp.obdcardiagnosticpro.R;

public class Helpers

{

   public void showError(Activity activity, String title, String Message){
        new FancyAlertDialog.Builder(activity)
                .setTitle(title)
                .setBackgroundColor(Color.parseColor("#303F9F"))  //Don't pass R.color.colorvalue
                .setMessage(Message)
                .setNegativeBtnText("Cancel")
                .setPositiveBtnBackground(Color.parseColor("#FF4081"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("Okay")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  //Don't pass R.color.colorvalue
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .setIcon(R.drawable.ic_star_border_black_24dp, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                    }
                })
                .build();
    }
    public void showSuccess(Activity activity, String title, String Message){
        new FancyAlertDialog.Builder(activity)
                .setTitle(title)
                .setBackgroundColor(Color.parseColor("#303F9F"))  //Don't pass R.color.colorvalue
                .setMessage(Message)
                .setNegativeBtnText("Cancel")
                .setPositiveBtnBackground(Color.parseColor("#FF4081"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("Okay")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  //Don't pass R.color.colorvalue
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .setIcon(R.drawable.ic_star_border_black_24dp, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                    }
                })
                .build();
    }
    public String getRPMdata(BluetoothSocket socket)  throws Exception
    {
        new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
        new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
        new TimeoutCommand(1).run(socket.getInputStream(), socket.getOutputStream());
        new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
        RPMCommand rpmCommand = new RPMCommand();
        String str = "";
        while (!Thread.currentThread().isInterrupted()) {
            rpmCommand.run(socket.getInputStream(),socket.getOutputStream());
            str = str + "\nRPM: " + rpmCommand.getFormattedResult();
            Log.e("OBD","RPM:" + rpmCommand.getFormattedResult());
        }
        return str;
    }
    public String getSpeeddata(BluetoothSocket socket)  throws Exception
    {
        new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
        new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
        new TimeoutCommand(1).run(socket.getInputStream(), socket.getOutputStream());

        new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
        SpeedCommand speedCommand = new SpeedCommand();
        String str = "";
        while (!Thread.currentThread().isInterrupted()) {
            speedCommand.run(socket.getInputStream(),socket.getOutputStream());
            str = str + "\nSpeed: " + speedCommand.getFormattedResult();
            Log.e("OBD","Speed:" + speedCommand.getFormattedResult());
        }
        return str;
    }
}
