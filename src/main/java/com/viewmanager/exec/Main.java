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
            case "G": //Generate View Registry and Dependency Cache
                ViewMSorter.genViewRegistry();
                break;
            case "C": // Create All
                ViewMBulkActions.createAllIgnoreErrors();
                break;
            case "U": {// Uninstall View
                String view = args[1];
                ViewMExecUtil.uninstall(view);
                break;
            }
            case "I": {// Install View
                String view = args[1];
                ViewMExecUtil.install(view);
                break;
            }
        }
    }
}
