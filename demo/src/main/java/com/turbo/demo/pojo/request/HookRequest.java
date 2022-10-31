package com.turbo.demo.pojo.request;

public class HookRequest {
    private String flowInstanceId;
    private String hookInfoParam;

    public String getFlowInstanceId() {
        return flowInstanceId;
    }

    public void setFlowInstanceId(String flowInstanceId) {
        this.flowInstanceId = flowInstanceId;
    }

    public String getHookInfoParam() {
        return hookInfoParam;
    }

    public void setHookInfoParam(String hookInfoParam) {
        this.hookInfoParam = hookInfoParam;
    }
}
