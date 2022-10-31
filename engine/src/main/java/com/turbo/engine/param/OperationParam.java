package com.turbo.engine.param;

public class OperationParam extends CommonParam{
    private String operator;

    public OperationParam(String tenant, String caller) {
        super(tenant, caller);
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

}
