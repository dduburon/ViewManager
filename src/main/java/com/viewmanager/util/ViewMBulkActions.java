package com.viewmanager.util;

import com.viewmanager.config.ViewMOrderedList;
import com.viewmanager.pojo.ViewPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;

public class ViewMBulkActions {
    final static Logger logger = LoggerFactory.getLogger(ViewMBulkActions.class);

    public static void createAllIgnoreErrors() {
        for (ViewPojo view : ViewMOrderedList.getViewList()) {
            try {
                ViewServiceUtil.getViewService().createView(view);
            } catch (UncategorizedSQLException | BadSqlGrammarException e) {
                //Ignore
                continue;
            } catch (Exception e) {
                logger.error("Unknown error from view {} ({})",view.getName(),view.getFileName(), e);
                continue;
            }
            logger.info("Successfully added view {} ({})",view.getName(),view.getFileName());
        }

    }
}
