package com.didiglobal.turbo.engine.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("ei_flow_instance")
public class FlowInstance extends CommonEntity {
    private String flowInstanceId;
    private String flowDeployId;
    private String flowModuleId;
    private Integer status;

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

    public String getFlowModuleId() {
        return flowModuleId;
    }

    public void setFlowModuleId(String flowModuleId) {
        this.flowModuleId = flowModuleId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
