package com.didiglobal.turbo.engine.result;

import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.model.InstanceData;
import java.util.List;

public class InstanceDataListResult extends CommonResult {
    private List<InstanceData> variables;

    public InstanceDataListResult(ErrorEnum errorEnum) {
        super(errorEnum);
    }

    public List<InstanceData> getVariables() {
        return variables;
    }

    public void setVariables(List<InstanceData> variables) {
        this.variables = variables;
    }

}
