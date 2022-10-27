package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;
import com.didiglobal.turbo.engine.runner.BaseTest;
import com.didiglobal.turbo.engine.util.EntityBuilder;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;


public class FlowDeploymentDAOTest extends BaseTest {

    @Resource
    FlowDeploymentDAO flowDeploymentDAO;

    @Test
    public void insert() {
        FlowDeploymentPO flowDeploymentPO = EntityBuilder.buildFlowDeploymentPO();
        flowDeploymentPO.setFlowDeployId("testFlowDeployId_" + System.currentTimeMillis());
        int result = flowDeploymentDAO.insert(flowDeploymentPO);
        Assert.assertTrue(result == 1);
    }

    @Test
    public void selectByDeployId() {
        FlowDeploymentPO flowDeploymentPO = EntityBuilder.buildFlowDeploymentPO();
        flowDeploymentPO.setFlowDeployId("testFlowDeployId_" + System.currentTimeMillis());
        flowDeploymentDAO.insert(flowDeploymentPO);
        String flowDeployId = flowDeploymentPO.getFlowDeployId();
        flowDeploymentPO = flowDeploymentDAO.selectByDeployId(flowDeployId);
        Assert.assertTrue(flowDeployId.equals(flowDeploymentPO.getFlowDeployId()));


    }

    @Test
    public void selectRecentByFlowModuleId() {
        FlowDeploymentPO flowDeploymentPO = EntityBuilder.buildFlowDeploymentPO();
        flowDeploymentPO.setFlowDeployId("testFlowDeployId_" + System.currentTimeMillis());
        flowDeploymentDAO.insert(flowDeploymentPO);
        FlowDeploymentPO flowDeploymentPONew = EntityBuilder.buildFlowDeploymentPO();
        String flowModuleId1 = flowDeploymentPO.getFlowModuleId();
        flowDeploymentPO.setFlowDeployId("testFlowDeployId_" + System.currentTimeMillis());
        flowDeploymentPONew.setFlowModuleId(flowModuleId1);
        flowDeploymentPONew.setFlowDeployId(flowDeploymentPONew.getFlowDeployId()+2);
        flowDeploymentDAO.insert(flowDeploymentPONew);
        FlowDeploymentPO flowDeploymentPORes = flowDeploymentDAO.selectRecentByFlowModuleId(flowModuleId1);
        Assert.assertTrue(flowDeploymentPONew.getFlowDeployId().equals(flowDeploymentPORes.getFlowDeployId()));



    }
}