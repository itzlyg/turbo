package com.turbo.engine.result;

import java.util.Date;

public class FlowModuleResult extends CommonResult {
    private String flowModuleId;
    private String flowName;
    private String flowKey;
    private String flowModel;
    private Integer status;
    private String remark;
    private String tenant;
    private String caller;
    private String operator;
    private Date modifyTime;

    public String getFlowModuleId() {
        return flowModuleId;
    }

    public void setFlowModuleId(String flowModuleId) {
        this.flowModuleId = flowModuleId;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getFlowKey() {
        return flowKey;
    }

    public void setFlowKey(String flowKey) {
        this.flowKey = flowKey;
    }

    public String getFlowModel() {
        return flowModel;
    }

    public void setFlowModel(String flowModel) {
        this.flowModel = flowModel;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}
