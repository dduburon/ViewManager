package com.viewmanager.util;

import java.io.File;

public class ViewMEnvUtil {

	public static String CONFIG_LOC;

	public static final String VIEW_CONFIG_HOME_NAME = "ViewM_HOME";

	static {
		String conf_loc = System.getenv(VIEW_CONFIG_HOME_NAME);
		if (conf_loc == null || conf_loc.isEmpty()) {
			conf_loc = System.getProperty(VIEW_CONFIG_HOME_NAME);
		}
		if (conf_loc == null || conf_loc.isEmpty()) {
			conf_loc = System.getProperty(VIEW_CONFIG_HOME_NAME.toLowerCase());
		}
		if (conf_loc == null || conf_loc.isEmpty()) {
			throw new RuntimeException("No configuration home set. Please set the environment variable or java property: " + VIEW_CONFIG_HOME_NAME);
		}
		if(!conf_loc.endsWith(File.separator)) {
			conf_loc += File.separator;
		}
		ViewMEnvUtil.CONFIG_LOC = conf_loc;
	}
}