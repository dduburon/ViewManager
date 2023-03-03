package com.viewmanager.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewMExecUtil {

    final static Logger logger = LoggerFactory.getLogger(ViewMExecUtil.class);

    public static void verify() {
        ViewMVerifier.verifyConfig();
    }

    public static void uninstall(String view) {
        ViewMBulkActions.uninstallView(view);
    }

    public static void install(String view) {
        ViewMBulkActions.installView(view);
    }
}
