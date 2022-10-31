package com.turbo.engine.executor;

import com.turbo.engine.bo.HookInfoResponseBO;
import com.turbo.engine.bo.NodeInstanceBO;
import com.turbo.engine.common.InstanceDataType;
import com.turbo.engine.common.NodeInstanceStatus;
import com.turbo.engine.common.RuntimeContext;
import com.turbo.engine.config.HookProperties;
import com.turbo.engine.entity.InstanceData;
import com.turbo.engine.exception.ProcessException;
import com.turbo.engine.model.FlowElement;
import com.turbo.engine.model.InstanceDataModel;
import com.turbo.engine.util.FlowModelUtil;
import com.turbo.engine.util.InstanceDataUtil;
import com.turbo.engine.util.JsonUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExclusiveGatewayExecutor extends ElementExecutor {

    private static final String HOOK_URL_NAME = "infoHook";
    private static final String PARAM_FLOW_INSTANCE_ID = "flowInstanceId";
    private static final String PARAM_DATA_LIST = "dataList";

    private static final Logger log = LoggerFactory.getLogger(ExclusiveGatewayExecutor.class);

    @Resource
    private HookProperties hookProperties;

    @Resource
    private RestTemplate turboRestTemplate;
    /**
     * Update data map: http request to update data map
     * Url: dynamic config
     * Param: one of flowElement's properties
     */
    // TODO: 2019/12/16 common hook in preExecute
    @Override
    protected void doExecute(RuntimeContext runtimeContext) throws ProcessException {
        //1.get hook param
        FlowElement flowElement = runtimeContext.getCurrentNodeModel();
        String hookInfoParam = FlowModelUtil.getHookInfos(flowElement);
        //ignore while properties is empty
        if (StringUtils.isBlank(hookInfoParam)) {
            return;
        }

        //http post hook and get data result
        Map<String, InstanceDataModel> hookInfoValueMap = getHookInfoValueMap(runtimeContext.getFlowInstanceId(), hookInfoParam);
        LOGGER.info("doExecute getHookInfoValueMap.||hookInfoValueMap={}", hookInfoValueMap);
        if (MapUtils.isEmpty(hookInfoValueMap)) {
            LOGGER.warn("doExecute: hookInfoValueMap is empty.||flowInstanceId={}||hookInfoParam={}||nodeKey={}",
                    runtimeContext.getFlowInstanceId(), hookInfoParam, flowElement.getKey());
            return;
        }

        //merge data to current dataMap
        Map<String, InstanceDataModel> dataMap = runtimeContext.getInstanceDataMap();
        dataMap.putAll(hookInfoValueMap);

        //save data
        if (MapUtils.isNotEmpty(dataMap)) {
            String instanceDataId = saveInstanceDataPO(runtimeContext);
            runtimeContext.setInstanceDataId(instanceDataId);
        }
    }

    private Map<String, InstanceDataModel> getHookInfoValueMap(String flowInstanceId, String hookInfoParam) {
        //get hook config: url and timeout
        String hookUrl = hookProperties.getUrl();
        if (StringUtils.isBlank(hookUrl)) {
            LOGGER.info("getHookInfoValueMap: cannot find hookConfig.||flowInstanceId={}", flowInstanceId);
            return new HashMap<>();
        }
        //build request and http post
        Map<String, Object> hookParamMap = new HashMap<>();
        hookParamMap.put("flowInstanceId", flowInstanceId);
        hookParamMap.put("hookInfoParam", hookInfoParam);

        HookInfoResponseBO hookInfoResponse;
        try {
            hookInfoResponse = postJson(hookUrl, hookParamMap, HookInfoResponseBO.class);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        if (hookInfoResponse == null || hookInfoResponse.getStatus() != 0) {
            LOGGER.warn("getHookInfoValueMap failed: hookInfoResponse is null." +
                    "||hookUrl={}||hookParamMap={}", hookUrl, hookParamMap);
            return new HashMap<>();
        }

        Map<String, Object> data = hookInfoResponse.getData();
        if (MapUtils.isEmpty(data)) {
            LOGGER.warn("getHookInfoValueMap failed: data is empty.||hookUrl={}||hookParamMap={}",
                    hookUrl, hookParamMap);
            return new HashMap<>();
        }

        String respFlowInstanceId = (String) data.get(PARAM_FLOW_INSTANCE_ID);
        if (!flowInstanceId.equals(respFlowInstanceId)) {
            LOGGER.warn("getHookInfoValueMap failed: flowInstanceId is not match." +
                    "||hookUrl={}||hookParamMap={}", hookUrl, hookParamMap);
            return new HashMap<>();

        }

        List<InstanceDataModel> dataList = JsonUtil.toBeans(JsonUtil.toJson(data.get(PARAM_DATA_LIST)), InstanceDataModel.class);
        return InstanceDataUtil.getInstanceDataMap(dataList);
    }

    private String saveInstanceDataPO(RuntimeContext runtimeContext) {
        String instanceDataId = genId();
        InstanceData instanceDataPO = buildHookInstanceData(instanceDataId, runtimeContext);
        instanceDataService.save(instanceDataPO);
        return instanceDataId;
    }

    private InstanceData buildHookInstanceData(String instanceDataId, RuntimeContext runtimeContext) {
        InstanceData instanceDataPO = new InstanceData();
        BeanUtils.copyProperties(runtimeContext, instanceDataPO);
        instanceDataPO.setInstanceDataId(instanceDataId);
        instanceDataPO.setInstanceData(InstanceDataUtil.getInstanceDataListStr(runtimeContext.getInstanceDataMap()));
        instanceDataPO.setNodeInstanceId(runtimeContext.getCurrentNodeInstance().getNodeInstanceId());
        instanceDataPO.setNodeKey(runtimeContext.getCurrentNodeModel().getKey());
        instanceDataPO.setType(InstanceDataType.HOOK);
        instanceDataPO.setCreateTime(LocalDateTime.now());
        return instanceDataPO;
    }

    @Override
    protected void postExecute(RuntimeContext runtimeContext) throws ProcessException {
        NodeInstanceBO currentNodeInstance = runtimeContext.getCurrentNodeInstance();
        currentNodeInstance.setInstanceDataId(runtimeContext.getInstanceDataId());
        currentNodeInstance.setStatus(NodeInstanceStatus.COMPLETED);
        runtimeContext.getNodeInstanceList().add(currentNodeInstance);
    }

    /**
     * Calculate unique outgoing
     * Expression: one of flowElement's properties
     * Input: data map
     *
     * @return
     * @throws Exception
     */
    @Override
    protected RuntimeExecutor getExecuteExecutor(RuntimeContext runtimeContext) throws ProcessException {
        FlowElement nextNode = calculateNextNode(runtimeContext.getCurrentNodeModel(),
                runtimeContext.getFlowElementMap(), runtimeContext.getInstanceDataMap());

        runtimeContext.setCurrentNodeModel(nextNode);
        return getElementExecutor(nextNode);
    }


    /**
     * POST 请求
     * @Description
     * @param url 地址
     * @param params 参数
     * @param clazz 返参类型
     * @return 返回值
     * @throws Exception
     */
    private  <T> T postJson (String url, @Nullable Object params, Class<T> clazz) throws URISyntaxException {
        assert params != null;
        log.info("开始post请求，请求地址{}，请求参数{}，返回参数类型{}", url, params, clazz.getName());
        URI uri;
        T t;
        try {
            uri = new URI(url);
            t = turboRestTemplate.postForObject(uri, params, clazz);
        } catch (Exception e) {
            log.error("post请求异常，{}", e.getMessage());
            throw e;
        }
        return t;
    }
}
