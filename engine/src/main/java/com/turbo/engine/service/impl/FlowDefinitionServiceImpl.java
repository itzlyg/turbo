package com.turbo.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.turbo.engine.mapper.FlowDefinitionMapper;
import com.turbo.engine.entity.FlowDefinition;
import com.turbo.engine.service.FlowDefinitionService;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class FlowDefinitionServiceImpl extends ServiceImpl<FlowDefinitionMapper, FlowDefinition> implements FlowDefinitionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowDefinitionServiceImpl.class);

    @Resource
    private FlowDefinitionMapper mapper;

    public boolean updateByModelId (FlowDefinition definition){
        if (null == definition) {
            LOGGER.warn("updateByModuleId failed: FlowDefinition is null.");
            return false;
        }
        String flowModuleId = definition.getFlowModuleId();
        if (StringUtils.isBlank(flowModuleId)) {
            LOGGER.warn("updateByModuleId failed: flowModuleId is empty.||FlowDefinition={}", definition);
            return false;
        }
        LambdaQueryWrapper<FlowDefinition> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(FlowDefinition::getFlowModuleId, flowModuleId);
        return mapper.update(definition, updateWrapper) > 0;
    }

    public FlowDefinition byModelId (String modelId){
        LambdaQueryWrapper<FlowDefinition> wrapper = new LambdaQueryWrapper<FlowDefinition>()
                .eq(FlowDefinition::getFlowModuleId, modelId);
        return getOne(wrapper);
    }
}
