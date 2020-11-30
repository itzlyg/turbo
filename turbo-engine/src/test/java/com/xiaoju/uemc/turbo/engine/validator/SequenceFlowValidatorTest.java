package com.xiaoju.uemc.turbo.engine.validator;

import com.xiaoju.uemc.turbo.engine.exception.ModelException;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SequenceFlowValidatorTest extends BaseTest {

    @Resource
    SequenceFlowValidator sequenceFlowValidator;

    /**
     * Test sequenceFlow's checkIncoming, while incoming is normal.
     *
     */
    @Test
    public void checkIncomingAccess() {
        FlowElement sequenceFlow = EntityBuilder.buildSequenceFlow();
        Map<String, FlowElement> flowElementMap = new HashMap<>();
        flowElementMap.put(sequenceFlow.getKey(), sequenceFlow);
        boolean access = false;
        try {
            sequenceFlowValidator.checkIncoming(flowElementMap, sequenceFlow);
            access = true;
            Assert.assertTrue(access == true);
        } catch (ModelException e) {
            e.printStackTrace();
            Assert.assertTrue(access = true);
        }
    }


    /**
     * Test sequenceFlow's checkIncoming, while incoming  is too much.
     *
     */
    @Test
    public void checkTooMuchIncoming() {
        FlowElement sequenceFlow = EntityBuilder.buildSequenceFlow();
        List<String> sfIncomings = new ArrayList<>();
        sfIncomings.add("userTask2");
        sfIncomings.add("userTask1");
        sequenceFlow.setIncoming(sfIncomings);
        Map<String, FlowElement> flowElementMap = new HashMap<>();
        flowElementMap.put(sequenceFlow.getKey(), sequenceFlow);
        boolean access = false;
        try {
            sequenceFlowValidator.checkIncoming(flowElementMap, sequenceFlow);
            access = true;
            Assert.assertTrue(access == false);
        } catch (ModelException e) {
            e.printStackTrace();
            Assert.assertTrue(access == false);
        }
    }

    /**
     * Test sequenceFlow's checkOutgoing, while outgoing is normal.
     *
     */
    @Test
    public void checkOutgoingAccess() {

        FlowElement sequenceFlow = EntityBuilder.buildSequenceFlow();
        Map<String, FlowElement> flowElementMap = new HashMap<>();
        flowElementMap.put(sequenceFlow.getKey(), sequenceFlow);
        boolean access = false;
        try {
            sequenceFlowValidator.checkOutgoing(flowElementMap, sequenceFlow);
            access = true;
            Assert.assertTrue(access == true);
        } catch (ModelException e) {
            e.printStackTrace();
            Assert.assertTrue(access == true);
        }
    }

    /**
     * Test sequenceFlow's outgoing, while outgoing is lack.
     *
     */
    @Test
    public void checkWithoutOutgoing() {

        FlowElement sequenceFlow = EntityBuilder.buildSequenceFlow();
        sequenceFlow.setOutgoing(null);
        Map<String, FlowElement> flowElementMap = new HashMap<>();
        flowElementMap.put(sequenceFlow.getKey(), sequenceFlow);
        boolean access = false;
        try {
            sequenceFlowValidator.checkOutgoing(flowElementMap, sequenceFlow);
            access = true;
            Assert.assertTrue(access == false);
        } catch (ModelException e) {
            e.printStackTrace();
        }
    }
}