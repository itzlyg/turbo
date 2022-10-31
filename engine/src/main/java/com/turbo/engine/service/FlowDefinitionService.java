package com.turbo.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.turbo.engine.entity.FlowDefinition;

public interface FlowDefinitionService extends IService<FlowDefinition> {

    boolean updateByModelId (FlowDefinition definition);

    FlowDefinition byModelId (String modelId);
}
