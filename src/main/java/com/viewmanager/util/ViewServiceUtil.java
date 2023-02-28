package com.viewmanager.util;

import com.viewmanager.dao.spring.ViewAppConfig;
import com.viewmanager.dao.service.IViewService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ViewServiceUtil {

    static ApplicationContext context = null;
    static IViewService viewService;


    public static IViewService getViewService() {
        if (context == null || viewService == null) {
            context = new AnnotationConfigApplicationContext(ViewAppConfig.class);
            viewService = context.getBean(IViewService.class);
        }
        return viewService;
    }
}
