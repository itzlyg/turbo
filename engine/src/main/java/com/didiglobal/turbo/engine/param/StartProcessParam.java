package com.didiglobal.turbo.engine.param;

import com.didiglobal.turbo.engine.model.InstanceDataModel;
import java.util.List;

public class StartProcessParam {
    private String flowModuleId;
    private String flowDeployId;
    private List<InstanceDataModel> variables;

    public String getFlowModuleId() {
        return flowModuleId;
    }

    public void setFlowModuleId(String flowModuleId) {
        this.flowModuleId = flowModuleId;
    }

    public String getFlowDeployId() {
        return flowDeployId;
    }

    public void setFlowDeployId(String flowDeployId) {
        this.flowDeployId = flowDeployId;
    }

    public List<InstanceDataModel> getVariables() {
        return variables;
    }

    public void setVariables(List<InstanceDataModel> variables) {
        this.variables = variables;
    }

}
