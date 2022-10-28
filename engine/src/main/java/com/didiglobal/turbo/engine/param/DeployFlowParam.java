package com.didiglobal.turbo.engine.param;

public class DeployFlowParam extends OperationParam {
    private String flowModuleId;

    public DeployFlowParam(String tenant, String caller) {
        super(tenant, caller);
    }

    public String getFlowModuleId() {
        return flowModuleId;
    }

    public void setFlowModuleId(String flowModuleId) {
        this.flowModuleId = flowModuleId;
    }

}
