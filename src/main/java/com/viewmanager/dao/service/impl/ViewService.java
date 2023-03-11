package com.viewmanager.dao.service.impl;

import com.viewmanager.dao.service.IViewService;
import com.viewmanager.dao.mapper.ViewMapper;
import com.viewmanager.util.SQLExceptionInterpreter;
import com.viewmanager.util.ViewFileUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.viewmanager.pojo.ViewPojo;

import javax.inject.Inject;
import java.util.List;

@Transactional
@Service
@Scope(proxyMode = ScopedProxyMode.INTERFACES)
public class ViewService implements IViewService {


    @Inject
    private ViewMapper viewMapper;

    public ViewService() {
    }

    public ViewService(ViewMapper viewMapper) {
        this.viewMapper = viewMapper;
    }

    @Override
    public List<ViewPojo> dropView(String view_name) {
        try {
            viewMapper.dropView(view_name);
        } catch (UncategorizedSQLException e) {
            List<ViewPojo> dependent = SQLExceptionInterpreter.interpret(e);
            if (dependent.isEmpty()) {

            }
            return dependent;
        }
        return null;
    }

    @Override
    public List<ViewPojo> dropView(ViewPojo view) {
        try {
            if (view.getType().equals(ViewPojo.Type.M)) {
                viewMapper.dropMatView(view.getName());
            } else {
                viewMapper.dropView(view.getName());
            }
        } catch (UncategorizedSQLException e ) {
            return SQLExceptionInterpreter.interpret(e);
        }
        return null;
    }

    @Override
    public List<ViewPojo> dropMatView(String view_name) {
        try {
            viewMapper.dropMatView(view_name);
        } catch (UncategorizedSQLException e) {
            return SQLExceptionInterpreter.interpret(e);
        }
        return null;
    }

    @Override
    public void createView(String view_name, String sql) {
        viewMapper.createView(view_name, sql);
    }

    @Override
    public void createMatView(String view_name, String sql) {
        viewMapper.createMatView(view_name, sql);
    }

    @Override
    public void createView(ViewPojo view, String sql) {
        if (view.getType().equals(ViewPojo.Type.M)) {
            viewMapper.createMatView(view.getName(), sql);
        } else {
            viewMapper.createView(view.getName(), sql);
        }
    }

    @Override
    public void createView(String view_name) {
        String sql = ViewFileUtil.getSQLFromViewFile(view_name);
        viewMapper.createView(view_name, sql);
    }

    @Override
    public void createView(ViewPojo view) {
        String sql = ViewFileUtil.getSQLFromViewFile(view);
        createView(view, sql);
    }

    @Override
    public List<String> getAllViewsFromDB() {
        return viewMapper.getAllViewsFromDB();
    }
}