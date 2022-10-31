package com.turbo.engine.executor;

import com.turbo.engine.bo.NodeInstanceBO;
import com.turbo.engine.common.Constants;
import com.turbo.engine.common.ErrorEnum;
import com.turbo.engine.common.NodeInstanceStatus;
import com.turbo.engine.common.RuntimeContext;
import com.turbo.engine.exception.ProcessException;
import com.turbo.engine.model.FlowElement;
import com.turbo.engine.util.FlowModelUtil;
import java.text.MessageFormat;
import org.springframework.stereotype.Service;

@Service
public class EndEventExecutor extends ElementExecutor {

    @Override
    protected void postExecute(RuntimeContext runtimeContext) throws ProcessException {
        NodeInstanceBO currentNodeInstance = runtimeContext.getCurrentNodeInstance();
        currentNodeInstance.setInstanceDataId(runtimeContext.getInstanceDataId());
        currentNodeInstance.setStatus(NodeInstanceStatus.COMPLETED);
        runtimeContext.getNodeInstanceList().add(currentNodeInstance);
    }

    @Override
    protected void doRollback(RuntimeContext runtimeContext) throws ProcessException {
        FlowElement flowElement = runtimeContext.getCurrentNodeModel();
        String nodeName = FlowModelUtil.getElementName(flowElement);
        LOGGER.warn("doRollback: unsupported element type as EndEvent.||flowInstanceId={}||nodeKey={}||nodeName={}||nodeType={}",
                runtimeContext.getFlowInstanceId(), flowElement.getKey(), nodeName, flowElement.getType());
        throw new ProcessException(ErrorEnum.UNSUPPORTED_ELEMENT_TYPE,
                MessageFormat.format(Constants.NODE_INFO_FORMAT, flowElement.getKey(), nodeName, flowElement.getType()));
    }

    @Override
    protected void postRollback(RuntimeContext runtimeContext) throws ProcessException {
        //do nothing
    }

    @Override
    protected RuntimeExecutor getExecuteExecutor(RuntimeContext runtimeContext) throws ProcessException {
        LOGGER.info("getExecuteExecutor: no executor after EndEvent.");
        return null;
    }
}
