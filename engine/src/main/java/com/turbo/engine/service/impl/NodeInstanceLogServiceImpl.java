package com.turbo.engine.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.turbo.engine.mapper.NodeInstanceLogMapper;
import com.turbo.engine.entity.NodeInstanceLog;
import com.turbo.engine.service.NodeInstanceLogService;
import org.springframework.stereotype.Service;

@Service
public class NodeInstanceLogServiceImpl extends ServiceImpl<NodeInstanceLogMapper, NodeInstanceLog> implements NodeInstanceLogService {
}
