package com.didiglobal.turbo.engine.executor;


import com.didiglobal.turbo.engine.common.Constants;
import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.common.FlowElementTypeEnum;
import com.didiglobal.turbo.engine.common.RuntimeContext;
import com.didiglobal.turbo.engine.config.SpringUtil;
import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.service.InstanceDataService;
import com.didiglobal.turbo.engine.service.NodeInstanceLogService;
import com.didiglobal.turbo.engine.service.NodeInstanceService;
import com.didiglobal.turbo.engine.util.FlowModelUtil;
import com.didiglobal.turbo.engine.util.SnowFlake;
import java.text.MessageFormat;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RuntimeExecutor {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RuntimeExecutor.class);

    @Resource
    protected InstanceDataService instanceDataService;

    @Resource
    protected NodeInstanceService nodeInstanceService;

    @Resource
    protected NodeInstanceLogService nodeInstanceLogService;

    protected String genId() {
        return SnowFlake.genId();
    }

    public abstract void execute(RuntimeContext runtimeContext) throws ProcessException;

    public abstract void commit(RuntimeContext runtimeContext) throws ProcessException;

    public abstract void rollback(RuntimeContext runtimeContext) throws ProcessException;

    protected abstract boolean isCompleted(RuntimeContext runtimeContext) throws ProcessException;

    protected abstract RuntimeExecutor getExecuteExecutor(RuntimeContext runtimeContext) throws ProcessException;

    protected abstract RuntimeExecutor getRollbackExecutor(RuntimeContext runtimeContext) throws ProcessException;

    protected ElementExecutor getElementExecutor(FlowElement flowElement) throws ProcessException {
        int elementType = flowElement.getType();
        ElementExecutor elementExecutor = getElementExecutor(elementType);
        if (elementExecutor == null) {
            LOGGER.warn("getElementExecutor failed: unsupported elementType.|elementType={}", elementType);
            throw new ProcessException(ErrorEnum.UNSUPPORTED_ELEMENT_TYPE,
                    MessageFormat.format(Constants.NODE_INFO_FORMAT, flowElement.getKey(),
                            FlowModelUtil.getElementName(flowElement), elementType));
        }

        return elementExecutor;
    }

    private ElementExecutor getElementExecutor(int elementType) {
        FlowElementTypeEnum elementTypeEnum = FlowElementTypeEnum.byCode(elementType);
        if (elementTypeEnum != null) {
            Map<String, ElementExecutor> elementExecutor = SpringUtil.beansOfType(ElementExecutor.class);
            return elementExecutor.get(elementTypeEnum.getName());
        }
        return null;
    }
}
