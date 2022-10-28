package com.didiglobal.turbo.engine.executor;

import com.didiglobal.turbo.engine.common.Constants;
import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.common.FlowElementTypeEnum;
import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.util.FlowModelUtil;
import java.text.MessageFormat;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExecutorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorFactory.class);

    @Resource
    private Map<String, ElementExecutor> elementExecutor;

    public ElementExecutor getElementExecutor(FlowElement flowElement) throws ProcessException {
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
            return elementExecutor.get(elementTypeEnum.getName());
        }
        return null;
    }
}
