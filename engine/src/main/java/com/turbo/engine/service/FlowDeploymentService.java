package com.turbo.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.turbo.engine.entity.FlowDeployment;

public interface FlowDeploymentService extends IService<FlowDeployment> {

    FlowDeployment deployIdOrFlowModuleId(String deployId, String flowModuleId);
}
