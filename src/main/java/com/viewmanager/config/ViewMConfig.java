package com.viewmanager.config;

import com.viewmanager.util.ViewMEnvUtil;

import java.io.*;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewMConfig {

    final static Logger logger = LoggerFactory.getLogger(ViewMConfig.class);

    private static Properties configProp;

    static {
        try (InputStream stream = new FileInputStream(ViewMEnvUtil.CONFIG_LOC + "ViewM.properties")) {
            configProp = new Properties();
            configProp.load(stream);
        } catch (IOException e) {
            logger.error("Error encoutered loading config property file");
        }
    }

    public static File getViewsLoc() {
        return new File(configProp.getProperty("view_loc"));
    }

    public static String getDBServer() {
        return configProp.getProperty("db_server");
    }

    public static String getDBName() {
        return configProp.getProperty("db_name");
    }

    public static String getDBUser() {
        return configProp.getProperty("db_un");
    }

    public static String getDBPass() {
        return configProp.getProperty("db_pw");
    }

    public static Integer getDBPort() {
        return Integer.valueOf(configProp.getProperty("db_port"));
    }

    public static String getViewFileSuffix() {
        return configProp.getProperty("view_file_suffix");
    }

}
