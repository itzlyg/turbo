package com.didiglobal.turbo.engine.param;

import com.didiglobal.turbo.engine.model.InstanceData;
import java.util.List;

public class StartProcessParam {
    private String flowModuleId;
    private String flowDeployId;
    private List<InstanceData> variables;

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

    public List<InstanceData> getVariables() {
        return variables;
    }

    public void setVariables(List<InstanceData> variables) {
        this.variables = variables;
    }

}
