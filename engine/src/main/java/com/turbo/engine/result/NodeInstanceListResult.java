package com.turbo.engine.result;

import com.turbo.engine.bo.NodeInstance;
import com.turbo.engine.common.ErrorEnum;
import java.util.List;

public class NodeInstanceListResult extends CommonResult {
    private List<NodeInstance> nodeInstanceList;

    public NodeInstanceListResult(ErrorEnum errorEnum) {
        super(errorEnum);
    }

    public List<NodeInstance> getNodeInstanceList() {
        return nodeInstanceList;
    }

    public void setNodeInstanceList(List<NodeInstance> nodeInstanceList) {
        this.nodeInstanceList = nodeInstanceList;
    }

}
