package com.didiglobal.turbo.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.didiglobal.turbo.engine.mapper.FlowDeploymentMapper;
import com.didiglobal.turbo.engine.entity.FlowDeployment;
import com.didiglobal.turbo.engine.service.FlowDeploymentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

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
