package com.didiglobal.turbo.engine.param;

import com.didiglobal.turbo.engine.model.InstanceDataModel;
import java.util.List;

public class CommitTaskParam extends RuntimeTaskParam {
    private List<InstanceDataModel> variables;

    public List<InstanceDataModel> getVariables() {
        return variables;
    }

    public void setVariables(List<InstanceDataModel> variables) {
        this.variables = variables;
    }

}
