package com.viewmanager.config;

import com.viewmanager.pojo.ViewPojo;
import com.viewmanager.util.ViewMEnvUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class ViewMOrderedList {

    final static Logger logger = LoggerFactory.getLogger(ViewMOrderedList.class);
    private static Properties viewList;
    private static List<ViewPojo> blockingViewList;

    public static final String VIEW_LIST_NAME = "viewlist.properties";

    static {
        try (InputStream stream = new FileInputStream(ViewMEnvUtil.CONFIG_LOC + VIEW_LIST_NAME)) {
            viewList = new Properties();
            viewList.load(stream);
        } catch (IOException e) {
            logger.error("Error encoutered loading config property file");
        }
        blockingViewList = Collections.synchronizedList(new ArrayList<>());
        for (String key : viewList.stringPropertyNames()) {
            String viewFileName = (String) viewList.get(key);
            ViewPojo view;
            if (viewFileName == null || viewFileName.isEmpty()) {
                view = new ViewPojo((String) key);
            } else {
                view = new ViewPojo((String) key, viewFileName);
            }
            blockingViewList.add(view);
        }
    }

    protected static List<ViewPojo> getLiveViewList() {
        return blockingViewList;
    }
    public static List<ViewPojo> getViewList() {
        List<ViewPojo> copiedList = new ArrayList<>();
        for (ViewPojo viewPojo : getLiveViewList()) {
            copiedList.add(new ViewPojo(viewPojo));
        }
        return copiedList;
    }

    public static boolean containsFile(String fileName) {
        return getViewList().stream().anyMatch(v -> v.getFileName().endsWith(fileName))
                && !fileName.contains("_old") && !fileName.contains("_dep")
                && !fileName.contains("_withoutview")
                && !Arrays.asList("ListOfViews.txt",
                        "next_matchup_view.sql",
                        "wr_role_view_WIP.sql")
                .contains(fileName);
    }

    public static ViewPojo getViewPojoByName(String name) {
        return getViewPojoByName(getViewList(), name);
    }
    public static ViewPojo getViewPojoByName(List<ViewPojo> list, String name) {
        return list.stream().filter(v -> v.getName().equals(name)).findFirst().get();
    }

    public static void updateDependencies(ViewPojo viewWithDependencies) {
        synchronized (blockingViewList) {
            ViewPojo viewToUpdate = getViewPojoByName(getLiveViewList(), viewWithDependencies.getName());
            viewToUpdate.setDependentViews(viewWithDependencies.getDependentViews());
        }
    }

    public static void sortViewList(Comparator comp) {
        synchronized (blockingViewList) {
            Collections.sort(getLiveViewList(),comp);
        }
    }
}
