package com.viewmanager.config;

import com.viewmanager.pojo.ViewPojo;
import com.viewmanager.util.FileUtil;
import com.viewmanager.util.ViewMEnvUtil;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ViewMOrderedList {

    final static Logger logger = LoggerFactory.getLogger(ViewMOrderedList.class);
    private static Properties viewList;
    private static List<ViewPojo> blockingViewList;

    public static final String VIEW_LIST_NAME = "ViewFileReltn.properties";

    static {
        try (InputStream stream = new FileInputStream(getViewListFileLocString())) {
            viewList = new Properties();
            viewList.load(stream);
        } catch (IOException e) {
            logger.error("Error encountered loading config property file");
        }
        blockingViewList = Collections.synchronizedList(new ArrayList<>());
        for (String key : viewList.stringPropertyNames()) {
            String viewFileName = (String) viewList.get(key);
            ViewPojo view;
            if (viewFileName == null || viewFileName.isEmpty()) {
                view = new ViewPojo(key);
            } else {
                view = new ViewPojo(key, viewFileName);
            }
            blockingViewList.add(view);
        }
    }

    public static String getViewListFileLocString() {
        return ViewMEnvUtil.CONFIG_LOC + VIEW_LIST_NAME;
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
                && !ignoreFile(fileName);
    }

    public static boolean containsView(String viewName) {
        return getViewList().stream().anyMatch(v -> v.getName().equals(viewName));
    }

    public static boolean ignoreFile(String fileName) {
        return ViewMIgnoreList.ignoreFile(fileName);
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

    public static void sortFullViewList() {
        synchronized (blockingViewList) {
            List<ViewPojo> liveViewList = getLiveViewList();
            for (int i = 0, liveViewListSize = liveViewList.size(); i < liveViewListSize; i++) {
                ViewPojo viewPojo = liveViewList.get(i);
                if (viewPojo.getDependentViews() == null) {
                    continue;
                }
                for (ViewPojo dependent : viewPojo.getDependentViews()) {
                    boolean moved = false;
                    int d = getLiveViewList().indexOf(dependent);
                    if (d > i) {
                        //Move it to below the dependent view.
                        blockingViewList.remove(viewPojo);
                        blockingViewList.add(d, viewPojo);
                        moved = true;
                    }
                    if (moved) {
                        i--; // Check this index again.
                    }
                }
            }
        }
    }

    public static void writeToPropertyFile() {
//        String pathname = ViewMEnvUtil.CONFIG_LOC + "viewlist.test.properties";
        try {
            FileUtil.toProperties(c -> {
                for (ViewPojo view : getViewList()) {
                    if (view.getFileName().endsWith(view.getName() + ViewMConfig.getViewFileSuffix())) {
                        c.setProperty(view.getNameWithType(), "");
                    } else {
                        c.setProperty(view.getNameWithType(), new File(view.getFileName()).getName());
                    }
                }
            }, getViewListFileLocString()).save();
        } catch (ConfigurationException e) {
            logger.error("Error writing property file", e);
        }
    }
}
