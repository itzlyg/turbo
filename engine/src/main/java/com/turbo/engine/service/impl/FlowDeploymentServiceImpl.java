package com.turbo.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.turbo.engine.mapper.FlowDeploymentMapper;
import com.turbo.engine.entity.FlowDeployment;
import com.turbo.engine.service.FlowDeploymentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;


/**
 * 流程部署表 服务实现类
 * @Description
 * @Copyright Copyright (c) 2022
 * @author xieyubin
 * @since 2022-10-31 15:07:51
 */
@Service
public class FlowDeploymentServiceImpl extends ServiceImpl<FlowDeploymentMapper, FlowDeployment> implements FlowDeploymentService {
    @Override
    public FlowDeployment deployIdOrFlowModuleId(String deployId, String flowModuleId) {
        LambdaQueryWrapper<FlowDeployment> wrapper = new LambdaQueryWrapper<FlowDeployment>()
                .eq(StringUtils.isNotBlank(deployId), FlowDeployment::getFlowDeployId, deployId)
                .eq(StringUtils.isNotBlank(flowModuleId), FlowDeployment::getFlowModuleId, flowModuleId)
                .orderByDesc(FlowDeployment::getId);
        return getOne(wrapper);
    }
}
