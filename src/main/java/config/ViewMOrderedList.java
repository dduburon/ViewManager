package config;

import org.apache.log4j.Logger;
import pojo.ViewPojo;
import util.ViewMEnvUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class ViewMOrderedList {

    final static Logger logger = Logger.getLogger(ViewMOrderedList.class);
    private static Properties viewList;
    private static Collection<ViewPojo> blockingViewList;

    public static final String VIEW_LIST_NAME = "viewlist.properties";

    static {
        try (InputStream stream = new FileInputStream(ViewMEnvUtil.CONFIG_LOC + VIEW_LIST_NAME)) {
            viewList = new Properties();
            viewList.load(stream);
        } catch (IOException e) {
            logger.error("Error encoutered loading config property file");
        }
        blockingViewList = new ArrayBlockingQueue<>(viewList.keySet().size());
        for (Object key : viewList.keySet()) {
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

    public static List<ViewPojo> getViewList() {
        List<ViewPojo> copiedList = new ArrayList<>();
        for (ViewPojo viewPojo : blockingViewList) {
            copiedList.add(new ViewPojo(viewPojo));
        }
        return copiedList;
    }
}
