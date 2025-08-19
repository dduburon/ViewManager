package com.viewmanager.exec;

import com.viewmanager.util.ViewMBulkActions;
import com.viewmanager.util.ViewMExecUtil;
import com.viewmanager.util.ViewMSorter;
import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws ParseException {
        // create Options object
        Options options = new Options();

        options.addOption("a", true, "Action to pass the program. Availabe Actions are:\n" +
                "V - Verify config\nG - Generate\nC - Create All\nU - Uninstall (requires view name)\nI - Install (requires view name)");
        options.addOption("v", true, "View Name to install / uninstall.");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        MODE mode = MODE.valueOf(cmd.getOptionValue("a"));
        switch (mode) {
            case V: //Verify config
            default:
                ViewMExecUtil.verify();
                break;
            case G: //Generate View Registry and Dependency Cache
                ViewMSorter.genViewRegistry();
                break;
            case C: // Create All
                ViewMBulkActions.createAllIgnoreErrors();
                break;
            case U: {// Uninstall View
                String view = cmd.getOptionValue("v");
                ViewMExecUtil.uninstall(view);
                break;
            }
            case I: {// Install View
                String viewProp = cmd.getOptionValue("v");
                List<String> viewList = new ArrayList();
                if (viewProp.contains(",")) {
                    viewList.addAll(List.of(viewProp.split(",")));
                } else {
                    viewList.add(viewProp);
                }
                for (String view : viewList) {
                    ViewMExecUtil.install(view);
                }
                break;
            }
        }
    }

    public enum MODE {
        V, //Verify
        G, //Generate
        C, //Create All Views
        U, //Uninstall
        I //Install
    }
}
