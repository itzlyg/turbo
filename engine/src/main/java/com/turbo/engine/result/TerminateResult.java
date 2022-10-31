package com.turbo.engine.result;

import com.turbo.engine.common.ErrorEnum;

public class TerminateResult extends RuntimeResult {

    public TerminateResult(ErrorEnum errorEnum) {
        super(errorEnum);
    }

}
