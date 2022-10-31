package com.turbo.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.turbo.engine.mapper.NodeInstanceMapper;
import com.turbo.engine.entity.NodeInstance;
import com.turbo.engine.service.NodeInstanceService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NodeInstanceServiceImpl extends ServiceImpl<NodeInstanceMapper, NodeInstance> implements NodeInstanceService {

    protected static final Logger LOGGER = LoggerFactory.getLogger(NodeInstanceServiceImpl.class);

    @Resource
    private NodeInstanceMapper mapper;

    @Override
    public NodeInstance nodeInstanceId(String nodeInstanceId, String flowInstanceId) {
        // TODO 源码此处为 null
        return query(true, nodeInstanceId, null).get(0);
    }

    @Override
    public List<NodeInstance> byFlowInstanceId(String flowInstanceId, boolean asc) {
        return query(asc, null, flowInstanceId);
    }

    @Override
    public NodeInstance selectBySourceInstanceId(String nodeInstanceId, String sourceNodeInstanceId, String nodeKey) {
        LambdaQueryWrapper<NodeInstance> wrapper = new LambdaQueryWrapper<NodeInstance>()
                .eq(NodeInstance::getNodeInstanceId, nodeInstanceId)
                .eq(NodeInstance::getSourceNodeInstanceId, sourceNodeInstanceId)
                .eq(NodeInstance::getNodeKey, nodeKey);
        return getOne(wrapper);
    }

    public boolean insertOrUpdateList(List<NodeInstance> list) {
        if (CollectionUtils.isEmpty(list)) {
            LOGGER.warn("insertOrUpdateList: nodeInstanceList is empty.");
            return true;
        }
        List<NodeInstance> insertList = new ArrayList<>();
        List<NodeInstance> modifyList = new ArrayList<>();
        list.forEach(entity -> {
            if (entity.getId() == null) {
                insertList.add(entity);
            } else {
                NodeInstance instance = new NodeInstance();
                instance.setModifyTime(LocalDateTime.now());
                instance.setId(entity.getId());
                instance.setStatus(entity.getStatus());
                modifyList.add(instance);
            }
        });
        if (CollectionUtils.isNotEmpty(modifyList)) {
            updateBatchById(modifyList);
        }

        if (CollectionUtils.isEmpty(insertList)) {
            return true;
        }

        return saveBatch(insertList);
    }

    private List<NodeInstance> query (boolean asc, String nodeInstanceId, String flowInstanceId){
        LambdaQueryWrapper<NodeInstance> wrapper = new LambdaQueryWrapper<NodeInstance>()
                .eq(StringUtils.isNotBlank(flowInstanceId), NodeInstance::getFlowInstanceId, flowInstanceId)
                .eq(StringUtils.isNotBlank(nodeInstanceId), NodeInstance::getNodeInstanceId, nodeInstanceId)
                .orderBy(true, asc, NodeInstance::getId);
        return list(wrapper);
    }
}
