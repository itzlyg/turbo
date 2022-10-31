package com.turbo.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.turbo.engine.entity.NodeInstance;
import java.util.List;

public interface NodeInstanceService extends IService<NodeInstance> {
    boolean insertOrUpdateList(List<NodeInstance> list);
    NodeInstance nodeInstanceId (String nodeInstanceId, String flowInstanceId);
    List<NodeInstance> byFlowInstanceId(String flowInstanceId, boolean asc);

    NodeInstance selectBySourceInstanceId (String nodeInstanceId, String sourceNodeInstanceId, String nodeKey);
}
