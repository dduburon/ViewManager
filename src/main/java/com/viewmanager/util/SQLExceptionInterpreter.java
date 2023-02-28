package com.viewmanager.util;

import com.viewmanager.config.ViewMOrderedList;
import com.viewmanager.pojo.ViewPojo;
import org.postgresql.util.PSQLException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class SQLExceptionInterpreter {

    public static List<ViewPojo> interpret(Exception e) {
        SQLExceptionInterpreter sei = new SQLExceptionInterpreter();
        return sei.parse(e);
    }

    private List<ViewPojo> parse(Exception e) {
        List<ViewPojo> dependentViews = new ArrayList<>();
        if (e.getCause() != null && e.getCause() instanceof PSQLException ) {
            String message = e.getCause().getMessage();
            String errorFormat = "view (.*) depends on ";
            Scanner scan = new Scanner(message);
            List<String> dependentViewStrList = scan.findAll(errorFormat)
                    .map(m -> m.group(1))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            for (String string : dependentViewStrList) {
                dependentViews.add(ViewMOrderedList.getViewPojoByName(string));
            }
        }
        if (dependentViews.isEmpty()) {
            //This shouldn't happen.
            throw new RuntimeException(e);
        }
        return dependentViews;
    }
}
