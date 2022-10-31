package com.turbo.engine.result;

import com.turbo.engine.bo.NodeInstance;
import com.turbo.engine.common.ErrorEnum;
import com.turbo.engine.model.InstanceDataModel;
import java.util.List;

public class RuntimeResult extends CommonResult {
    private String flowInstanceId;
    private int status;
    private NodeInstance activeTaskInstance;
    private List<InstanceDataModel> variables;

    public RuntimeResult() {
        super();
    }

    public RuntimeResult(ErrorEnum errorEnum) {
        super(errorEnum);
    }

    public String getFlowInstanceId() {
        return flowInstanceId;
    }

    public void setFlowInstanceId(String flowInstanceId) {
        this.flowInstanceId = flowInstanceId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public NodeInstance getActiveTaskInstance() {
        return activeTaskInstance;
    }

    public void setActiveTaskInstance(NodeInstance activeTaskInstance) {
        this.activeTaskInstance = activeTaskInstance;
    }

    public List<InstanceDataModel> getVariables() {
        return variables;
    }

    public void setVariables(List<InstanceDataModel> variables) {
        this.variables = variables;
    }

}
