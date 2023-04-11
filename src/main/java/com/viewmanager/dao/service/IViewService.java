package com.viewmanager.dao.service;

import com.viewmanager.exception.ViewManagerIntelligenException;
import com.viewmanager.pojo.ViewPojo;

import java.util.List;

public interface IViewService {
    List<ViewPojo> dropView(String view_name);
    List<ViewPojo> dropView(ViewPojo view);

    List<ViewPojo> dropMatView(String view_name);

    void createMatView(String view_name, String sql);

    void createView(ViewPojo view, String sql);

    void createView(String view_name);

    void createView(ViewPojo view);

    List<String> getAllViewsFromDB();
}
