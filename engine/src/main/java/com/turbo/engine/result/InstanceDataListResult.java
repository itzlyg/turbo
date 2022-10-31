package com.turbo.engine.result;

import com.turbo.engine.common.ErrorEnum;
import com.turbo.engine.model.InstanceDataModel;
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
