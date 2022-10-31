package com.turbo.engine.engine.impl;

import com.turbo.engine.engine.ProcessEngine;
import com.turbo.engine.param.CommitTaskParam;
import com.turbo.engine.param.CreateFlowParam;
import com.turbo.engine.param.DeployFlowParam;
import com.turbo.engine.param.GetFlowModuleParam;
import com.turbo.engine.param.RollbackTaskParam;
import com.turbo.engine.param.StartProcessParam;
import com.turbo.engine.param.UpdateFlowParam;
import com.turbo.engine.processor.DefinitionProcessor;
import com.turbo.engine.processor.RuntimeProcessor;
import com.turbo.engine.result.CommitTaskResult;
import com.turbo.engine.result.CreateFlowResult;
import com.turbo.engine.result.DeployFlowResult;
import com.turbo.engine.result.ElementInstanceListResult;
import com.turbo.engine.result.FlowModuleResult;
import com.turbo.engine.result.InstanceDataListResult;
import com.turbo.engine.result.NodeInstanceListResult;
import com.turbo.engine.result.NodeInstanceResult;
import com.turbo.engine.result.RollbackTaskResult;
import com.turbo.engine.result.StartProcessResult;
import com.turbo.engine.result.TerminateResult;
import com.turbo.engine.result.UpdateFlowResult;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ProcessEngineImpl implements ProcessEngine {

    @Resource
    private DefinitionProcessor definitionProcessor;

    @Resource
    private RuntimeProcessor runtimeProcessor;

    @Override
    public CreateFlowResult createFlow(CreateFlowParam createFlowParam) {
        return definitionProcessor.create(createFlowParam);
    }

    @Override
    public UpdateFlowResult updateFlow(UpdateFlowParam updateFlowParam) {
        return definitionProcessor.update(updateFlowParam);
    }

    @Override
    public DeployFlowResult deployFlow(DeployFlowParam deployFlowParam) {
        return definitionProcessor.deploy(deployFlowParam);
    }

    @Override
    public FlowModuleResult getFlowModule(GetFlowModuleParam getFlowModuleParam) {
        return definitionProcessor.getFlowModule(getFlowModuleParam);
    }

    @Override
    public StartProcessResult startProcess(StartProcessParam startProcessParam) {
        return runtimeProcessor.startProcess(startProcessParam);
    }

    @Override
    public CommitTaskResult commitTask(CommitTaskParam commitTaskParam) {
        return runtimeProcessor.commit(commitTaskParam);
    }

    @Override
    public RollbackTaskResult rollbackTask(RollbackTaskParam rollbackTaskParam) {
        return runtimeProcessor.rollback(rollbackTaskParam);
    }

    @Override
    public TerminateResult terminateProcess(String flowInstanceId) {
        return runtimeProcessor.terminateProcess(flowInstanceId);
    }

    @Override
    public NodeInstanceListResult getHistoryUserTaskList(String flowInstanceId) {
        return runtimeProcessor.getHistoryUserTaskList(flowInstanceId);
    }

    @Override
    public ElementInstanceListResult getHistoryElementList(String flowInstanceId) {
        return runtimeProcessor.getHistoryElementList(flowInstanceId);
    }

    @Override
    public InstanceDataListResult getInstanceData(String flowInstanceId) {
        return runtimeProcessor.getInstanceData(flowInstanceId);
    }

    @Override
    public NodeInstanceResult getNodeInstance(String flowInstanceId, String nodeInstanceId) {
        return runtimeProcessor.getNodeInstance(flowInstanceId, nodeInstanceId);
    }
}
