package com.turbo.engine.processor;

import com.turbo.engine.common.ErrorEnum;
import com.turbo.engine.common.FlowDefinitionStatus;
import com.turbo.engine.common.FlowDeploymentStatus;
import com.turbo.engine.common.FlowModuleEnum;
import com.turbo.engine.entity.FlowDefinition;
import com.turbo.engine.entity.FlowDeployment;
import com.turbo.engine.exception.DefinitionException;
import com.turbo.engine.exception.ParamException;
import com.turbo.engine.exception.TurboException;
import com.turbo.engine.param.CreateFlowParam;
import com.turbo.engine.param.DeployFlowParam;
import com.turbo.engine.param.GetFlowModuleParam;
import com.turbo.engine.param.UpdateFlowParam;
import com.turbo.engine.result.CommonResult;
import com.turbo.engine.result.CreateFlowResult;
import com.turbo.engine.result.DeployFlowResult;
import com.turbo.engine.result.FlowModuleResult;
import com.turbo.engine.result.UpdateFlowResult;
import com.turbo.engine.service.FlowDefinitionService;
import com.turbo.engine.service.FlowDeploymentService;
import com.turbo.engine.util.JsonUtil;
import com.turbo.engine.util.SnowFlake;
import com.turbo.engine.validator.ModelValidator;
import com.turbo.engine.validator.ParamValidator;
import java.time.LocalDateTime;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class DefinitionProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefinitionProcessor.class);


    @Resource
    private ModelValidator modelValidator;

    @Resource
    private FlowDefinitionService flowDefinitionService;

    @Resource
    private FlowDeploymentService flowDeploymentService;

    public CreateFlowResult create(CreateFlowParam createFlowParam) {
        CreateFlowResult createFlowResult = new CreateFlowResult();
        try {
            ParamValidator.validate(createFlowParam);

            FlowDefinition flowDefinitionPO = new FlowDefinition();
            BeanUtils.copyProperties(createFlowParam, flowDefinitionPO);
            String flowModuleId = SnowFlake.genId();
            flowDefinitionPO.setFlowModuleId(flowModuleId);
            flowDefinitionPO.setFlStatus(FlowDefinitionStatus.INIT);
            flowDefinitionPO.setCreatedTime(LocalDateTime.now());
            flowDefinitionPO.setUpdatedTime(LocalDateTime.now());

            boolean rows = flowDefinitionService.save(flowDefinitionPO);
            if (!rows) {
                LOGGER.warn("create flow failed: insert to db failed.||createFlowParam={}", createFlowParam);
                throw new DefinitionException(ErrorEnum.DEFINITION_INSERT_INVALID);
            }

            BeanUtils.copyProperties(flowDefinitionPO, createFlowResult);
            fillCommonResult(createFlowResult, ErrorEnum.SUCCESS);
        } catch (TurboException te) {
            fillCommonResult(createFlowResult, te);
        }
        return createFlowResult;
    }

    public UpdateFlowResult update(UpdateFlowParam updateFlowParam) {
        UpdateFlowResult updateFlowResult = new UpdateFlowResult();
        try {
            ParamValidator.validate(updateFlowParam);

            FlowDefinition flowDefinitionPO = new FlowDefinition();
            BeanUtils.copyProperties(updateFlowParam, flowDefinitionPO);
            flowDefinitionPO.setFlStatus(FlowDefinitionStatus.EDITING);
            flowDefinitionPO.setUpdatedTime(LocalDateTime.now());

            boolean rows = flowDefinitionService.updateByModelId(flowDefinitionPO);
            if (!rows) {
                LOGGER.warn("update flow failed: update to db failed.||updateFlowParam={}", updateFlowParam);
                throw new DefinitionException(ErrorEnum.DEFINITION_UPDATE_INVALID);
            }
            fillCommonResult(updateFlowResult, ErrorEnum.SUCCESS);
        } catch (TurboException te) {
            fillCommonResult(updateFlowResult, te);
        }
        return updateFlowResult;
    }

    public DeployFlowResult deploy(DeployFlowParam deployFlowParam) {
        DeployFlowResult deployFlowResult = new DeployFlowResult();
        try {
            ParamValidator.validate(deployFlowParam);

            FlowDefinition flowDefinitionPO = flowDefinitionService.byModelId(deployFlowParam.getFlowModuleId());
            if (null == flowDefinitionPO) {
                LOGGER.warn("deploy flow failed: flow is not exist.||deployFlowParam={}", deployFlowParam);
                throw new DefinitionException(ErrorEnum.FLOW_NOT_EXIST);
            }

            Integer status = flowDefinitionPO.getFlStatus();
            if (status != FlowDefinitionStatus.EDITING) {
                LOGGER.warn("deploy flow failed: flow is not editing status.||deployFlowParam={}", deployFlowParam);
                throw new DefinitionException(ErrorEnum.FLOW_NOT_EDITING);
            }

            String flowModel = flowDefinitionPO.getFlowModel();
            modelValidator.validate(flowModel);

            FlowDeployment flowDeploymentPO = new FlowDeployment();
            BeanUtils.copyProperties(flowDefinitionPO, flowDeploymentPO);
            String flowDeployId = SnowFlake.genId();
            flowDeploymentPO.setFlowDeployId(flowDeployId);
            flowDeploymentPO.setFlStatus(FlowDeploymentStatus.DEPLOYED);

            boolean rows = flowDeploymentService.save(flowDeploymentPO);
            if (!rows) {
                LOGGER.warn("deploy flow failed: insert to db failed.||deployFlowParam={}", deployFlowParam);
                throw new DefinitionException(ErrorEnum.DEFINITION_INSERT_INVALID);
            }

            BeanUtils.copyProperties(flowDeploymentPO, deployFlowResult);
            fillCommonResult(deployFlowResult, ErrorEnum.SUCCESS);
        } catch (TurboException te) {
            fillCommonResult(deployFlowResult, te);
        }
        return deployFlowResult;
    }

    public FlowModuleResult getFlowModule(GetFlowModuleParam getFlowModuleParam) {
        FlowModuleResult flowModuleResult = new FlowModuleResult();
        try {
            ParamValidator.validate(getFlowModuleParam);
            String flowModuleId = getFlowModuleParam.getFlowModuleId();
            String flowDeployId = getFlowModuleParam.getFlowDeployId();
            if (StringUtils.isNotBlank(flowDeployId)) {
                flowModuleResult = getFlowModuleByFlowDeployId(flowDeployId);
            } else {
                flowModuleResult = getFlowModuleByFlowModuleId(flowModuleId);
            }
            fillCommonResult(flowModuleResult, ErrorEnum.SUCCESS);
        } catch (TurboException te) {
            fillCommonResult(flowModuleResult, te);
        }
        return flowModuleResult;
    }

    private FlowModuleResult getFlowModuleByFlowModuleId(String flowModuleId) throws ParamException {
        FlowDefinition flowDefinitionPO = flowDefinitionService.byModelId(flowModuleId);
        if (flowDefinitionPO == null) {
            LOGGER.warn("getFlowModuleByFlowModuleId failed: can not find flowDefinitionPO.||flowModuleId={}", flowModuleId);
            throw new ParamException(ErrorEnum.PARAM_INVALID.getErrNo(), "flowDefinitionPO is not exist");
        }
        FlowModuleResult flowModuleResult = new FlowModuleResult();
        BeanUtils.copyProperties(flowDefinitionPO, flowModuleResult);
        Integer status = FlowModuleEnum.getStatusByDefinitionStatus(flowDefinitionPO.getFlStatus());
        flowModuleResult.setStatus(status);
        LOGGER.info("getFlowModuleByFlowModuleId||flowModuleId={}||FlowModuleResult={}", flowModuleId, JsonUtil.toJson(flowModuleResult));
        return flowModuleResult;
    }

    private FlowModuleResult getFlowModuleByFlowDeployId(String flowDeployId) throws ParamException {
        FlowDeployment flowDeploymentPO = flowDeploymentService.deployIdOrFlowModuleId(flowDeployId, null);
        if (flowDeploymentPO == null) {
            LOGGER.warn("getFlowModuleByFlowDeployId failed: can not find flowDefinitionPO.||flowDeployId={}", flowDeployId);
            throw new ParamException(ErrorEnum.PARAM_INVALID.getErrNo(), "flowDefinitionPO is not exist");
        }
        FlowModuleResult flowModuleResult = new FlowModuleResult();
        BeanUtils.copyProperties(flowDeploymentPO, flowModuleResult);
        Integer status = FlowModuleEnum.getStatusByDeploymentStatus(flowDeploymentPO.getFlStatus());
        flowModuleResult.setStatus(status);
        LOGGER.info("getFlowModuleByFlowDeployId||flowDeployId={}||response={}", flowDeployId, JsonUtil.toJson(flowModuleResult));
        return flowModuleResult;
    }

    private void fillCommonResult(CommonResult commonResult, ErrorEnum errorEnum) {
        fillCommonResult(commonResult, errorEnum.getErrNo(), errorEnum.getErrMsg());
    }

    private void fillCommonResult(CommonResult commonResult, TurboException turboException) {
        fillCommonResult(commonResult, turboException.getErrNo(), turboException.getErrMsg());
    }

    private void fillCommonResult(CommonResult commonResult, int errNo, String errMsg) {
        commonResult.setErrCode(errNo);
        commonResult.setErrMsg(errMsg);
    }
}
