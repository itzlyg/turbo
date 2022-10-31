package com.turbo.engine.bo;

public class FlowInstanceBO {
    private String flowInstanceId;
    private String flowDeployId;
    private Integer flStatus;

    public String getFlowInstanceId() {
        return flowInstanceId;
    }

    public void setFlowInstanceId(String flowInstanceId) {
        this.flowInstanceId = flowInstanceId;
    }

    public String getFlowDeployId() {
        return flowDeployId;
    }

    public void setFlowDeployId(String flowDeployId) {
        this.flowDeployId = flowDeployId;
    }

    public Integer getFlStatus() {
        return flStatus;
    }

    public void setFlStatus(Integer flStatus) {
        this.flStatus = flStatus;
    }
}
