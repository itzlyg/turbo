package com.turbo.engine.executor;

import com.turbo.engine.bo.NodeInstanceBO;
import com.turbo.engine.common.ErrorEnum;
import com.turbo.engine.common.FlowElementTypeEnum;
import com.turbo.engine.common.FlowInstanceStatus;
import com.turbo.engine.common.InstanceDataType;
import com.turbo.engine.common.NodeInstanceStatus;
import com.turbo.engine.common.NodeInstanceType;
import com.turbo.engine.common.ProcessStatus;
import com.turbo.engine.common.RuntimeContext;
import com.turbo.engine.entity.FlowInstance;
import com.turbo.engine.entity.InstanceData;
import com.turbo.engine.entity.NodeInstance;
import com.turbo.engine.entity.NodeInstanceLog;
import com.turbo.engine.exception.ProcessException;
import com.turbo.engine.exception.ReentrantException;
import com.turbo.engine.model.FlowElement;
import com.turbo.engine.model.InstanceDataModel;
import com.turbo.engine.service.FlowInstanceService;
import com.turbo.engine.util.FlowModelUtil;
import com.turbo.engine.util.InstanceDataUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class FlowExecutor extends RuntimeExecutor {

    @Resource
    private FlowInstanceService flowInstanceService;

    ////////////////////////////////////////execute////////////////////////////////////////

    @Override
    public void execute(RuntimeContext runtimeContext) throws ProcessException {
        int processStatus = ProcessStatus.SUCCESS;
        try {
            preExecute(runtimeContext);
            doExecute(runtimeContext);
        } catch (ProcessException pe) {
            if (!ErrorEnum.isSuccess(pe.getErrNo())) {
                processStatus = ProcessStatus.FAILED;
            }
            throw pe;
        } finally {
            runtimeContext.setProcessStatus(processStatus);
            postExecute(runtimeContext);
        }
    }

    /**
     * Fill runtimeContext:
     * 1. Generate flowInstanceId and insert FlowInstancePO into db
     * 2. Generate instanceDataId and insert InstanceDataPO into db
     * 3. Update runtimeContext: flowInstanceId, flowInstanceStatus, instanceDataId, nodeInstanceList, suspendNodeInstance
     *
     * @throws Exception
     */
    private void preExecute(RuntimeContext runtimeContext) throws ProcessException {
        //1.save FlowInstancePO into db
        FlowInstance flowInstancePO = saveFlowInstance(runtimeContext);

        //2.save InstanceDataPO into db
        String instanceDataId = saveInstanceData(flowInstancePO, runtimeContext.getInstanceDataMap());

        //3.update runtimeContext
        fillExecuteContext(runtimeContext, flowInstancePO.getFlowInstanceId(), instanceDataId);
    }

    private FlowInstance saveFlowInstance(RuntimeContext runtimeContext) throws ProcessException {
        FlowInstance flowInstancePO = buildFlowInstancePO(runtimeContext);
        boolean result = flowInstanceService.save(flowInstancePO);
        if (result) {
            return flowInstancePO;
        }
        LOGGER.warn("saveFlowInstancePO: insert failed.||flowInstancePO={}", flowInstancePO);
        throw new ProcessException(ErrorEnum.SAVE_FLOW_INSTANCE_FAILED);
    }

    private FlowInstance buildFlowInstancePO(RuntimeContext runtimeContext) {
        FlowInstance flowInstancePO = new FlowInstance();
        // copy flow info
        BeanUtils.copyProperties(runtimeContext, flowInstancePO);
        // generate flowInstanceId
        flowInstancePO.setFlowInstanceId(genId());
        flowInstancePO.setFlStatus(FlowInstanceStatus.RUNNING);
        flowInstancePO.setCreatedTime(LocalDateTime.now());
        flowInstancePO.setUpdatedTime(LocalDateTime.now());
        return flowInstancePO;
    }

    private String saveInstanceData(FlowInstance flowInstancePO, Map<String, InstanceDataModel> instanceDataMap) throws ProcessException {
        if (MapUtils.isEmpty(instanceDataMap)) {
            return StringUtils.EMPTY;
        }

        InstanceData instanceDataPO = buildInstanceDataPO(flowInstancePO, instanceDataMap);
        boolean result = instanceDataService.save(instanceDataPO);
        if (result) {
            return instanceDataPO.getInstanceDataId();
        }

        LOGGER.warn("saveInstanceDataPO: insert failed.||instanceDataPO={}", instanceDataPO);
        throw new ProcessException(ErrorEnum.SAVE_INSTANCE_DATA_FAILED);
    }

    private InstanceData buildInstanceDataPO(FlowInstance flowInstancePO, Map<String, InstanceDataModel> instanceDataMap) {
        InstanceData instanceDataPO = new InstanceData();
        // copy flow info & flowInstanceId
        BeanUtils.copyProperties(flowInstancePO, instanceDataPO);

        // generate instanceDataId
        instanceDataPO.setInstanceDataId(genId());
        instanceDataPO.setInstanceData(InstanceDataUtil.getInstanceDataListStr(instanceDataMap));

        instanceDataPO.setNodeInstanceId(StringUtils.EMPTY);
        instanceDataPO.setNodeKey(StringUtils.EMPTY);
        instanceDataPO.setCreatedTime(LocalDateTime.now());
        instanceDataPO.setType(InstanceDataType.INIT);
        return instanceDataPO;
    }

    private void fillExecuteContext(RuntimeContext runtimeContext, String flowInstanceId, String instanceDataId) throws ProcessException {
        runtimeContext.setFlowInstanceId(flowInstanceId);
        runtimeContext.setFlowInstanceStatus(FlowInstanceStatus.RUNNING);

        runtimeContext.setInstanceDataId(instanceDataId);

        runtimeContext.setNodeInstanceList(new ArrayList<>());

        //set startEvent into suspendNodeInstance as the first node to process
        Map<String, FlowElement> flowElementMap = runtimeContext.getFlowElementMap();
        FlowElement startEvent = FlowModelUtil.getStartEvent(flowElementMap);
        if (startEvent == null) {
            LOGGER.warn("fillExecuteContext failed: cannot get startEvent.||flowInstance={}||flowDeployId={}",
                    runtimeContext.getFlowInstanceId(), runtimeContext.getFlowDeployId());
            throw new ProcessException(ErrorEnum.GET_NODE_FAILED);
        }
        NodeInstanceBO suspendNodeInstance = new NodeInstanceBO();
        suspendNodeInstance.setNodeKey(startEvent.getKey());
        suspendNodeInstance.setFlStatus(NodeInstanceStatus.ACTIVE);
        suspendNodeInstance.setSourceNodeInstanceId(StringUtils.EMPTY);
        suspendNodeInstance.setSourceNodeKey(StringUtils.EMPTY);
        runtimeContext.setSuspendNodeInstance(suspendNodeInstance);

        runtimeContext.setCurrentNodeModel(startEvent);
    }

    private void doExecute(RuntimeContext runtimeContext) throws ProcessException {
        RuntimeExecutor runtimeExecutor = getExecuteExecutor(runtimeContext);
        while (runtimeExecutor != null) {
            runtimeExecutor.execute(runtimeContext);
            runtimeExecutor = runtimeExecutor.getExecuteExecutor(runtimeContext);
        }
    }

    private void postExecute(RuntimeContext runtimeContext) throws ProcessException {

        //1.update context with processStatus
        if (runtimeContext.getProcessStatus() == ProcessStatus.SUCCESS) {
            //SUCCESS: update runtimeContext: update suspendNodeInstance
            if (runtimeContext.getCurrentNodeInstance() != null) {
                runtimeContext.setSuspendNodeInstance(runtimeContext.getCurrentNodeInstance());
            }
        }

        //2.save nodeInstanceList to db
        saveNodeInstanceList(runtimeContext, NodeInstanceType.EXECUTE);

        //3.update flowInstance status while completed
        if (isCompleted(runtimeContext)) {
            flowInstanceService.updateStatus(runtimeContext.getFlowInstanceId(), FlowInstanceStatus.COMPLETED);

            runtimeContext.setFlowInstanceStatus(FlowInstanceStatus.COMPLETED);

            LOGGER.info("postExecute: flowInstance process completely.||flowInstanceId={}", runtimeContext.getFlowInstanceId());
        }
    }


    ////////////////////////////////////////commit////////////////////////////////////////

    @Override
    public void commit(RuntimeContext runtimeContext) throws ProcessException {
        int processStatus = ProcessStatus.SUCCESS;
        try {
            preCommit(runtimeContext);
            doCommit(runtimeContext);
        } catch (ReentrantException re) {
            //ignore
        } catch (ProcessException pe) {
            if (!ErrorEnum.isSuccess(pe.getErrNo())) {
                processStatus = ProcessStatus.FAILED;
            }
            throw pe;
        } finally {
            runtimeContext.setProcessStatus(processStatus);
            postCommit(runtimeContext);
        }
    }

    /**
     * Fill runtimeContext:
     * 1. Get instanceData from db firstly
     * 2. merge and save instanceData while commitData is not empty
     * 3. Update runtimeContext: instanceDataId, instanceDataMap, nodeInstanceList, suspendNodeInstance
     *
     * @throws Exception
     */
    private void preCommit(RuntimeContext runtimeContext) throws ProcessException {
        String flowInstanceId = runtimeContext.getFlowInstanceId();
        NodeInstanceBO suspendNodeInstance = runtimeContext.getSuspendNodeInstance();
        String nodeInstanceId = suspendNodeInstance.getNodeInstanceId();

        //1.get instanceData from db
        NodeInstance nodeInstancePO = nodeInstanceService.nodeInstanceId(nodeInstanceId, flowInstanceId);
        if (nodeInstancePO == null) {
            LOGGER.warn("preCommit failed: cannot find nodeInstancePO from db.||flowInstanceId={}||nodeInstanceId={}",
                    flowInstanceId, nodeInstanceId);
            throw new ProcessException(ErrorEnum.GET_NODE_INSTANCE_FAILED);
        }

        //unexpected: flowInstance is completed
        if (isCompleted(runtimeContext)) {
            LOGGER.warn("preExecute warning: reentrant process. FlowInstance has been processed completely.||runtimeContext={}", runtimeContext);
            runtimeContext.setFlowInstanceStatus(FlowInstanceStatus.COMPLETED);
            suspendNodeInstance.setId(nodeInstancePO.getId());
            suspendNodeInstance.setNodeKey(nodeInstancePO.getNodeKey());
            suspendNodeInstance.setSourceNodeInstanceId(nodeInstancePO.getSourceNodeInstanceId());
            suspendNodeInstance.setSourceNodeKey(nodeInstancePO.getSourceNodeKey());
            suspendNodeInstance.setInstanceDataId(nodeInstancePO.getInstanceDataId());
            suspendNodeInstance.setFlStatus(nodeInstancePO.getFlStatus());
            throw new ReentrantException(ErrorEnum.REENTRANT_WARNING);
        }
        Map<String, InstanceDataModel> instanceDataMap;
        String instanceDataId = nodeInstancePO.getInstanceDataId();
        if (StringUtils.isBlank(instanceDataId)) {
            instanceDataMap = new HashMap<>();
        } else {
            InstanceData instanceDataPO = instanceDataService.select(flowInstanceId, instanceDataId);
            if (instanceDataPO == null) {
                LOGGER.warn("preCommit failed: cannot find instanceDataPO from db." +
                        "||flowInstanceId={}||instanceDataId={}", flowInstanceId, instanceDataId);
                throw new ProcessException(ErrorEnum.GET_INSTANCE_DATA_FAILED);
            }
            instanceDataMap = InstanceDataUtil.getInstanceDataMap(instanceDataPO.getInstanceData());
        }

        //2.merge data while commitDataMap is not empty
        Map<String, InstanceDataModel> commitDataMap = runtimeContext.getInstanceDataMap();
        if (MapUtils.isNotEmpty(commitDataMap)) {
            instanceDataId = genId();
            instanceDataMap.putAll(commitDataMap);

            InstanceData commitInstanceDataPO = buildCommitInstanceData(runtimeContext, nodeInstanceId,
                    nodeInstancePO.getNodeKey(), instanceDataId, instanceDataMap);
            instanceDataService.save(commitInstanceDataPO);
        }

        //3.update runtimeContext
        fillCommitContext(runtimeContext, nodeInstancePO, instanceDataId, instanceDataMap);
    }

    private InstanceData buildCommitInstanceData(RuntimeContext runtimeContext, String nodeInstanceId, String nodeKey,
                                                 String newInstanceDataId, Map<String, InstanceDataModel> instanceDataMap) {
        InstanceData instanceDataPO = new InstanceData();
        BeanUtils.copyProperties(runtimeContext, instanceDataPO);

        instanceDataPO.setNodeInstanceId(nodeInstanceId);
        instanceDataPO.setNodeKey(nodeKey);
        instanceDataPO.setType(InstanceDataType.COMMIT);
        instanceDataPO.setCreatedTime(LocalDateTime.now());

        instanceDataPO.setInstanceDataId(newInstanceDataId);
        instanceDataPO.setInstanceData(InstanceDataUtil.getInstanceDataListStr(instanceDataMap));

        return instanceDataPO;
    }

    private void fillCommitContext(RuntimeContext runtimeContext, NodeInstance nodeInstancePO, String instanceDataId,
                                   Map<String, InstanceDataModel> instanceDataMap) throws ProcessException {

        runtimeContext.setInstanceDataId(instanceDataId);
        runtimeContext.setInstanceDataMap(instanceDataMap);

        updateSuspendNodeInstanceBO(runtimeContext.getSuspendNodeInstance(), nodeInstancePO, instanceDataId);

        setCurrentFlowModel(runtimeContext);

        runtimeContext.setNodeInstanceList(new ArrayList<>());
    }

    private void doCommit(RuntimeContext runtimeContext) throws ProcessException {
        RuntimeExecutor runtimeExecutor = getExecuteExecutor(runtimeContext);
        runtimeExecutor.commit(runtimeContext);

        runtimeExecutor = runtimeExecutor.getExecuteExecutor(runtimeContext);
        while (runtimeExecutor != null) {
            runtimeExecutor.execute(runtimeContext);
            runtimeExecutor = runtimeExecutor.getExecuteExecutor(runtimeContext);
        }
    }

    private void postCommit(RuntimeContext runtimeContext) throws ProcessException {
        if (runtimeContext.getProcessStatus() == ProcessStatus.SUCCESS && runtimeContext.getCurrentNodeInstance() != null) {
            runtimeContext.setSuspendNodeInstance(runtimeContext.getCurrentNodeInstance());
        }
        //update FlowInstancePO to db
        saveNodeInstanceList(runtimeContext, NodeInstanceType.COMMIT);

        if (isCompleted(runtimeContext)) {
            flowInstanceService.updateStatus(runtimeContext.getFlowInstanceId(), FlowInstanceStatus.COMPLETED);

            //update context
            runtimeContext.setFlowInstanceStatus(FlowInstanceStatus.COMPLETED);

            LOGGER.info("postCommit: flowInstance process completely.||flowInstanceId={}", runtimeContext.getFlowInstanceId());
        }
    }

    ////////////////////////////////////////rollback////////////////////////////////////////

    @Override
    public void rollback(RuntimeContext runtimeContext) throws ProcessException {
        int processStatus = ProcessStatus.SUCCESS;
        try {
            preRollback(runtimeContext);
            doRollback(runtimeContext);
        } catch (ReentrantException re) {
            //ignore
        } catch (ProcessException pe) {
            if (!ErrorEnum.isSuccess(pe.getErrNo())) {
                processStatus = ProcessStatus.FAILED;
            }
            throw pe;
        } finally {
            runtimeContext.setProcessStatus(processStatus);
            postRollback(runtimeContext);
        }
    }

    private void preRollback(RuntimeContext runtimeContext) throws ProcessException {
        String flowInstanceId = runtimeContext.getFlowInstanceId();

        //1.check node: only the latest enabled(ACTIVE or COMPLETED) nodeInstance can be rollbacked.
        String suspendNodeInstanceId = runtimeContext.getSuspendNodeInstance().getNodeInstanceId();
        NodeInstance rollbackNodeInstancePO = getActiveUserTaskForRollback(flowInstanceId, suspendNodeInstanceId,
                runtimeContext.getFlowElementMap());
        if (rollbackNodeInstancePO == null) {
            LOGGER.warn("preRollback failed: cannot rollback.||runtimeContext={}", runtimeContext);
            throw new ProcessException(ErrorEnum.ROLLBACK_FAILED);
        }

        //2.check status: flowInstance is completed
        if (isCompleted(runtimeContext)) {
            LOGGER.warn("invalid preRollback: FlowInstance has been processed completely."
                    + "||flowInstanceId={}||flowDeployId={}", flowInstanceId, runtimeContext.getFlowDeployId());
            NodeInstanceBO suspendNodeInstance = new NodeInstanceBO();
            BeanUtils.copyProperties(rollbackNodeInstancePO, suspendNodeInstance);
            runtimeContext.setSuspendNodeInstance(suspendNodeInstance);
            runtimeContext.setFlowInstanceStatus(FlowInstanceStatus.COMPLETED);
            throw new ProcessException(ErrorEnum.ROLLBACK_FAILED);
        }

        //3.get instanceData
        String instanceDataId = rollbackNodeInstancePO.getInstanceDataId();
        Map<String, InstanceDataModel> instanceDataMap;
        if (StringUtils.isBlank(instanceDataId)) {
            instanceDataMap = new HashMap<>();
        } else {
            InstanceData instanceDataPO = instanceDataService.select(flowInstanceId, instanceDataId);
            if (instanceDataPO == null) {
                LOGGER.warn("preRollback failed: cannot find instanceDataPO from db."
                        + "||flowInstanceId={}||instanceDataId={}", flowInstanceId, instanceDataId);
                throw new ProcessException(ErrorEnum.GET_INSTANCE_DATA_FAILED);
            }
            instanceDataMap = InstanceDataUtil.getInstanceDataMap(instanceDataPO.getInstanceData());
        }

        //4.update runtimeContext
        fillRollbackContext(runtimeContext, rollbackNodeInstancePO, instanceDataMap);
    }

    // TODO: 2020/1/10 clean code
    //1. getActiveUserTask
    //2. if(canRollback): only the active userTask or the lasted completed userTask can be rollback
    private NodeInstance getActiveUserTaskForRollback(String flowInstanceId, String suspendNodeInstanceId,
                                                      Map<String, FlowElement> flowElementMap) {
        List<NodeInstance> nodeInstancePOList = nodeInstanceService.byFlowInstanceId(flowInstanceId, false);
        if (CollectionUtils.isEmpty(nodeInstancePOList)) {
            LOGGER.warn("getActiveUserTaskForRollback: nodeInstancePOList is empty."
                    + "||flowInstanceId={}||suspendNodeInstanceId={}", flowInstanceId, suspendNodeInstanceId);
            return null;
        }

        NodeInstance activeNodeInstancePO = null;
        for (NodeInstance nodeInstancePO : nodeInstancePOList) {
            // TODO: 2020/1/10 the first active or completed node must be UserTask
            //ignore userTask
            if (!FlowModelUtil.isElementType(nodeInstancePO.getNodeKey(), flowElementMap, FlowElementTypeEnum.USER_TASK.getCode())) {
                LOGGER.info("getActiveUserTaskForRollback: ignore un-userTask nodeInstance.||flowInstanceId={}"
                        + "||suspendNodeInstanceId={}||nodeKey={}", flowInstanceId, suspendNodeInstanceId, nodeInstancePO.getNodeKey());
                continue;
            }

            if (nodeInstancePO.getFlStatus() == NodeInstanceStatus.ACTIVE) {
                activeNodeInstancePO = nodeInstancePO;

                if (nodeInstancePO.getNodeInstanceId().equals(suspendNodeInstanceId)) {
                    LOGGER.info("getActiveUserTaskForRollback: roll back the active userTask."
                            + "||flowInstanceId={}||suspendNodeInstanceId={}", flowInstanceId, suspendNodeInstanceId);
                    return activeNodeInstancePO;
                }
            } else if (nodeInstancePO.getFlStatus() == NodeInstanceStatus.COMPLETED) {
                if (nodeInstancePO.getNodeInstanceId().equals(suspendNodeInstanceId)) {
                    LOGGER.info("getActiveUserTaskForRollback: roll back the lasted completed userTask."
                                    + "||flowInstanceId={}||suspendNodeInstanceId={}||activeNodeInstanceId={}",
                            flowInstanceId, suspendNodeInstanceId, activeNodeInstancePO);
                    return activeNodeInstancePO;
                }

                LOGGER.warn("getActiveUserTaskForRollback: cannot rollback the userTask."
                        + "||flowInstanceId={}||suspendNodeInstanceId={}", flowInstanceId, suspendNodeInstanceId);
                return null;
            }
            LOGGER.info("getActiveUserTaskForRollback: ignore disabled userTask instance.||flowInstanceId={}"
                    + "||suspendNodeInstanceId={}||status={}", flowInstanceId, suspendNodeInstanceId, nodeInstancePO.getFlStatus());

        }
        LOGGER.warn("getActiveUserTaskForRollback: cannot rollback the suspendNodeInstance."
                + "||flowInstanceId={}||suspendNodeInstanceId={}", flowInstanceId, suspendNodeInstanceId);
        return null;
    }

    private void doRollback(RuntimeContext runtimeContext) throws ProcessException {
        RuntimeExecutor runtimeExecutor = getRollbackExecutor(runtimeContext);
        while (runtimeExecutor != null) {
            runtimeExecutor.rollback(runtimeContext);
            runtimeExecutor = runtimeExecutor.getRollbackExecutor(runtimeContext);
        }
    }

    private void postRollback(RuntimeContext runtimeContext) {

        if (runtimeContext.getProcessStatus() != ProcessStatus.SUCCESS) {
            LOGGER.warn("postRollback: ignore while process failed.||runtimeContext={}", runtimeContext);
            return;
        }
        if (runtimeContext.getCurrentNodeInstance() != null) {
            runtimeContext.setSuspendNodeInstance(runtimeContext.getCurrentNodeInstance());
        }

        //update FlowInstancePO to db
        saveNodeInstanceList(runtimeContext, NodeInstanceType.ROLLBACK);

    }

    private void fillRollbackContext(RuntimeContext runtimeContext, NodeInstance nodeInstancePO,
                                     Map<String, InstanceDataModel> instanceDataMap) throws ProcessException {
        runtimeContext.setInstanceDataId(nodeInstancePO.getInstanceDataId());
        runtimeContext.setInstanceDataMap(instanceDataMap);
        runtimeContext.setNodeInstanceList(new ArrayList<>());
        NodeInstanceBO suspendNodeInstanceBO = buildSuspendNodeInstanceBO(nodeInstancePO);
        runtimeContext.setSuspendNodeInstance(suspendNodeInstanceBO);
        setCurrentFlowModel(runtimeContext);
    }

    private NodeInstanceBO buildSuspendNodeInstanceBO(NodeInstance nodeInstancePO) {
        NodeInstanceBO suspendNodeInstanceBO = new NodeInstanceBO();
        BeanUtils.copyProperties(nodeInstancePO, suspendNodeInstanceBO);
        return suspendNodeInstanceBO;
    }

    private void updateSuspendNodeInstanceBO(NodeInstanceBO suspendNodeInstanceBO, NodeInstance nodeInstancePO, String
            instanceDataId) {
        suspendNodeInstanceBO.setId(nodeInstancePO.getId());
        suspendNodeInstanceBO.setNodeKey(nodeInstancePO.getNodeKey());
        suspendNodeInstanceBO.setFlStatus(nodeInstancePO.getFlStatus());
        suspendNodeInstanceBO.setSourceNodeInstanceId(nodeInstancePO.getSourceNodeInstanceId());
        suspendNodeInstanceBO.setSourceNodeKey(nodeInstancePO.getSourceNodeKey());
        suspendNodeInstanceBO.setInstanceDataId(instanceDataId);
    }

    //suspendNodeInstanceBO is necessary
    private void setCurrentFlowModel(RuntimeContext runtimeContext) throws ProcessException {
        NodeInstanceBO suspendNodeInstanceBO = runtimeContext.getSuspendNodeInstance();
        FlowElement currentNodeModel = FlowModelUtil.getFlowElement(runtimeContext.getFlowElementMap(), suspendNodeInstanceBO.getNodeKey());
        if (currentNodeModel == null) {
            LOGGER.warn("setCurrentFlowModel failed: cannot get currentNodeModel.||flowInstance={}||flowDeployId={}||nodeKey={}",
                    runtimeContext.getFlowInstanceId(), runtimeContext.getFlowDeployId(), suspendNodeInstanceBO.getNodeKey());
            throw new ProcessException(ErrorEnum.GET_NODE_FAILED);
        }
        runtimeContext.setCurrentNodeModel(currentNodeModel);
    }

    @Override
    protected boolean isCompleted(RuntimeContext runtimeContext) throws ProcessException{
        if (runtimeContext.getFlowInstanceStatus() == FlowInstanceStatus.COMPLETED) {
            return true;
        }

        NodeInstanceBO suspendNodeInstance = runtimeContext.getSuspendNodeInstance();
        if (suspendNodeInstance == null) {
            LOGGER.warn("suspendNodeInstance is null.||runtimeContext={}", runtimeContext);
            return false;
        }

        if (suspendNodeInstance.getFlStatus() != NodeInstanceStatus.COMPLETED) {
            return false;
        }

        String nodeKey = suspendNodeInstance.getNodeKey();
        Map<String, FlowElement> flowElementMap = runtimeContext.getFlowElementMap();
        if (FlowModelUtil.getFlowElement(flowElementMap, nodeKey).getType() == FlowElementTypeEnum.END_EVENT.getCode()) {
            return true;
        }
        return false;
    }

    @Override
    protected RuntimeExecutor getExecuteExecutor(RuntimeContext runtimeContext) throws ProcessException {
        return getElementExecutor(runtimeContext);
    }

    @Override
    protected RuntimeExecutor getRollbackExecutor(RuntimeContext runtimeContext) throws ProcessException {
        return getElementExecutor(runtimeContext);
    }

    private RuntimeExecutor getElementExecutor(RuntimeContext runtimeContext) throws ProcessException {
        //if process completed, return null
        if (isCompleted(runtimeContext)) {
            return null;
        }
        return getElementExecutor(runtimeContext.getCurrentNodeModel());
    }

    ////////////////////////////////////////common////////////////////////////////////////

    private void saveNodeInstanceList(RuntimeContext runtimeContext, int nodeInstanceType) {

        List<NodeInstanceBO> processNodeList = runtimeContext.getNodeInstanceList();

        if (CollectionUtils.isEmpty(processNodeList)) {
            LOGGER.warn("saveNodeInstanceList: processNodeList is empty,||flowInstanceId={}||nodeInstanceType={}",
                    runtimeContext.getFlowInstanceId(), nodeInstanceType);
            return;
        }

        List<NodeInstance> nodeInstancePOList = new ArrayList<>();
        List<NodeInstanceLog> nodeInstanceLogPOList = new ArrayList<>();

        processNodeList.forEach(nodeInstanceBO -> {
            NodeInstance nodeInstancePO = buildNodeInstancePO(runtimeContext, nodeInstanceBO);
            if (nodeInstancePO != null) {
                nodeInstancePOList.add(nodeInstancePO);

                //build nodeInstance log
                NodeInstanceLog nodeInstanceLogPO = buildNodeInstanceLogPO(nodeInstancePO, nodeInstanceType);
                nodeInstanceLogPOList.add(nodeInstanceLogPO);
            }
        });
        nodeInstanceService.insertOrUpdateList(nodeInstancePOList);
        nodeInstanceLogService.saveBatch(nodeInstanceLogPOList);
    }

    private NodeInstance buildNodeInstancePO(RuntimeContext runtimeContext, NodeInstanceBO nodeInstanceBO) {
        if (runtimeContext.getProcessStatus() == ProcessStatus.FAILED) {
            //set status=FAILED unless it is origin processNodeInstance(suspendNodeInstance)
            if (nodeInstanceBO.getNodeKey().equals(runtimeContext.getSuspendNodeInstance().getNodeKey())) {
                //keep suspendNodeInstance's status while process failed.
                return null;
            }
            nodeInstanceBO.setFlStatus(NodeInstanceStatus.FAILED);
        }

        NodeInstance nodeInstancePO = new NodeInstance();
        BeanUtils.copyProperties(nodeInstanceBO, nodeInstancePO);
        nodeInstancePO.setFlowInstanceId(runtimeContext.getFlowInstanceId());
        nodeInstancePO.setFlowDeployId(runtimeContext.getFlowDeployId());
        nodeInstancePO.setTenant(runtimeContext.getTenant());
        nodeInstancePO.setCaller(runtimeContext.getCaller());
        nodeInstancePO.setCreatedTime(LocalDateTime.now());
        nodeInstancePO.setUpdatedTime(LocalDateTime.now());
        return nodeInstancePO;
    }

    private NodeInstanceLog buildNodeInstanceLogPO(NodeInstance nodeInstancePO, int nodeInstanceType) {
        NodeInstanceLog nodeInstanceLogPO = new NodeInstanceLog();
        BeanUtils.copyProperties(nodeInstancePO, nodeInstanceLogPO);
        nodeInstanceLogPO.setType(nodeInstanceType);
        nodeInstanceLogPO.setId(null);
        return nodeInstanceLogPO;
    }

}
