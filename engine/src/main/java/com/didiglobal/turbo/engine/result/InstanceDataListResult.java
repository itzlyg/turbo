package com.didiglobal.turbo.engine.result;

import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.model.InstanceDataModel;
import java.util.List;

public class InstanceDataListResult extends CommonResult {
    private List<InstanceDataModel> variables;

    public InstanceDataListResult(ErrorEnum errorEnum) {
        super(errorEnum);
    }

    public List<InstanceDataModel> getVariables() {
        return variables;
    }

    public void setVariables(List<InstanceDataModel> variables) {
        this.variables = variables;
    }

}
