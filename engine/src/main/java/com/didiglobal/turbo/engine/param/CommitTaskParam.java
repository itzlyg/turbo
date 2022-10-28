package com.didiglobal.turbo.engine.param;

import com.didiglobal.turbo.engine.model.InstanceData;
import java.util.List;

public class CommitTaskParam extends RuntimeTaskParam {
    private List<InstanceData> variables;

    public List<InstanceData> getVariables() {
        return variables;
    }

    public void setVariables(List<InstanceData> variables) {
        this.variables = variables;
    }

}
