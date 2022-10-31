package com.turbo.engine.exception;

import com.turbo.engine.common.ErrorEnum;

public class DefinitionException extends TurboException {

    public DefinitionException(int errNo, String errMsg) {
        super(errNo, errMsg);
    }

    public DefinitionException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
