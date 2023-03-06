/**
 * File Name: Car.java
 * Date: 05 MAR 2023
 * Author: Joseph Julian
 * Purpose: Car class implements Runnable interface with functionality to start, stop, suspend, and resume threads, as
 * well as return car position and speed. Actions are printed to the console as they relate to the relevant thread.
 */

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class Car implements Runnable {
    public final AtomicBoolean isAtLight = new AtomicBoolean(false);
    public final AtomicBoolean isSuspended = new AtomicBoolean(false);
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final String threadName;
    private final Thread thread;
    private int xPosition;

    public Car(String name, int max, int min) {
        this.threadName = name;
        this.xPosition = ThreadLocalRandom.current().nextInt(min, max);
        this.thread = new Thread(this, threadName);
        System.out.println("CREATE:\t\t" + threadName);
    }

    public int getPosition() {
        return xPosition;
    }

    public int getSpeed() {
        int speed;
        if (isRunning.get()) {
            if (isAtLight.get())
                speed = 0;
            else
                speed = 3 * 60; // Incrementing 50 meters/second (3 kilometers/minute * 60 = 180 kilometers/hour)
        } else
            speed = 0;
        return speed;
    }

    public void startThread() {
        System.out.println("START:\t\t" + threadName);
        if (!thread.isAlive()) {
            thread.start();
        }
    }

    public void stopThread() {
        thread.interrupt();
        isRunning.set(false);
        System.out.println("STOP:\t\t" + threadName);
    }

    public void suspendThread() {
        isSuspended.set(true);
        System.out.println("SUSPEND:\t" + threadName);
    }

    public void resumeThread() {
        if (isSuspended.get() || isAtLight.get()) {
            isSuspended.set(false);
            isAtLight.set(false);
            synchronized (this) {
                notify();
            }
            System.out.println("RESUME:\t\t" + threadName);
        }
    }

    @Override
    public void run() {
        System.out.println("RUN:\t\t" + threadName);
        isRunning.set(true);
        while (isRunning.get()) {
            try {
                while (xPosition < 3000) {
                    synchronized (this) {
                        while (isSuspended.get() || isAtLight.get()) {
                            System.out.println("WAIT:\t\t" + threadName);
                            wait();
                        }
                    }
                    if (isRunning.get()) {
                        Thread.sleep(100);
                        xPosition += 5;
                    }
                }
                xPosition = 0;
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
