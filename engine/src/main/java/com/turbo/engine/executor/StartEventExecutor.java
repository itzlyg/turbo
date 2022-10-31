package com.turbo.engine.executor;

import com.turbo.engine.bo.NodeInstanceBO;
import com.turbo.engine.common.ErrorEnum;
import com.turbo.engine.common.NodeInstanceStatus;
import com.turbo.engine.common.RuntimeContext;
import com.turbo.engine.exception.ProcessException;
import java.util.Collections;
import org.springframework.stereotype.Service;

@Service
public class StartEventExecutor extends ElementExecutor {

    @Override
    protected void postExecute(RuntimeContext runtimeContext) throws ProcessException {
        NodeInstanceBO currentNodeInstance = runtimeContext.getCurrentNodeInstance();
        currentNodeInstance.setInstanceDataId(runtimeContext.getInstanceDataId());
        currentNodeInstance.setFlStatus(NodeInstanceStatus.COMPLETED);
        runtimeContext.getNodeInstanceList().add(currentNodeInstance);
    }

    @Override
    protected void preRollback(RuntimeContext runtimeContext) throws ProcessException {
        runtimeContext.setCurrentNodeInstance(runtimeContext.getSuspendNodeInstance());
        runtimeContext.setNodeInstanceList(Collections.emptyList());

        LOGGER.warn("postRollback: reset runtimeContext.||flowInstanceId={}||nodeKey={}||nodeType={}",
                runtimeContext.getFlowInstanceId(), runtimeContext.getCurrentNodeModel().getKey(), runtimeContext.getCurrentNodeModel().getType());
        throw new ProcessException(ErrorEnum.NO_USER_TASK_TO_ROLLBACK, "It's a startEvent.");
    }

    @Override
    protected void postRollback(RuntimeContext runtimeContext) throws ProcessException {
        //do nothing
    }
}
