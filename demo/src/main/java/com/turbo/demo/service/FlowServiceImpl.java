package com.turbo.demo.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.turbo.demo.enums.FlowModuleStatusEnum;
import com.turbo.demo.pojo.request.CreateFlowRequest;
import com.turbo.demo.pojo.request.DeployFlowRequest;
import com.turbo.demo.pojo.request.GetFlowModuleListRequest;
import com.turbo.demo.pojo.request.GetFlowModuleRequest;
import com.turbo.demo.pojo.request.UpdateFlowRequest;
import com.turbo.demo.pojo.response.FlowModuleListResponse;
import com.turbo.demo.pojo.response.FlowModuleResponse;
import com.turbo.engine.common.FlowDeploymentStatus;
import com.turbo.engine.engine.ProcessEngine;
import com.turbo.engine.entity.FlowDefinition;
import com.turbo.engine.entity.FlowDeployment;
import com.turbo.engine.result.CreateFlowResult;
import com.turbo.engine.result.DeployFlowResult;
import com.turbo.engine.result.FlowModuleResult;
import com.turbo.engine.result.UpdateFlowResult;
import com.turbo.engine.service.FlowDefinitionService;
import com.turbo.engine.service.FlowDeploymentService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @Author: james zhangxiao
 * @Date: 4/11/22
 * @Description: 流程处理类
 */
@Service
public class FlowServiceImpl {

    @Resource
    private ProcessEngine processEngine;

    @Resource
    private FlowDefinitionService flowDefinitionService;

    @Resource
    private FlowDeploymentService flowDeploymentService;

    /**
     * 创建流程
     *
     * @param createFlowParam 创建流程基础信息
     * @return 创建流程结果
     */
    public CreateFlowResult createFlow(CreateFlowRequest createFlowParam) {
        return processEngine.createFlow(createFlowParam);
    }

    /**
     * 保存流程模型数据
     *
     * @param updateFlowRequest 保存流程模型请求参数
     * @return 保存流程模型结果
     */
    public UpdateFlowResult updateFlow(UpdateFlowRequest updateFlowRequest) {
        return processEngine.updateFlow(updateFlowRequest);
    }

    /**
     * 发布流程
     *
     * @param deployFlowRequest 发布流程请求参数
     * @return 发布流程结果
     */
    public DeployFlowResult deployFlow(DeployFlowRequest deployFlowRequest) {
        return processEngine.deployFlow(deployFlowRequest);
    }

    /**
     * 获取单个流程
     *
     * @param getFlowModuleRequest 查询单个流程参数
     * @return 查询单个流程结果
     */
    public FlowModuleResult getFlowModule(GetFlowModuleRequest getFlowModuleRequest) {
        return processEngine.getFlowModule(getFlowModuleRequest);
    }

    /**
     * 查询流程列表
     *
     * @param getFlowModuleListRequest 查询流程列表参数
     * @return 查询流程结果
     */
    public FlowModuleListResponse getFlowModuleList(GetFlowModuleListRequest getFlowModuleListRequest) {
        Page<FlowDefinition> page = buildGetFlowModuleListPage(getFlowModuleListRequest);
        LambdaQueryWrapper<FlowDefinition> queryWrapper = buildGetFlowModuleListQueryWrapper(getFlowModuleListRequest);
        IPage<FlowDefinition> pageRes = flowDefinitionService.page(page, queryWrapper);
        List<FlowModuleResponse> flowModuleList = new ArrayList<>();
        for (FlowDefinition flowDefinitionPO : pageRes.getRecords()) {
            FlowModuleResponse getFlowModuleResponse = new FlowModuleResponse();
            BeanUtils.copyProperties(flowDefinitionPO, getFlowModuleResponse);
            LambdaQueryWrapper<FlowDeployment> flowDeployQuery = buildCountFlowDeployQueryWrapper(flowDefinitionPO.getFlowModuleId());
            long count = flowDeploymentService.count(flowDeployQuery);
            if (count >= 1) {
                //4 已发布
                getFlowModuleResponse.setStatus(FlowModuleStatusEnum.PUBLISHED.getValue());
            }
            flowModuleList.add(getFlowModuleResponse);
        }
        FlowModuleListResponse flowModuleListResponse = new FlowModuleListResponse();
        flowModuleListResponse.setFlowModuleList(flowModuleList);
        BeanUtils.copyProperties(pageRes, flowModuleListResponse);
        return flowModuleListResponse;
    }

    private Page<FlowDefinition> buildGetFlowModuleListPage(GetFlowModuleListRequest params) {
        Page<FlowDefinition> page = new Page<>();
        if (params.getSize() != null && params.getCurrent() != null) {
            page.setCurrent(params.getCurrent());
            page.setSize(params.getSize());
        }
        return page;
    }

    private LambdaQueryWrapper<FlowDefinition> buildGetFlowModuleListQueryWrapper(GetFlowModuleListRequest params) {
        return new LambdaQueryWrapper<FlowDefinition>()
                .eq(StringUtils.isNotBlank(params.getFlowModuleId()), FlowDefinition::getFlowModuleId, params.getFlowModuleId())
                .like(StringUtils.isNotBlank(params.getFlowName()), FlowDefinition::getFlowName, params.getFlowName())
                .eq(StringUtils.isNotBlank(params.getFlowDeployId()), FlowDefinition::getFlowModuleId, params.getFlowDeployId())
                .orderByDesc(FlowDefinition::getUpdatedTime);
    }

    private LambdaQueryWrapper<FlowDeployment> buildCountFlowDeployQueryWrapper(String flowModuleId) {
        return new LambdaQueryWrapper<FlowDeployment>()
                .eq(StringUtils.isNotBlank(flowModuleId), FlowDeployment::getFlowModuleId, flowModuleId)
                .eq(FlowDeployment::getFlStatus, FlowDeploymentStatus.DEPLOYED);
    }

}
