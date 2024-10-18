package com.viewmanager.exception;

import lombok.Getter;

import static com.viewmanager.exception.SQLExceptionInterpreter.MAT_VIEW_EXCEPTION;
import static com.viewmanager.exception.SQLExceptionInterpreter.getRegexFromException;

public class ViewIsMaterializedException extends RuntimeException implements ViewManagerIntelligenException {

    @Getter
    private String viewName;

    public ViewIsMaterializedException(Exception e) {
        this.viewName = getRegexFromException(e, MAT_VIEW_EXCEPTION);
    }

    @Override
    public String getType() {
        return "MAT_VIEW";
    }
}
