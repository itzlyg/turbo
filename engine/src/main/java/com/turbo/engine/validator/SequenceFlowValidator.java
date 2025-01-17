package com.turbo.engine.validator;

import com.turbo.engine.common.ErrorEnum;
import com.turbo.engine.exception.DefinitionException;
import com.turbo.engine.model.FlowElement;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SequenceFlowValidator extends ElementValidator {

    @Override
    public void checkIncoming(Map<String, FlowElement> flowElementMap, FlowElement flowElement) throws DefinitionException {
        super.checkIncoming(flowElementMap, flowElement);

        List<String> incomingList = flowElement.getIncoming();
        if (incomingList.size() >1) {
            throwElementValidatorException(flowElement, ErrorEnum.ELEMENT_TOO_MUCH_INCOMING);
        }
    }

    @Override
    public void checkOutgoing(Map<String, FlowElement> flowElementMap, FlowElement flowElement) throws DefinitionException {
        super.checkOutgoing(flowElementMap, flowElement);

        List<String> outgoingList = flowElement.getOutgoing();
        if (outgoingList.size() > 1) {
            throwElementValidatorException(flowElement, ErrorEnum.ELEMENT_TOO_MUCH_OUTGOING);
        }
    }
}
