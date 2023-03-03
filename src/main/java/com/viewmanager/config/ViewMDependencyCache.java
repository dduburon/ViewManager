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
import java.util.stream.Collectors;

public class ViewMDependencyCache {

    final static Logger logger = LoggerFactory.getLogger(ViewMOrderedList.class);

    private static Properties cache;
    public static final String VIEW_DEPEND_CACHE_NAME = "ViewDependencies.properties";

    public static List<ViewPojo> getCachedDependencies(String viewName) {
        return getCachedDependencies(ViewMOrderedList.getViewPojoByName(viewName));
    }
    public static List<ViewPojo> getCachedDependencies(ViewPojo view) {
        if (view.getDependentViews() != null && !view.getDependentViews().isEmpty()) {
            return view.getDependentViews();
        }
        List<ViewPojo> dependencies = null;
        String rawDep = getDependecyCache().getProperty(view.getName());
        if (rawDep != null && !rawDep.isEmpty()) {
            dependencies = new ArrayList<>();
            for (String dependName : rawDep.split(",")) {
                dependencies.add(ViewMOrderedList.getViewPojoByName(dependName));
            }
            dependencies = sortDependencies(dependencies);
        }
        return dependencies;
    }

    private static List<ViewPojo> sortDependencies(List<ViewPojo> dependenciesOrig) {
        List<ViewPojo> newOrder = new ArrayList<>(dependenciesOrig);
        Collections.sort(newOrder, Comparator.comparing(v -> ViewMOrderedList.getViewList().indexOf(v)));
        return newOrder;
    }

    private static String viewDependencyCacheLocation() {
        return ViewMEnvUtil.CONFIG_LOC + VIEW_DEPEND_CACHE_NAME;
    }

    private static Properties getDependecyCache() {
        if (cache == null) {
            File cacheFile = new File(viewDependencyCacheLocation());
            if (!cacheFile.exists()) {
                FileUtil.touchFile(cacheFile);
            }
            try (InputStream stream = new FileInputStream(cacheFile)) {
                cache = new Properties();
                cache.load(stream);
            } catch (IOException e) {
                logger.error("Error encountered loading dependency cache property file");
            }
        }
        return cache;
    }

    public static void writeToPropertyFile() {
//        String pathname = ViewMEnvUtil.CONFIG_LOC + "viewlist.test.properties";
        try {
            FileUtil.toProperties(c -> {
                for (ViewPojo view : ViewMOrderedList.getViewList()) {
                    String viewName = view.getName();
                    List<ViewPojo> dependsList = getCachedDependencies(viewName);
                    String depends = null;
                    if (dependsList != null && !dependsList.isEmpty()) {
                        depends = dependsList.stream().map(v -> v.getName()).collect(Collectors.joining(","));
                    }
                    c.setProperty(viewName, depends);
                }
            }, viewDependencyCacheLocation()).save();
        } catch (ConfigurationException e) {
            logger.error("Error writing property file", e);
        }
    }
}
