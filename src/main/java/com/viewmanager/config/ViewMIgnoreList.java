package com.viewmanager.config;

import com.viewmanager.pojo.ViewPojo;
import com.viewmanager.util.ViewMEnvUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ViewMIgnoreList {

    final static Logger logger = LoggerFactory.getLogger(ViewMIgnoreList.class);
    private static Properties ignoreFiles;
    private static String EQUALS_KEY = "equals";
    private static String CONTAINS_KEY = "contains";
    private static String STARTS_KEY = "starts";
    private static String ENDS_KEY = "ends";
    private static Map<String,List<String>> blockingIgnoreFiles;

    public static final String VIEW_FILE_IGNORE_NAME = "IgnoreViews.properties";

    static {
        try (InputStream stream = new FileInputStream(getViewFileIgnoreList())) {
            ignoreFiles = new Properties();
            ignoreFiles.load(stream);
        } catch (IOException e) {
            logger.error("Error encountered loading ignore file list.");
        }
        blockingIgnoreFiles = Collections.synchronizedMap(new HashMap<>());
        List<String> equals = new ArrayList<>();
        List<String> contains = new ArrayList<>();
        List<String> starts = new ArrayList<>();
        List<String> ends = new ArrayList<>();
        for (String key : ignoreFiles.stringPropertyNames()) {
            if (key.startsWith("%")) {
                if (key.endsWith("%")) {
                    contains.add(key.substring(1,key.length()-1));
                } else {
                    ends.add(key.substring(1));
                }
            } else if(key.endsWith("%")){
                starts.add(key.substring(0,key.length()-1));
            } else {
                equals.add(key);
            }
        }
        blockingIgnoreFiles.put(EQUALS_KEY, equals);
        blockingIgnoreFiles.put(CONTAINS_KEY, contains);
        blockingIgnoreFiles.put(STARTS_KEY, starts);
        blockingIgnoreFiles.put(ENDS_KEY, ends);
    }

    public static String getViewFileIgnoreList() {
        return ViewMEnvUtil.CONFIG_LOC + VIEW_FILE_IGNORE_NAME;
    }

    public static boolean ignoreFile(String fileName) {
        boolean ignore = false;
        for (String contains : blockingIgnoreFiles.get(CONTAINS_KEY)) {
            if (fileName.contains(contains)) {
                ignore = true;
                break;
            }
        }
        if (!ignore) {
            for (String equals : blockingIgnoreFiles.get(EQUALS_KEY)) {
                if (fileName.equals(equals)) {
                    ignore = true;
                    break;
                }
            }
        }
        if (!ignore) {
            for (String starts : blockingIgnoreFiles.get(STARTS_KEY)) {
                if (fileName.startsWith(starts)) {
                    ignore = true;
                    break;
                }
            }
        }
        if (!ignore) {
            for (String ends : blockingIgnoreFiles.get(ENDS_KEY)) {
                if (fileName.substring(0,fileName.lastIndexOf(".")).endsWith(ends)) {
                    ignore = true;
                    break;
                }
            }
        }
        return ignore;
    }
}
