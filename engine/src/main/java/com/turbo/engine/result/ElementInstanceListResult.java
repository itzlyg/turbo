package com.turbo.engine.result;

import com.turbo.engine.bo.ElementInstanceBO;
import com.turbo.engine.common.ErrorEnum;
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
