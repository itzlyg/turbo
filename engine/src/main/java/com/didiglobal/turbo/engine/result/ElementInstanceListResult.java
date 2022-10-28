package com.didiglobal.turbo.engine.result;

import com.didiglobal.turbo.engine.bo.ElementInstanceBO;
import com.didiglobal.turbo.engine.common.ErrorEnum;
import java.util.List;

public class ElementInstanceListResult extends CommonResult {
    private List<ElementInstanceBO> elementInstanceList;

    public ElementInstanceListResult(ErrorEnum errorEnum) {
        super(errorEnum);
    }

    public List<ElementInstanceBO> getElementInstanceList() {
        return elementInstanceList;
    }

    public void setElementInstanceList(List<ElementInstanceBO> elementInstanceList) {
        this.elementInstanceList = elementInstanceList;
    }

}
