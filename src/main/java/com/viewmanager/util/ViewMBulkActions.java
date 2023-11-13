package com.viewmanager.util;

import com.viewmanager.config.ViewMDependencyCache;
import com.viewmanager.config.ViewMOrderedList;
import com.viewmanager.pojo.ViewPojo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class ViewMBulkActions {
    final static Logger logger = LoggerFactory.getLogger(ViewMBulkActions.class);

    public static void createAllIgnoreErrors() {
        List<ViewPojo> viewList = ViewMOrderedList.getViewList();
        for (ViewPojo view : viewList) {
            ViewServiceUtil.getViewService().createView(view);
        }
    }

    public static void uninstallView(String viewParam) {
        ViewPojo view = verifyViewGiven(viewParam);
        List<ViewPojo> dependencies = ViewMDependencyCache.getCachedDependencies(view);
        if(dependencies != null && !dependencies.isEmpty()) {
            Collections.reverse(dependencies);
            for (ViewPojo dependency : dependencies) {
                ViewServiceUtil.getViewService().dropView(dependency);
            }
        }
        ViewServiceUtil.getViewService().dropView(view);
    }

    public static void installView(String viewParam) {
        ViewPojo view = verifyViewGiven(viewParam);
        List<ViewPojo> dependencies = ViewMDependencyCache.getCachedDependencies(view);
        ViewServiceUtil.getViewService().createView(view);
        if (dependencies != null && !dependencies.isEmpty()) {
            for (ViewPojo dependency : dependencies) {
                ViewServiceUtil.getViewService().createView(dependency);
            }
        }
    }

    private static ViewPojo verifyViewGiven(String viewParam) {
        if (StringUtils.isEmpty(viewParam)) {
            logger.error("View name not set.");
            throw new RuntimeException();
        }
        ViewPojo view = ViewMOrderedList.getViewPojoByName(viewParam);
        if (view == null) {
            logger.error("View not found by name '{}'", viewParam);
            throw new RuntimeException();
        }
        return view;
    }
}
