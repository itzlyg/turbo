package com.turbo.engine.processor;

import com.turbo.engine.bo.ElementInstanceBO;
import com.turbo.engine.bo.FlowInfoBO;
import com.turbo.engine.bo.FlowInstanceBO;
import com.turbo.engine.bo.NodeInstanceBO;
import com.turbo.engine.common.ErrorEnum;
import com.turbo.engine.common.FlowElementTypeEnum;
import com.turbo.engine.common.FlowInstanceStatus;
import com.turbo.engine.common.NodeInstanceStatus;
import com.turbo.engine.common.ProcessStatus;
import com.turbo.engine.common.RuntimeContext;
import com.turbo.engine.entity.FlowDeployment;
import com.turbo.engine.entity.FlowInstance;
import com.turbo.engine.entity.InstanceData;
import com.turbo.engine.entity.NodeInstance;
import com.turbo.engine.exception.ProcessException;
import com.turbo.engine.exception.ReentrantException;
import com.turbo.engine.exception.TurboException;
import com.turbo.engine.executor.FlowExecutor;
import com.turbo.engine.model.FlowElement;
import com.turbo.engine.model.InstanceDataModel;
import com.turbo.engine.param.CommitTaskParam;
import com.turbo.engine.param.RollbackTaskParam;
import com.turbo.engine.param.StartProcessParam;
import com.turbo.engine.result.CommitTaskResult;
import com.turbo.engine.result.ElementInstanceListResult;
import com.turbo.engine.result.InstanceDataListResult;
import com.turbo.engine.result.NodeInstanceListResult;
import com.turbo.engine.result.NodeInstanceResult;
import com.turbo.engine.result.RollbackTaskResult;
import com.turbo.engine.result.RuntimeResult;
import com.turbo.engine.result.StartProcessResult;
import com.turbo.engine.result.TerminateResult;
import com.turbo.engine.service.FlowDeploymentService;
import com.turbo.engine.service.FlowInstanceService;
import com.turbo.engine.service.InstanceDataService;
import com.turbo.engine.service.NodeInstanceService;
import com.turbo.engine.util.FlowModelUtil;
import com.turbo.engine.util.InstanceDataUtil;
import com.turbo.engine.util.JsonUtil;
import com.turbo.engine.validator.ParamValidator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class RuntimeProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeProcessor.class);

    @Resource
    private FlowDeploymentService flowDeploymentService;

    @Resource
    private FlowInstanceService flowInstanceService;

    @Resource
    private NodeInstanceService nodeInstanceService;

    @Resource
    private InstanceDataService instanceDataService;

    @Resource
    private FlowExecutor flowExecutor;

    ////////////////////////////////////////startProcess////////////////////////////////////////

    public StartProcessResult startProcess(StartProcessParam startProcessParam) {
        RuntimeContext runtimeContext = null;
        try {
            //1.param validate
            ParamValidator.validate(startProcessParam);

            //2.getFlowInfo
            FlowInfoBO flowInfo = getFlowInfo(startProcessParam);

            //3.init context for runtime
            runtimeContext = buildStartProcessContext(flowInfo, startProcessParam.getVariables());

            //4.process
            flowExecutor.execute(runtimeContext);

            //5.build result
            return buildStartProcessResult(runtimeContext);
        } catch (TurboException e) {
            if (!ErrorEnum.isSuccess(e.getErrNo())) {
                LOGGER.warn("startProcess ProcessException.||startProcessParam={}||runtimeContext={}, ",
                        startProcessParam, runtimeContext, e);
            }
            return buildStartProcessResult(runtimeContext, e);
        }
    }

    private FlowInfoBO getFlowInfo(StartProcessParam startProcessParam) throws ProcessException {
        if (StringUtils.isNotBlank(startProcessParam.getFlowDeployId())) {
            return getFlowInfoByFlowDeployId(startProcessParam.getFlowDeployId());
        } else {
            return getFlowInfoByFlowModuleId(startProcessParam.getFlowModuleId());
        }
    }

    /**
     * Init runtimeContext for startProcess:
     * 1.flowInfo: flowDeployId, flowModuleId, tenantId, flowModel(FlowElementList)
     * 2.variables: inputDataList fr. param
     */
    private RuntimeContext buildStartProcessContext(FlowInfoBO flowInfo, List<InstanceDataModel> variables) {
        return buildRuntimeContext(flowInfo, variables);
    }

    private StartProcessResult buildStartProcessResult(RuntimeContext runtimeContext) {
        StartProcessResult startProcessResult = new StartProcessResult();
        BeanUtils.copyProperties(runtimeContext, startProcessResult);
        return (StartProcessResult) fillRuntimeResult(startProcessResult, runtimeContext);
    }

    private StartProcessResult buildStartProcessResult(RuntimeContext runtimeContext, TurboException e) {
        StartProcessResult startProcessResult = new StartProcessResult();
        BeanUtils.copyProperties(runtimeContext, startProcessResult);
        return (StartProcessResult) fillRuntimeResult(startProcessResult, runtimeContext, e);
    }

    ////////////////////////////////////////commit////////////////////////////////////////

    public CommitTaskResult commit(CommitTaskParam commitTaskParam) {
        RuntimeContext runtimeContext = null;
        try {
            //1.param validate
            ParamValidator.validate(commitTaskParam);

            //2.get flowInstance
            FlowInstanceBO flowInstanceBO = getFlowInstanceBO(commitTaskParam.getFlowInstanceId());

            //3.check status
            if (flowInstanceBO.getFlStatus() == FlowInstanceStatus.TERMINATED) {
                LOGGER.warn("commit failed: flowInstance has been completed.||commitTaskParam={}", commitTaskParam);
                throw new ProcessException(ErrorEnum.COMMIT_REJECTRD);
            }
            if (flowInstanceBO.getFlStatus() == FlowInstanceStatus.COMPLETED) {
                LOGGER.warn("commit: reentrant process.||commitTaskParam={}", commitTaskParam);
                throw new ReentrantException(ErrorEnum.REENTRANT_WARNING);
            }
            String flowDeployId = flowInstanceBO.getFlowDeployId();

            //4.getFlowInfo
            FlowInfoBO flowInfo = getFlowInfoByFlowDeployId(flowDeployId);

            //5.init runtimeContext
            runtimeContext = buildCommitContext(commitTaskParam, flowInfo, flowInstanceBO.getFlStatus());

            //6.process
            flowExecutor.commit(runtimeContext);

            //7.build result
            return buildCommitTaskResult(runtimeContext);
        } catch (TurboException e) {
            if (!ErrorEnum.isSuccess(e.getErrNo())) {
                LOGGER.warn("commit ProcessException.||commitTaskParam={}||runtimeContext={}, ", commitTaskParam, runtimeContext, e);
            }
            return buildCommitTaskResult(runtimeContext, e);
        }
    }

    private RuntimeContext buildCommitContext(CommitTaskParam commitTaskParam, FlowInfoBO flowInfo, int flowInstanceStatus) {
        //1. set flow info
        RuntimeContext runtimeContext = buildRuntimeContext(flowInfo, commitTaskParam.getVariables());

        //2. init flowInstance with flowInstanceId
        runtimeContext.setFlowInstanceId(commitTaskParam.getFlowInstanceId());
        runtimeContext.setFlowInstanceStatus(flowInstanceStatus);

        //3. set suspendNodeInstance with taskInstance in param
        NodeInstanceBO suspendNodeInstance = new NodeInstanceBO();
        suspendNodeInstance.setNodeInstanceId(commitTaskParam.getTaskInstanceId());
        runtimeContext.setSuspendNodeInstance(suspendNodeInstance);

        return runtimeContext;
    }

    private CommitTaskResult buildCommitTaskResult(RuntimeContext runtimeContext) {
        CommitTaskResult commitTaskResult = new CommitTaskResult();
        return (CommitTaskResult) fillRuntimeResult(commitTaskResult, runtimeContext);
    }

    private CommitTaskResult buildCommitTaskResult(RuntimeContext runtimeContext, TurboException e) {
        CommitTaskResult commitTaskResult = new CommitTaskResult();
        return (CommitTaskResult) fillRuntimeResult(commitTaskResult, runtimeContext, e);
    }

    ////////////////////////////////////////rollback////////////////////////////////////////

    /**
     * Rollback: rollback node process from param.taskInstance to the last taskInstance to suspend
     *
     * @param rollbackTaskParam: flowInstanceId + taskInstanceId(nodeInstanceId)
     * @return rollbackTaskResult: runtimeResult, flowInstanceId + activeTaskInstance(nodeInstanceId,nodeKey,status) + dataMap
     * @throws Exception
     */
    public RollbackTaskResult rollback(RollbackTaskParam rollbackTaskParam) {
        RuntimeContext runtimeContext = null;
        try {
            //1.param validate
            ParamValidator.validate(rollbackTaskParam);

            //2.get flowInstance
            FlowInstanceBO flowInstanceBO = getFlowInstanceBO(rollbackTaskParam.getFlowInstanceId());
            //3.check status
            if (flowInstanceBO.getFlStatus() != FlowInstanceStatus.RUNNING) {
                LOGGER.warn("rollback failed: invalid status to rollback.||rollbackTaskParam={}||status={}",
                        rollbackTaskParam, flowInstanceBO.getFlStatus());
                throw new ProcessException(ErrorEnum.ROLLBACK_REJECTRD);
            }
            String flowDeployId = flowInstanceBO.getFlowDeployId();

            //4.getFlowInfo
            FlowInfoBO flowInfo = getFlowInfoByFlowDeployId(flowDeployId);

            //5.init runtimeContext
            runtimeContext = buildRollbackContext(rollbackTaskParam, flowInfo, flowInstanceBO.getFlStatus());

            //6.process
            flowExecutor.rollback(runtimeContext);

            //7.build result
            return buildRollbackTaskResult(runtimeContext);
        } catch (TurboException e) {
            if (!ErrorEnum.isSuccess(e.getErrNo())) {
                LOGGER.warn("rollback ProcessException.||rollbackTaskParam={}||runtimeContext={}, ", rollbackTaskParam, runtimeContext, e);
            }
            return buildRollbackTaskResult(runtimeContext, e);
        }
    }

    private RuntimeContext buildRollbackContext(RollbackTaskParam rollbackTaskParam, FlowInfoBO flowInfo, int flowInstanceStatus) {
        //1. set flow info
        RuntimeContext runtimeContext = buildRuntimeContext(flowInfo);

        //2. init flowInstance with flowInstanceId
        runtimeContext.setFlowInstanceId(rollbackTaskParam.getFlowInstanceId());
        runtimeContext.setFlowInstanceStatus(flowInstanceStatus);

        //3. set suspendNodeInstance with taskInstance in param
        NodeInstanceBO suspendNodeInstance = new NodeInstanceBO();
        suspendNodeInstance.setNodeInstanceId(rollbackTaskParam.getTaskInstanceId());
        runtimeContext.setSuspendNodeInstance(suspendNodeInstance);

        return runtimeContext;
    }

    private RollbackTaskResult buildRollbackTaskResult(RuntimeContext runtimeContext) {
        RollbackTaskResult rollbackTaskResult = new RollbackTaskResult();
        return (RollbackTaskResult) fillRuntimeResult(rollbackTaskResult, runtimeContext);
    }

    private RollbackTaskResult buildRollbackTaskResult(RuntimeContext runtimeContext, TurboException e) {
        RollbackTaskResult rollbackTaskResult = new RollbackTaskResult();
        return (RollbackTaskResult) fillRuntimeResult(rollbackTaskResult, runtimeContext, e);
    }

    ////////////////////////////////////////terminate////////////////////////////////////////

    public TerminateResult terminateProcess(String flowInstanceId) {
        TerminateResult terminateResult;
        try {
            int flowInstanceStatus;

            FlowInstance flowInstancePO = flowInstanceService.selectByFlowInstanceId(flowInstanceId);
            if (flowInstancePO.getFlStatus() == FlowInstanceStatus.COMPLETED) {
                LOGGER.warn("terminateProcess: flowInstance is completed.||flowInstanceId={}", flowInstanceId);
                flowInstanceStatus = FlowInstanceStatus.COMPLETED;
            } else {
                flowInstanceService.updateStatusById(flowInstancePO.getId(), FlowInstanceStatus.TERMINATED);
                flowInstanceStatus = FlowInstanceStatus.TERMINATED;
            }

            terminateResult = new TerminateResult(ErrorEnum.SUCCESS);
            terminateResult.setFlowInstanceId(flowInstanceId);
            terminateResult.setStatus(flowInstanceStatus);
        } catch (Exception e) {
            LOGGER.error("terminateProcess exception.||flowInstanceId={}, ", flowInstanceId, e);
            terminateResult = new TerminateResult(ErrorEnum.SYSTEM_ERROR);
            terminateResult.setFlowInstanceId(flowInstanceId);
        }
        return terminateResult;
    }

    ////////////////////////////////////////getHistoryUserTaskList////////////////////////////////////////

    public NodeInstanceListResult getHistoryUserTaskList(String flowInstanceId) {

        //1.get nodeInstanceList by flowInstanceId order by id desc
        List<NodeInstance> historyNodeInstanceList = getDescHistoryNodeInstanceList(flowInstanceId);

        //2.init result
        NodeInstanceListResult historyListResult = new NodeInstanceListResult(ErrorEnum.SUCCESS);
        historyListResult.setNodeInstanceList(new ArrayList<>());

        try {

            if (CollectionUtils.isEmpty(historyNodeInstanceList)) {
                LOGGER.warn("getHistoryUserTaskList: historyNodeInstanceList is empty.||flowInstanceId={}", flowInstanceId);
                return historyListResult;
            }

            //3.get flow info
            String flowDeployId = historyNodeInstanceList.get(0).getFlowDeployId();
            Map<String, FlowElement> flowElementMap = getFlowElementMap(flowDeployId);

            //4.pick out userTask and build result
            List<com.turbo.engine.bo.NodeInstance> userTaskList = historyListResult.getNodeInstanceList();//empty list

            for (NodeInstance nodeInstancePO : historyNodeInstanceList) {
                //ignore noneffective nodeInstance
                if (!isEffectiveNodeInstance(nodeInstancePO.getFlStatus())) {
                    continue;
                }

                //ignore un-userTask instance
                if (!isUserTask(nodeInstancePO.getNodeKey(), flowElementMap)) {
                    continue;
                }

                //build effective userTask instance
                com.turbo.engine.bo.NodeInstance nodeInstance = new com.turbo.engine.bo.NodeInstance();
                //set instanceId & status
                BeanUtils.copyProperties(nodeInstancePO, nodeInstance);

                //set ElementModel info
                FlowElement flowElement = FlowModelUtil.getFlowElement(flowElementMap, nodeInstancePO.getNodeKey());
                nodeInstance.setModelKey(flowElement.getKey());
                nodeInstance.setModelName(FlowModelUtil.getElementName(flowElement));
                if (MapUtils.isNotEmpty(flowElement.getProperties())) {
                    nodeInstance.setProperties(flowElement.getProperties());
                } else {
                    nodeInstance.setProperties(new HashMap<>());
                }
                userTaskList.add(nodeInstance);
            }
        } catch (ProcessException e) {
            historyListResult.setErrCode(e.getErrNo());
            historyListResult.setErrMsg(e.getErrMsg());
        }
        return historyListResult;
    }

    private Map<String, FlowElement> getFlowElementMap(String flowDeployId) throws ProcessException {
        FlowInfoBO flowInfo = getFlowInfoByFlowDeployId(flowDeployId);
        String flowModel = flowInfo.getFlowModel();
        return FlowModelUtil.getFlowElementMap(flowModel);
    }

    private boolean isEffectiveNodeInstance(int status) {
        return status == NodeInstanceStatus.COMPLETED || status == NodeInstanceStatus.ACTIVE;
    }

    private boolean isUserTask(String nodeKey, Map<String, FlowElement> flowElementMap) throws ProcessException {
        if (!flowElementMap.containsKey(nodeKey)) {
            LOGGER.warn("isUserTask: invalid nodeKey which is not in flowElementMap.||nodeKey={}||flowElementMap={}",
                    nodeKey, flowElementMap);
            throw new ProcessException(ErrorEnum.GET_NODE_FAILED);
        }
        FlowElement flowElement = flowElementMap.get(nodeKey);
        return flowElement.getType() == FlowElementTypeEnum.USER_TASK.getCode();
    }

    ////////////////////////////////////////getHistoryElementList////////////////////////////////////////

    public ElementInstanceListResult getHistoryElementList(String flowInstanceId) {
        //1.getHistoryNodeList
        List<NodeInstance> historyNodeInstanceList = getHistoryNodeInstanceList(flowInstanceId);

        //2.init
        ElementInstanceListResult elementInstanceListResult = new ElementInstanceListResult(ErrorEnum.SUCCESS);
        elementInstanceListResult.setElementInstanceList(new ArrayList<>());

        try {
            if (CollectionUtils.isEmpty(historyNodeInstanceList)) {
                LOGGER.warn("getHistoryElementList: historyNodeInstanceList is empty.||flowInstanceId={}", flowInstanceId);
                return elementInstanceListResult;
            }

            //3.get flow info
            String flowDeployId = historyNodeInstanceList.get(0).getFlowDeployId();
            Map<String, FlowElement> flowElementMap = getFlowElementMap(flowDeployId);

            //4.calculate elementInstanceMap: key=elementKey, value(lasted)=ElementInstance(elementKey, status)
            List<ElementInstanceBO> elementInstanceList = elementInstanceListResult.getElementInstanceList();
            for (NodeInstance nodeInstancePO : historyNodeInstanceList) {
                String nodeKey = nodeInstancePO.getNodeKey();
                String sourceNodeKey = nodeInstancePO.getSourceNodeKey();
                int nodeStatus = nodeInstancePO.getFlStatus();

                //4.1 build the source sequenceFlow instance
                if (StringUtils.isNotBlank(sourceNodeKey)) {
                    FlowElement sourceFlowElement = FlowModelUtil.getSequenceFlow(flowElementMap, sourceNodeKey, nodeKey);
                    if (sourceFlowElement == null) {
                        LOGGER.error("getHistoryElementList failed: sourceFlowElement is null."
                                + "||nodeKey={}||sourceNodeKey={}||flowElementMap={}", nodeKey, sourceNodeKey, flowElementMap);
                        throw new ProcessException(ErrorEnum.MODEL_UNKNOWN_ELEMENT_KEY);
                    }

                    //build ElementInstance
                    int sourceSequenceFlowStatus = nodeStatus;
                    if (nodeStatus == NodeInstanceStatus.ACTIVE) {
                        sourceSequenceFlowStatus = NodeInstanceStatus.COMPLETED;
                    }
                    ElementInstanceBO sequenceFlowInstance = new ElementInstanceBO(sourceFlowElement.getKey(), sourceSequenceFlowStatus);
                    elementInstanceList.add(sequenceFlowInstance);
                }

                //4.2 build nodeInstance
                ElementInstanceBO nodeInstance = new ElementInstanceBO(nodeKey, nodeStatus);
                elementInstanceList.add(nodeInstance);
            }
        } catch (ProcessException e) {
            elementInstanceListResult.setErrCode(e.getErrNo());
            elementInstanceListResult.setErrMsg(e.getErrMsg());
        }
        return elementInstanceListResult;
    }

    private List<NodeInstance> getHistoryNodeInstanceList(String flowInstanceId) {
        return nodeInstanceService.byFlowInstanceId(flowInstanceId, true);
    }

    private List<NodeInstance> getDescHistoryNodeInstanceList(String flowInstanceId) {
        return nodeInstanceService.byFlowInstanceId(flowInstanceId, false);
    }

    public NodeInstanceResult getNodeInstance(String flowInstanceId, String nodeInstanceId) {
        NodeInstanceResult nodeInstanceResult = new NodeInstanceResult();
        try {
            NodeInstance nodeInstancePO = nodeInstanceService.nodeInstanceId(nodeInstanceId, flowInstanceId);
            String flowDeployId = nodeInstancePO.getFlowDeployId();
            Map<String, FlowElement> flowElementMap = getFlowElementMap(flowDeployId);
            com.turbo.engine.bo.NodeInstance nodeInstance = new com.turbo.engine.bo.NodeInstance();
            BeanUtils.copyProperties(nodeInstancePO, nodeInstance);
            FlowElement flowElement = FlowModelUtil.getFlowElement(flowElementMap, nodeInstancePO.getNodeKey());
            nodeInstance.setModelKey(flowElement.getKey());
            nodeInstance.setModelName(FlowModelUtil.getElementName(flowElement));
            if (MapUtils.isNotEmpty(flowElement.getProperties())) {
                nodeInstance.setProperties(flowElement.getProperties());
            } else {
                nodeInstance.setProperties(new HashMap<>());
            }
            nodeInstanceResult.setNodeInstance(nodeInstance);
            nodeInstanceResult.setErrCode(ErrorEnum.SUCCESS.getErrNo());
            nodeInstanceResult.setErrMsg(ErrorEnum.SUCCESS.getErrMsg());
        } catch (ProcessException e) {
            nodeInstanceResult.setErrCode(e.getErrNo());
            nodeInstanceResult.setErrMsg(e.getErrMsg());
        }
        return nodeInstanceResult;
    }

    ////////////////////////////////////////getInstanceData////////////////////////////////////////
    public InstanceDataListResult getInstanceData(String flowInstanceId) {
        InstanceData instanceDataPO = instanceDataService.select(flowInstanceId, null);

        List<InstanceDataModel> instanceDataList = JsonUtil.toBeans(instanceDataPO.getInstanceData(), InstanceDataModel.class);
        if (CollectionUtils.isEmpty(instanceDataList)) {
            instanceDataList =  new ArrayList<>();
        }

        InstanceDataListResult instanceDataListResult = new InstanceDataListResult(ErrorEnum.SUCCESS);
        instanceDataListResult.setVariables(instanceDataList);
        return instanceDataListResult;
    }

    ////////////////////////////////////////common////////////////////////////////////////

    private FlowInfoBO getFlowInfoByFlowDeployId(String flowDeployId) throws ProcessException {

        FlowDeployment flowDeploymentPO = flowDeploymentService.deployIdOrFlowModuleId(flowDeployId, null);
        if (flowDeploymentPO == null) {
            LOGGER.warn("getFlowInfoByFlowDeployId failed.||flowDeployId={}", flowDeployId);
            throw new ProcessException(ErrorEnum.GET_FLOW_DEPLOYMENT_FAILED);
        }
        FlowInfoBO flowInfo = new FlowInfoBO();
        BeanUtils.copyProperties(flowDeploymentPO, flowInfo);

        return flowInfo;
    }

    private FlowInfoBO getFlowInfoByFlowModuleId(String flowModuleId) throws ProcessException {
        //get from db directly
        FlowDeployment flowDeploymentPO = flowDeploymentService.deployIdOrFlowModuleId(null, flowModuleId);
        if (flowDeploymentPO == null) {
            LOGGER.warn("getFlowInfoByFlowModuleId failed.||flowModuleId={}", flowModuleId);
            throw new ProcessException(ErrorEnum.GET_FLOW_DEPLOYMENT_FAILED);
        }

        FlowInfoBO flowInfo = new FlowInfoBO();
        BeanUtils.copyProperties(flowDeploymentPO, flowInfo);

        return flowInfo;
    }

    private FlowInstanceBO getFlowInstanceBO(String flowInstanceId) throws ProcessException {
        //get from db
        FlowInstance flowInstancePO = flowInstanceService.selectByFlowInstanceId(flowInstanceId);
        if (flowInstancePO == null) {
            LOGGER.warn("getFlowInstancePO failed: cannot find flowInstancePO from db.||flowInstanceId={}", flowInstanceId);
            throw new ProcessException(ErrorEnum.GET_FLOW_INSTANCE_FAILED);
        }
        FlowInstanceBO flowInstanceBO = new FlowInstanceBO();
        BeanUtils.copyProperties(flowInstancePO, flowInstanceBO);

        return flowInstanceBO;
    }

    private RuntimeContext buildRuntimeContext(FlowInfoBO flowInfo) {
        RuntimeContext runtimeContext = new RuntimeContext();
        BeanUtils.copyProperties(flowInfo, runtimeContext);
        runtimeContext.setFlowElementMap(FlowModelUtil.getFlowElementMap(flowInfo.getFlowModel()));
        return runtimeContext;
    }

    private RuntimeContext buildRuntimeContext(FlowInfoBO flowInfo, List<InstanceDataModel> variables) {
        RuntimeContext runtimeContext = buildRuntimeContext(flowInfo);
        Map<String, InstanceDataModel> instanceDataMap = InstanceDataUtil.getInstanceDataMap(variables);
        runtimeContext.setInstanceDataMap(instanceDataMap);
        return runtimeContext;
    }

    private RuntimeResult fillRuntimeResult(RuntimeResult runtimeResult, RuntimeContext runtimeContext) {
        if (runtimeContext.getProcessStatus() == ProcessStatus.SUCCESS) {
            return fillRuntimeResult(runtimeResult, runtimeContext, ErrorEnum.SUCCESS);
        }
        return fillRuntimeResult(runtimeResult, runtimeContext, ErrorEnum.FAILED);
    }

    private RuntimeResult fillRuntimeResult(RuntimeResult runtimeResult, RuntimeContext runtimeContext, ErrorEnum errorEnum) {
        return fillRuntimeResult(runtimeResult, runtimeContext, errorEnum.getErrNo(), errorEnum.getErrMsg());
    }

    private RuntimeResult fillRuntimeResult(RuntimeResult runtimeResult, RuntimeContext runtimeContext, TurboException e) {
        return fillRuntimeResult(runtimeResult, runtimeContext, e.getErrNo(), e.getErrMsg());
    }

    private RuntimeResult fillRuntimeResult(RuntimeResult runtimeResult, RuntimeContext runtimeContext, int errNo, String errMsg) {
        runtimeResult.setErrCode(errNo);
        runtimeResult.setErrMsg(errMsg);

        if (runtimeContext != null) {
            runtimeResult.setFlowInstanceId(runtimeContext.getFlowInstanceId());
            runtimeResult.setStatus(runtimeContext.getFlowInstanceStatus());
            runtimeResult.setActiveTaskInstance(buildActiveTaskInstance(runtimeContext.getSuspendNodeInstance(), runtimeContext));
            runtimeResult.setVariables(InstanceDataUtil.getInstanceDataList(runtimeContext.getInstanceDataMap()));
        }
        return runtimeResult;
    }

    private com.turbo.engine.bo.NodeInstance buildActiveTaskInstance(NodeInstanceBO nodeInstanceBO, RuntimeContext runtimeContext) {
        com.turbo.engine.bo.NodeInstance activeNodeInstance = new com.turbo.engine.bo.NodeInstance();
        BeanUtils.copyProperties(nodeInstanceBO, activeNodeInstance);
        activeNodeInstance.setModelKey(nodeInstanceBO.getNodeKey());
        FlowElement flowElement = runtimeContext.getFlowElementMap().get(nodeInstanceBO.getNodeKey());
        activeNodeInstance.setModelName(FlowModelUtil.getElementName(flowElement));
        activeNodeInstance.setProperties(flowElement.getProperties());

        return activeNodeInstance;
    }
}
