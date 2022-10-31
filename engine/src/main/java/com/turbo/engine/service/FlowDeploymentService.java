package com.turbo.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.turbo.engine.entity.FlowDeployment;

/**
* 流程部署表 服务类
* @Description
* @Copyright Copyright (c) 2022
* @author xieyubin
* @since 2022-10-31 15:07:51
*/
public interface FlowDeploymentService extends IService<FlowDeployment> {

    FlowDeployment deployIdOrFlowModuleId(String deployId, String flowModuleId);
}
