package com.viewmanager.exception;

import lombok.Getter;

import static com.viewmanager.exception.SQLExceptionInterpreter.DOESNT_EXISTS_ERROR_FORMAT;
import static com.viewmanager.exception.SQLExceptionInterpreter.getRegexFromException;

public class SQLDoesntExistsException implements ViewManagerIntelligenException {

    @Getter
    private String viewName;

    public SQLDoesntExistsException(Exception e) {
        this.viewName = getRegexFromException(e, DOESNT_EXISTS_ERROR_FORMAT);
    }

    @Override
    public String getType() {
        return "DOESNT_EXISTS";
    }
}
