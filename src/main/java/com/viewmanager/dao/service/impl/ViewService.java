package com.viewmanager.dao.service.impl;

import com.viewmanager.config.ViewMOrderedList;
import com.viewmanager.dao.service.IViewService;
import com.viewmanager.dao.mapper.ViewMapper;
import com.viewmanager.exception.*;
import com.viewmanager.util.ViewFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.viewmanager.pojo.ViewPojo;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
@Scope(proxyMode = ScopedProxyMode.INTERFACES)
public class ViewService implements IViewService {

    final static Logger logger = LoggerFactory.getLogger(ViewService.class);

    @Inject
    private ViewMapper viewMapper;

    public ViewService() {
    }

    public ViewService(ViewMapper viewMapper) {
        this.viewMapper = viewMapper;
    }

    @Override
    public List<ViewPojo> dropView(String view_name) {
        ViewPojo view = ViewMOrderedList.getViewPojoByName(view_name);
        return dropView(view);
    }

    @Override
    public List<ViewPojo> dropView(ViewPojo view) {
        try {
            if (view.getType().equals(ViewPojo.Type.M)) {
                viewMapper.dropMatView(view);
            } else {
                viewMapper.dropView(view);
            }
        } catch (RuntimeException e) {
            ViewManagerIntelligenException interpret = SQLExceptionInterpreter.interpret(view, e);
            if (interpret instanceof ViewDoesntExistsException) {
                if (!view.getName().equals(((ViewDoesntExistsException) interpret).getViewName())) {
                    throw new RuntimeException("Attempted to drop view: " + view.getName(),e);
                } else {
                    return null;
                }
            } else if (interpret instanceof SQLExceptionWithDependencies) {
                return ((SQLExceptionWithDependencies) interpret).getDependentViews();
            } else if (interpret instanceof ViewIsMaterializedException) {
                throw new ViewIsMaterializedException(e);
            }
        }
        logger.info("View '{}' successfully dropped.", view.getName());
        return null;
    }

    private List<ViewPojo> getDependenciesFromException(ViewPojo view, UncategorizedSQLException e) {
        ViewManagerIntelligenException smartException = SQLExceptionInterpreter.interpret(view, e);
        List<ViewPojo> dependent =  new ArrayList<>();
        if (smartException.getType().equals("DEPENDENCY")) {
            dependent = ((SQLExceptionWithDependencies) smartException).getDependentViews();
        }
        if(!dependent.isEmpty()) {
            return dependent;
        }
        return null;
    }

    @Override
    public List<ViewPojo> dropMatView(ViewPojo view) {
        try {
            viewMapper.dropMatView(view);
        } catch (UncategorizedSQLException e) {
            List<ViewPojo> dependent = getDependenciesFromException(view, e);
            if (dependent != null) return dependent;
        }
        return null;
    }

    @Override
    public void createMatView(String view_name, String sql) {
        viewMapper.createMatView(view_name, sql);
    }

    @Override
    public void createView(ViewPojo view, String sql) {
        logger.debug("Creating view '{}'.", view.getName());
        try {
            if (view.getType().equals(ViewPojo.Type.M)) {
                viewMapper.createMatView(view.getName(), sql);
            } else {
                viewMapper.createView(view.getName(), sql);
            }
        } catch (RuntimeException e) {
            ViewManagerIntelligenException interpret = SQLExceptionInterpreter.interpret(view, e);
            if (interpret instanceof ViewAlreadyExistsException) {
                if (!view.getName().equals(((ViewAlreadyExistsException) interpret).getViewName())) {
                    throw new RuntimeException("Attempted to create view: " + view.getName(),e);
                } else {
                    return; //Ignore
                }
            }
        }
        logger.info("View '{}' successfully created.", view.getName());
    }

    @Override
    public void createView(String view_name) {
        ViewPojo viewPojo = ViewMOrderedList.getViewPojoByName(view_name);
        createView(viewPojo);
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