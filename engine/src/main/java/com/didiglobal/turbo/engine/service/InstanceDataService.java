package com.didiglobal.turbo.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.didiglobal.turbo.engine.entity.InstanceData;

public interface InstanceDataService extends IService<InstanceData> {

    InstanceData select(String flowInstanceId, String instanceDataId);
}
