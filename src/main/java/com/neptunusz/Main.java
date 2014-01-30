package com.neptunusz;

import com.neptunusz.view.NeptunuszDialog;

/**
 * Oh my god
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Initializing the dialog...");
        NeptunuszDialog dialog = new NeptunuszDialog();
        dialog.pack();
        dialog.setVisible(true);
    }
}
