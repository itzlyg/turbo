package com.turbo.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.turbo.engine.entity.FlowDefinition;

/**
* 流程定义表 服务类
* @Description
* @Copyright Copyright (c) 2022
* @author xieyubin
* @since 2022-10-31 15:07:51
*/
public interface FlowDefinitionService extends IService<FlowDefinition> {

    boolean updateByModelId (FlowDefinition definition);

    FlowDefinition byModelId (String modelId);
}