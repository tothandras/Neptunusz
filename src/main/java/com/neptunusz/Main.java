package com.neptunusz;

import com.neptunusz.view.NeptunuszDialog;

import java.util.Calendar;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Oh my god
 */
public class Main {

    private static final NeptunuszDialog dialog = new NeptunuszDialog();

    public static void main(String[] args) {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

        //Get current time
        long timeInMillis = Calendar.getInstance().getTimeInMillis();
        //Calculate the wished
        Calendar calendar = Calendar.getInstance();
        calendar.set(2014, Calendar.JANUARY, 30, 18, 0, 0);

        long diff = calendar.getTimeInMillis() - timeInMillis;

        //Schedule if diff is >0
        if (diff > 0) {
            executor.schedule(new Runnable() {
                @Override
                public void run() {
                    //Run this in the dialog's thread
                    synchronized (dialog) {
                        dialog.onOK();
                    }
                }
            }, diff, TimeUnit.MILLISECONDS);
            System.out.println("Registering scheduled at " + calendar.getTime());
        }

        System.out.println("Initializing the dialog...");
        dialog.pack();
        dialog.setVisible(true);
    }
}
