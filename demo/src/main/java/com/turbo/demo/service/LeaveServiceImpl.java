package com.turbo.demo.service;

import com.turbo.demo.util.Constant;
import com.turbo.demo.util.EntityBuilder;
import com.turbo.engine.engine.ProcessEngine;
import com.turbo.engine.model.InstanceDataModel;
import com.turbo.engine.param.CommitTaskParam;
import com.turbo.engine.param.CreateFlowParam;
import com.turbo.engine.param.DeployFlowParam;
import com.turbo.engine.param.RollbackTaskParam;
import com.turbo.engine.param.StartProcessParam;
import com.turbo.engine.param.UpdateFlowParam;
import com.turbo.engine.result.CommitTaskResult;
import com.turbo.engine.result.CreateFlowResult;
import com.turbo.engine.result.DeployFlowResult;
import com.turbo.engine.result.RollbackTaskResult;
import com.turbo.engine.result.StartProcessResult;
import com.turbo.engine.result.UpdateFlowResult;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author rick
 * @Date 2022/4/6 14:08
 * 先来了解一下常见的请假过程：
 * 小T是桔厂的员工，本周三有事想请假，提交了一个请假流程，根据公司规定，3天及以内的假期需要直属领导审批，超过3天需要间接领导审批。
 * <3
 * --->  用户节点（直属领导审批）--->
 * 开始节点 ---> 用户节点（输入请假天数）--->   排他网关                                 结束节点
 * --->  用户节点（间接领导审批）--->
 * >=3
 */
@Service
public class LeaveServiceImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeaveServiceImpl.class);

    @Resource
    private ProcessEngine processEngine;

    private CreateFlowParam createFlowParam;
    private CreateFlowResult createFlowResult;
    private UpdateFlowResult updateFlowResult;
    private DeployFlowResult deployFlowResult;

    public void run() {
        LOGGER.info("LeaveSOP Demo run:");

        LOGGER.info("LeaveSOP definition:");

        createFlow();

        updateFlow();

        deployFlow();

        LOGGER.info("LeaveSOP runtime:");

        startProcessToEnd();
    }

    private void createFlow() {
        createFlowParam = new CreateFlowParam(Constant.tenant, Constant.caller);
        createFlowParam.setFlowKey("person_time");
        createFlowParam.setFlowName("请假SOP");
        createFlowParam.setRemark("demo");
        createFlowParam.setOperator(Constant.operator);
        createFlowResult = processEngine.createFlow(createFlowParam);
        LOGGER.info("createFlow.||createFlowResult={}", createFlowResult);
    }

    private void updateFlow() {
        UpdateFlowParam updateFlowParam = new UpdateFlowParam(Constant.tenant, Constant.caller);
        updateFlowParam.setFlowModel(EntityBuilder.buildLeaveFlowModelStr());
        updateFlowParam.setFlowModuleId(createFlowResult.getFlowModuleId());
        updateFlowResult = processEngine.updateFlow(updateFlowParam);
        LOGGER.info("updateFlow.||updateFlowResult={}", updateFlowResult);
    }

    private void deployFlow() {
        DeployFlowParam param = new DeployFlowParam(Constant.tenant, Constant.caller);
        param.setFlowModuleId(createFlowResult.getFlowModuleId());
        deployFlowResult = processEngine.deployFlow(param);
        LOGGER.info("deployFlow.||deployFlowResult={}", deployFlowResult);
    }

    private void startProcessToEnd() {
        StartProcessResult startProcess = startProcess();
        CommitTaskResult commitTaskResult = inputTime(startProcess);
        RollbackTaskResult rollbackTaskResult = rollbackToInputTime(commitTaskResult);
        CommitTaskResult result = inputTimeAgain(rollbackTaskResult);
        commitCompleteProcess(result);
    }

    // 用户拉起请假sop
    private StartProcessResult startProcess() {
        StartProcessParam startProcessParam = new StartProcessParam();
        startProcessParam.setFlowDeployId(deployFlowResult.getFlowDeployId());
        List<InstanceDataModel> variables = new ArrayList<>();
        variables.add(new InstanceDataModel("user_name", "请假人名字"));
        startProcessParam.setVariables(variables);
        StartProcessResult startProcessResult = processEngine.startProcess(startProcessParam);

        LOGGER.info("startProcess.||startProcessResult={}", startProcessResult);
        return startProcessResult;
    }

    // 输入请假天数
    private CommitTaskResult inputTime(StartProcessResult startProcessResult) {
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceDataModel> variables = new ArrayList<>();
        variables.add(new InstanceDataModel("user_name", "请假人名字"));
        variables.add(new InstanceDataModel("n", 1));
        commitTaskParam.setVariables(variables);

        CommitTaskResult commitTaskResult = processEngine.commitTask(commitTaskParam);
        LOGGER.info("inputTime.||commitTaskResult={}", commitTaskResult);
        return commitTaskResult;
    }

    // 请假撤回
    private RollbackTaskResult rollbackToInputTime(CommitTaskResult commitTaskResult) {
        RollbackTaskParam rollbackTaskParam = new RollbackTaskParam();
        rollbackTaskParam.setFlowInstanceId(commitTaskResult.getFlowInstanceId());
        rollbackTaskParam.setTaskInstanceId(commitTaskResult.getActiveTaskInstance().getNodeInstanceId());
        RollbackTaskResult rollbackTaskResult = processEngine.rollbackTask(rollbackTaskParam);

        LOGGER.info("rollbackToInputTime.||rollbackTaskResult={}", rollbackTaskResult);
        return rollbackTaskResult;
    }

    // 填写请假天数
    private CommitTaskResult inputTimeAgain(RollbackTaskResult rollbackTaskResult) {
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(rollbackTaskResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(rollbackTaskResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceDataModel> variables = new ArrayList<>();
        variables.add(new InstanceDataModel("user_name", "请假人名字"));
        variables.add(new InstanceDataModel("n", 5));
        commitTaskParam.setVariables(variables);

        CommitTaskResult commitTaskResult = processEngine.commitTask(commitTaskParam);
        LOGGER.info("inputTimeAgain.||commitTaskResult={}", commitTaskResult);
        return commitTaskResult;
    }

    // BadCase：已完成流程，继续执行流程会失败。
    private CommitTaskResult commitCompleteProcess(CommitTaskResult commitTaskResult) {
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(commitTaskResult.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(commitTaskResult.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceDataModel> variables = new ArrayList<>();
        variables.add(new InstanceDataModel("user_name", "请假人名字"));
        variables.add(new InstanceDataModel("n", 4));
        commitTaskParam.setVariables(variables);

        CommitTaskResult result = processEngine.commitTask(commitTaskParam);
        LOGGER.info("inputTimeBadCase.||CommitTaskResult={}", result);
        return result;
    }

}
