package com.turbo.engine.validator;

import com.turbo.engine.common.ErrorEnum;
import com.turbo.engine.exception.DefinitionException;
import com.turbo.engine.exception.ProcessException;
import com.turbo.engine.model.FlowModel;
import com.turbo.engine.util.FlowModelUtil;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ModelValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelValidator.class);

    @Resource
    private FlowModelValidator flowModelValidator;

    public void validate(String flowModelStr) throws DefinitionException, ProcessException {
        if (StringUtils.isBlank(flowModelStr)) {
            LOGGER.warn("message={}", ErrorEnum.MODEL_EMPTY.getErrMsg());
            throw new DefinitionException(ErrorEnum.MODEL_EMPTY);
        }

        FlowModel flowModel = FlowModelUtil.parseModelFromString(flowModelStr);
        if (flowModel == null || CollectionUtils.isEmpty(flowModel.getFlowElementList())) {
            LOGGER.warn("message={}||flowModelStr={}", ErrorEnum.MODEL_EMPTY.getErrMsg(), flowModelStr);
            throw new DefinitionException(ErrorEnum.MODEL_EMPTY);
        }
        flowModelValidator.validate(flowModel);
    }
}