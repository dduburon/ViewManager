package com.viewmanager.exception;


import com.viewmanager.pojo.ViewPojo;
import org.postgresql.util.PSQLException;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLExceptionInterpreter {

    public static final String DOESNT_EXISTS_ERROR_FORMAT = "ERROR:.* view \"(.*)\" does not exist";
    public static final String ALREADY_EXISTS_ERROR_FORMAT = "ERROR: relation \"(.*)\" already exists";
    public static final String MAT_VIEW_EXCEPTION = "ERROR: \"(.*)\" is not a view";
    public static final String DEPENDENCY_ERROR_FORMAT = "view (.*) depends on ";

    public static ViewManagerIntelligenException interpret(ViewPojo view, Exception e) {
        Throwable sqlException = e.getCause();
        if (sqlException != null && sqlException instanceof PSQLException) {
            if (hasErrorFormat((Exception) sqlException, DEPENDENCY_ERROR_FORMAT)) {
                return new SQLExceptionWithDependencies((SQLException) sqlException);
            } else if (hasErrorFormat((Exception) sqlException, ALREADY_EXISTS_ERROR_FORMAT)) {
                return new ViewAlreadyExistsException((SQLException) sqlException);
            } else if (hasErrorFormat((Exception) sqlException, DOESNT_EXISTS_ERROR_FORMAT)) {
                return new ViewDoesntExistsException((SQLException) sqlException);
            } else if (hasErrorFormat((Exception) sqlException, MAT_VIEW_EXCEPTION)) {
                return new ViewDoesntExistsException((SQLException) sqlException);
            }
        }
        throw new RuntimeException("Error on view: " + view.getName(), e);
    }

    public static boolean hasErrorFormat(Exception e, String errorFormat) {
        String message = e.getMessage();
        Pattern pattern = Pattern.compile(errorFormat);
        Matcher matcher = pattern.matcher(message);
        return matcher.find();
    }

    public static String getRegexFromException(Exception e, String errorFormat) {
        String message = e.getMessage();
        Pattern pattern = Pattern.compile(errorFormat);
        Matcher matcher = pattern.matcher(message);
        if(matcher.find()) {
            return matcher.group(1);
        }
        throw new RuntimeException("Expected to find view name, but did not. Pattern: " + errorFormat, e);
    }

//    private static ViewManagerIntelligenException interpretBadSQL(BadSqlGrammarException e) {
//        return null;
//    }
}
