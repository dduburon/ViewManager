package com.viewmanager.exception;

import lombok.Getter;

import static com.viewmanager.exception.SQLExceptionInterpreter.ALREADY_EXISTS_ERROR_FORMAT;
import static com.viewmanager.exception.SQLExceptionInterpreter.getRegexFromException;

public class SQLAlreadyExistsException implements ViewManagerIntelligenException {

    @Getter
    private String viewName;

    public SQLAlreadyExistsException(Exception e) {
        this.viewName = getRegexFromException(e, ALREADY_EXISTS_ERROR_FORMAT);
    }

    @Override
    public String getType() {
        return "ALREADY_EXISTS";
    }
}
