package com.didiglobal.turbo.engine.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.didiglobal.turbo.engine.mapper.NodeInstanceLogMapper;
import com.didiglobal.turbo.engine.entity.NodeInstanceLog;
import com.didiglobal.turbo.engine.service.NodeInstanceLogService;
import org.springframework.stereotype.Service;

@Service
public class NodeInstanceLogServiceImpl extends ServiceImpl<NodeInstanceLogMapper, NodeInstanceLog> implements NodeInstanceLogService {
}
