package com.viewmanager.util;

import com.viewmanager.pojo.ViewPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ViewFileUtil {

    final static Logger logger = LoggerFactory.getLogger(ViewFileUtil.class);

    public static final String VIEW_CREATE_START_SEP = "-----VIEW-MANAGER-CREATE-VIEW-START-----";
    public static final String VIEW_CREATE_END_SEP = "-----VIEW-MANAGER-CREATE-VIEW-END-----";

    public static String getSQLFromViewFile(ViewPojo view) {
        return getSQLFromViewFile(view.getFileName());
    }
    public static String getSQLFromViewFile(String filePath) {
        return getSQLFromViewFile(new File(filePath));
    }

    private static String getSQLFromViewFile(File viewFile) {
        String sqlStr = "";
        Boolean foundStart = false;
        try (Scanner sc = new Scanner(viewFile)) {
            while (sc.hasNextLine()) {
                String textLine = sc.nextLine();
                if(foundStart) {
                    if(VIEW_CREATE_END_SEP.equals(textLine)) {
                        return sqlStr;
                    }
                    sqlStr += textLine + "\n";
                } else if(VIEW_CREATE_START_SEP.equals(textLine)){
                    foundStart = true;
                } else {
                    continue;
                }
            }
        } catch (FileNotFoundException e) {
            logger.error("SQL File ({}) related to view was not found.", viewFile.getName());
        }
        if (!foundStart && sqlStr.isEmpty()) {
            logger.error(String.format("SQL File '%s' not properly annotated.", viewFile.getName()));
        }
        return sqlStr;
    }

}
