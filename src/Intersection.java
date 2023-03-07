/**
 * File Name: Intersection.java
 * Date: 05 MAR 2023
 * Author: Joseph Julian
 * Purpose: Intersection class implements Runnable interface with functionality to start, stop, suspend, resume, and
 * interrupt threads, as well as update the GUI with intersection state. Actions are printed to the console as they
 * relate to the relevant thread.
 */

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Intersection implements Runnable {
    private static final String[] COLORS = {
            "Green",
            "Yellow",
            "Red"
    };
    public final AtomicBoolean isSuspended = new AtomicBoolean(false);
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final JLabel lblLight;
    private final String threadName;
    private int i = 0;

    public Intersection(String threadName, JLabel lblLight) {
        this.threadName = threadName;
        this.lblLight = lblLight;
        System.out.println("CREATE:\t\t" + threadName);
    }

    public synchronized String getColor() {
        return COLORS[i];
    }

    public void suspendThread() {
        isSuspended.set(true);
        System.out.println("SUSPEND:\t" + threadName);
    }

    public synchronized void resumeThread() {
        isSuspended.set(false);
        notify();
        System.out.println("RESUME:\t\t" + threadName);
    }

    public void startThread() {
        System.out.println("START:\t\t" + threadName);
        if (isRunning.compareAndSet(false, true)) {
            Thread thread = new Thread(this, threadName);
            thread.start();
        }
    }

    public void stopThread() {
        isRunning.set(false);
        System.out.println("STOP:\t\t" + threadName);
    }

    public void interruptThread() {
        Thread.currentThread().interrupt();
    }

    @Override
    public void run() {
        System.out.println("RUN:\t\t" + threadName);
        while (isRunning.get()) {
            try {
                synchronized (this) {
                    while (isSuspended.get()) {
                        System.out.println("WAIT:\t\t" + threadName);
                        wait();
                    }
                }
                switch (getColor()) {
                    case "Green" -> {
                        lblLight.setForeground(new Color(0, 255, 0));
                        lblLight.setText(getColor());
                        Thread.sleep(10000);
                        i++;
                    }
                    case "Yellow" -> {
                        lblLight.setForeground(new Color(255, 255, 0));
                        lblLight.setText(getColor());
                        Thread.sleep(5000);
                        i++;
                    }
                    case "Red" -> {
                        lblLight.setForeground(new Color(255, 0, 0));
                        lblLight.setText(getColor());
                        Thread.sleep(5000);
                        i = 0;
                    }
                }
                i %= COLORS.length;
            } catch (InterruptedException ex) {
                isSuspended.set(true);
                Thread.currentThread().interrupt();
            }
        }
    }
}