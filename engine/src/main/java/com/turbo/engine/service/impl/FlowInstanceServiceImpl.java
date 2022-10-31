package com.turbo.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.turbo.engine.mapper.FlowInstanceMapper;
import com.turbo.engine.entity.FlowInstance;
import com.turbo.engine.service.FlowInstanceService;
import java.time.LocalDateTime;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class FlowInstanceServiceImpl extends ServiceImpl<FlowInstanceMapper, FlowInstance> implements FlowInstanceService {

    @Resource
    private FlowInstanceMapper mapper;

    @Override
    public FlowInstance selectByFlowInstanceId(String flowInstanceId) {
        LambdaQueryWrapper<FlowInstance> wrapper = new LambdaQueryWrapper<FlowInstance>()
                .eq(FlowInstance::getFlowInstanceId, flowInstanceId);
        return getOne(wrapper);
    }
    @Override
    public void updateStatus(String flowInstanceId, int status) {
        FlowInstance flow = selectByFlowInstanceId(flowInstanceId);
        if (flow != null) {
            updateStatus(flow.getId(), status);
        }

    }

    @Override
    public void updateStatusById(String id, int status) {
        FlowInstance updateFlow = new FlowInstance();
        updateFlow.setStatus(status);
        updateFlow.setModifyTime(LocalDateTime.now());
        updateFlow.setId(id);
        mapper.updateById(updateFlow);
    }

}
