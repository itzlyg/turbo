package com.didiglobal.turbo.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.didiglobal.turbo.engine.mapper.InstanceDataMapper;
import com.didiglobal.turbo.engine.entity.InstanceData;
import com.didiglobal.turbo.engine.service.InstanceDataService;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class InstanceDataServiceImpl extends ServiceImpl<InstanceDataMapper, InstanceData> implements InstanceDataService {

    @Resource
    private InstanceDataMapper mapper;

    @Override
    public InstanceData select(String flowInstanceId, String instanceDataId) {
        LambdaQueryWrapper<InstanceData> wrapper = new LambdaQueryWrapper<InstanceData>()
                .eq(StringUtils.isNotBlank(flowInstanceId), InstanceData::getFlowInstanceId, flowInstanceId)
                .eq(StringUtils.isNotBlank(instanceDataId), InstanceData::getInstanceDataId, instanceDataId);
        return getOne(wrapper);
    }
}
