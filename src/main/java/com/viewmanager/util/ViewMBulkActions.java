package com.viewmanager.util;

import com.viewmanager.config.ViewMDependencyCache;
import com.viewmanager.config.ViewMOrderedList;
import com.viewmanager.exception.SQLExceptionInterpreter;
import com.viewmanager.exception.ViewManagerIntelligenException;
import com.viewmanager.pojo.ViewPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;

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
        String paramPrefix = "view=";
        if (!viewParam.startsWith(paramPrefix)) {
            logger.error("View name not set.");
            throw new RuntimeException();
        }
        String viewStr = viewParam.replace(paramPrefix, "");
        ViewPojo view = ViewMOrderedList.getViewPojoByName(viewStr);
        if (view == null) {
            logger.error("View not found by name '{}'", viewStr);
            throw new RuntimeException();
        }
        return view;
    }
}
