/**
 * File Name: Time.java
 * Date: 05 MAR 2023
 * Author: Joseph Julian
 * Purpose: Time class implements Runnable interface to provide the current system time in 1 second intervals
 */

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class Time implements Runnable {
    private final String timePattern = "hh:mm:ss a";
    private final SimpleDateFormat timeFormat = new SimpleDateFormat(timePattern);

    Date date = new Date(System.currentTimeMillis());

    public Time() {
    }

    public String getTime() {
        date = new Date(System.currentTimeMillis());
        return timeFormat.format(date);
    }

    @Override
    public void run() {
        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            Instant.now();
            String formattedTime = getTime();
            Main.lblCurrentTime.setText(formattedTime);
        });
        timer.start();
    }
}