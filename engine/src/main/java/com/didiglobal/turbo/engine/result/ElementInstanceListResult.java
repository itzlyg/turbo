package com.didiglobal.turbo.engine.result;

import com.didiglobal.turbo.engine.bo.ElementInstance;
import com.didiglobal.turbo.engine.common.ErrorEnum;
import java.util.List;

public class ElementInstanceListResult extends CommonResult {
    private List<ElementInstance> elementInstanceList;

    public ElementInstanceListResult(ErrorEnum errorEnum) {
        super(errorEnum);
    }

    public List<ElementInstance> getElementInstanceList() {
        return elementInstanceList;
    }

    public void setElementInstanceList(List<ElementInstance> elementInstanceList) {
        this.elementInstanceList = elementInstanceList;
    }

}
