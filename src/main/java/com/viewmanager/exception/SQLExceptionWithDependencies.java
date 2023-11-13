package com.viewmanager.exception;

import com.viewmanager.config.ViewMOrderedList;
import com.viewmanager.pojo.ViewPojo;
import lombok.Getter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

import static com.viewmanager.exception.SQLExceptionInterpreter.DEPENDENCY_ERROR_FORMAT;

public class SQLExceptionWithDependencies implements ViewManagerIntelligenException {

    @Getter
    List<ViewPojo> dependentViews = new ArrayList<>();

    public SQLExceptionWithDependencies(SQLException e) {
        parse(e);
    }

    private void parse(SQLException e) {
        String message = e.getMessage();
        Scanner scan = new Scanner(message);
        List<String> dependentViewStrList = scan.findAll(DEPENDENCY_ERROR_FORMAT)
                .map(m -> m.group(1))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        for (String string : dependentViewStrList) {
            dependentViews.add(ViewMOrderedList.getViewPojoByName(string));
        }
        if (dependentViews.isEmpty()) {
            //This shouldn't happen.
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getType() {
        return "DEPENDENCY";
    }
}
