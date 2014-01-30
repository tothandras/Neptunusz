package com.neptunusz;

import com.neptunusz.view.NeptunuszDialog;

import java.util.Calendar;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final NeptunuszDialog dialog = new NeptunuszDialog();

    public static void main(String[] args) {
        dialog.pack();
        dialog.setVisible(true);
    }
}
