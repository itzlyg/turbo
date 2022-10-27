package com.didiglobal.turbo.engine.executor;

import com.didiglobal.turbo.engine.common.RuntimeContext;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.model.FlowModel;
import com.didiglobal.turbo.engine.model.InstanceData;
import com.didiglobal.turbo.engine.runner.BaseTest;
import com.didiglobal.turbo.engine.util.EntityBuilder;
import com.didiglobal.turbo.engine.util.FlowModelUtil;
import com.didiglobal.turbo.engine.util.JsonUtil;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExclusiveGatewayExecutorTest extends BaseTest {

    @Resource
    private ExecutorFactory executorFactory;

    private ExclusiveGatewayExecutor exclusiveGatewayExecutor;

    private RuntimeContext runtimeContext;

    @Before
    public void initExclusiveGatewayExecutor() {
        List<FlowElement> flowElementList = EntityBuilder.buildFlowElementList();

        FlowModel flowModel = new FlowModel();
        flowModel.setFlowElementList(flowElementList);
        Map<String, FlowElement> flowElementMap = FlowModelUtil.getFlowElementMap(JsonUtil.toJson(flowModel));

        FlowElement exclusiveGateway = FlowModelUtil.getFlowElement(flowElementMap, "exclusiveGateway1");

        runtimeContext = EntityBuilder.buildRuntimeContext();
        Map<String, InstanceData> instanceDataMap = Maps.newHashMap();
        InstanceData instanceDataA = new InstanceData("a", 2);
        InstanceData instanceDataB = new InstanceData("b", 1);
        instanceDataMap.put(instanceDataA.getKey(), instanceDataA);
        instanceDataMap.put(instanceDataB.getKey(), instanceDataB);
        runtimeContext.setInstanceDataMap(instanceDataMap);
        runtimeContext.setCurrentNodeModel(exclusiveGateway);

        try {
            exclusiveGatewayExecutor = (ExclusiveGatewayExecutor) executorFactory
                    .getElementExecutor(exclusiveGateway);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    @Test
    public void testDoExecute() {
        try {
            exclusiveGatewayExecutor.doExecute(runtimeContext);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    @Test
    public void testGetExecuteExecutor() {
        try {
            exclusiveGatewayExecutor.getExecuteExecutor(runtimeContext);
            String modelKey = runtimeContext.getCurrentNodeModel().getKey();
            Assert.assertTrue("userTask2".equals(modelKey));
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }
}
