package lcwu.fyp.obdcardiagnosticpro.model;

import java.io.Serializable;

public class AccelerationTestObject implements Serializable {
    private int upperSpeed, hour, minute, second;

    public AccelerationTestObject() {
        upperSpeed = 0;
        hour = 0;
        minute = 0;
        second = 0;
    }

    public AccelerationTestObject(int upperSpeed, int hour, int minute, int second) {
        this.upperSpeed = upperSpeed;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public int getUpperSpeed() {
        return upperSpeed;
    }

    public void setUpperSpeed(int upperSpeed) {
        this.upperSpeed = upperSpeed;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }
}
