package com.viewmanager.util;

import com.viewmanager.config.ViewMConfig;
import com.viewmanager.config.ViewMOrderedList;
import com.viewmanager.pojo.ViewPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewMVerifier {

    final static Logger logger = LoggerFactory.getLogger(ViewMVerifier.class);

    public static void verifyConfig() {
        verifySQLFilesExist();
        verifySQLFilesAreRegistered();
        verifySQLFilesAreAnnotated();
    }

    private static void verifySQLFilesExist() {
        List<String> missingFiles = new ArrayList<>();
        for (ViewPojo view : ViewMOrderedList.getViewList()) {
            File viewFile = new File(view.getFileName());
            if(!viewFile.exists()) {
                missingFiles.add(view.getName());
            }
        }
        if (!missingFiles.isEmpty()) {
            String errorMsg = "The view file could not be found for the following views: \n ";
            for (String missing : missingFiles) {
                errorMsg += "\t" + missing + "\n";
            }
            logger.error(errorMsg);
        } else {
            logger.info("All registered views had a matching file.");
        }
    }

    private static void verifySQLFilesAreRegistered(){
        List<String> missingReg = new ArrayList<>();
        for (File file : Objects.requireNonNull(ViewMConfig.getViewsLoc().listFiles())) {
            if(!ViewMOrderedList.ignoreFile(file.getName()) && !ViewMOrderedList.containsFile(file.getName())) {
                missingReg.add(file.getName());
            }
        }
        if (!missingReg.isEmpty()) {
            String errorMsg = "The following view files were not found: \n ";
            for (String missing : missingReg) {
                errorMsg += "\t" + missing + "\n";
            }
            throw new RuntimeException(errorMsg);
        } else {
            logger.info("All sql files were matched to registered view.");
        }
    }


    private static void verifySQLFilesAreAnnotated() {
        List<String> neglectedFiles = new ArrayList<>();
        for (ViewPojo view : ViewMOrderedList.getViewList()) {
            String sqlText = ViewFileUtil.getSQLFromViewFile(view);
            if(sqlText.isEmpty() || sqlText.length() < 10) {
                neglectedFiles.add(view.getName());
            }
        }
        if (!neglectedFiles.isEmpty()) {
            String errorMsg = "SQL Files Not correctly annotated: \n ";
            for (String missing : neglectedFiles) {
                errorMsg += "\t" + missing + "\n";
            }
            logger.error(errorMsg);
        } else {
            logger.info("All SQL Files seem to be annotated correctly.");
        }
    }
}
