package com.turbo.engine.validator;

import com.turbo.engine.common.Constants;
import com.turbo.engine.common.ErrorEnum;
import com.turbo.engine.common.FlowElementTypeEnum;
import com.turbo.engine.exception.ProcessException;
import com.turbo.engine.model.FlowElement;
import com.turbo.engine.util.FlowModelUtil;
import java.text.MessageFormat;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ElementValidatorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElementValidatorFactory.class);

    @Resource
    private StartEventValidator startEventValidator;

    @Resource
    private EndEventValidator endEventValidator;

    @Resource
    private SequenceFlowValidator sequenceFlowValidator;

    @Resource
    private UserTaskValidator userTaskValidator;

    @Resource
    private ExclusiveGatewayValidator exclusiveGatewayValidator;

    public ElementValidator getElementValidator(FlowElement flowElement) throws ProcessException {
        int elementType = flowElement.getType();
        ElementValidator elementValidator = getElementValidator(elementType);

        if (elementValidator == null) {
            LOGGER.warn("getElementValidator failed: unsupported elementType.||elementType={}", elementType);
            throw new ProcessException(ErrorEnum.UNSUPPORTED_ELEMENT_TYPE,
                    MessageFormat.format(Constants.NODE_INFO_FORMAT, flowElement.getKey(),
                            FlowModelUtil.getElementName(flowElement), elementType));
        }
        return elementValidator;
    }

    private ElementValidator getElementValidator(int elementType) {
        FlowElementTypeEnum element = FlowElementTypeEnum.byCode(elementType);
        switch (element) {
            case START_EVENT:
                return startEventValidator;
            case END_EVENT:
                return endEventValidator;
            case SEQUENCE_FLOW:
                return sequenceFlowValidator;
            case USER_TASK:
                return userTaskValidator;
            case EXCLUSIVE_GATEWAY:
                return exclusiveGatewayValidator;
            default:
                return null;
        }
    }
}
