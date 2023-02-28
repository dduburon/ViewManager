package com.viewmanager.exec;

import com.viewmanager.util.ViewMBulkActions;
import com.viewmanager.util.ViewMExecUtil;
import com.viewmanager.util.ViewMSorter;

public class Main {

    public static void main(String[] args) {
        String mode = null;
        if (args.length >= 1) {
            mode = args[0];
        }
        switch (mode) {
            case "V": //Verify
            default:
                ViewMExecUtil.verify();
                break;
            case "S": //Sort
                ViewMSorter.sortViewRegistry();
                break;
            case "C": // Create All
                ViewMBulkActions.createAllIgnoreErrors();
                break;
        }
    }
}
