package com.turbo.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.turbo.engine.entity.FlowInstance;

public interface FlowInstanceService extends IService<FlowInstance> {

    FlowInstance selectByFlowInstanceId(String flowInstanceId);

    void updateStatus(String flowInstanceId, int status);

    void updateStatusById (String id, int status);
}
