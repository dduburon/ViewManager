package com.viewmanager.util;

import com.viewmanager.config.ViewMDependencyCache;
import com.viewmanager.config.ViewMOrderedList;
import com.viewmanager.pojo.ViewPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;

public class ViewMSorter {

    final static Logger logger = LoggerFactory.getLogger(ViewMSorter.class);

    public static void sortViewRegistry() {
        calcViewDependency();
        sortBasedOnDependencies();// a Quick sort
        fullSort(); // Should be a full sort
        ViewMOrderedList.writeToPropertyFile();
        ViewMDependencyCache.writeToPropertyFile();
    }

    private static void sortBasedOnDependencies() {
        ViewMOrderedList.sortViewList(Comparator.comparingInt((ViewPojo a) -> a.getDependentViews() == null ? 0 : a.getDependentViews().size()));
    }

    private static void calcViewDependency() {
        List<ViewPojo> viewList = ViewMOrderedList.getViewList();
        for (ViewPojo curView : viewList) {
            try {
                updateDependencies(curView);
            } catch (Exception e) {
                try {
                    // Try to recreate the view
                    ViewServiceUtil.getViewService().createView(curView);
                } catch (Exception e2) {
                    logger.error(String.format("Something went wrong when testing the drop of view %s (%s)",
                            curView.getName(), curView.getFileName()));
                    e.addSuppressed(e2);
                    throw e;
                }
            }
        }
    }

    private static void updateDependencies(ViewPojo curView) {
        List<ViewPojo> dependedBy = ViewServiceUtil.getViewService().dropView(curView);
        if (dependedBy == null || dependedBy.isEmpty()) {
            ViewServiceUtil.getViewService().createView(curView);
        } else {
            curView.setDependentViews(dependedBy);
            ViewMOrderedList.updateDependencies(curView);
        }
    }

    private static void fullSort() {
        ViewMOrderedList.sortFullViewList();
    }
}
