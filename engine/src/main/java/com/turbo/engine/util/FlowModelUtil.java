package com.turbo.engine.util;

import com.turbo.engine.common.Constants;
import com.turbo.engine.common.FlowElementTypeEnum;
import com.turbo.engine.model.FlowElement;
import com.turbo.engine.model.FlowModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowModelUtil {
    protected static final Logger LOGGER = LoggerFactory.getLogger(FlowModelUtil.class);

    private FlowModelUtil() {
    }

    /**
     * Parse flowModelStr to flowModel, put flowElement into map, with key=key and value=flowElement
     *
     * @param flowModelStr
     * @return flowElementMap or emptyMap
     */
    public static Map<String, FlowElement> getFlowElementMap(String flowModelStr) {
        if (StringUtils.isBlank(flowModelStr)) {
            return new HashMap<>();
        }
        FlowModel flowModel = parseModelFromString(flowModelStr);
        if (flowModel == null) {
            return new HashMap<>();
        }
        return getFlowElementMap(flowModel.getFlowElementList());
    }

    public static Map<String, FlowElement> getFlowElementMap(List<FlowElement> flowElementList) {
        if (CollectionUtils.isEmpty(flowElementList)) {
            return new HashMap<>();
        }
        Map<String, FlowElement> flowElementMap = new HashMap<>();
        flowElementList.forEach(flowElement -> {
            flowElementMap.put(flowElement.getKey(), flowElement);
        });
        return flowElementMap;
    }

    /**
     * Get startEvent node from flowModel
     *
     * @param flowModel
     * @return startEvent node or null
     */
    public static FlowElement getStartEvent(Map<String, FlowElement> flowModel) {
        for (FlowElement flowElement : flowModel.values()) {
            if (FlowElementTypeEnum.START_EVENT.getCode() == flowElement.getType()) {
                return flowElement;
            }
        }
        return null;
    }

    public static FlowElement getFlowElement(Map<String, FlowElement> flowModel, String elementKey) {
        return flowModel.get(elementKey);
    }

    /**
     * Get sequenceFlow between sourceNode and targetNode
     *
     * @param flowModel
     * @param sourceNodeKey
     * @param targetNodeKey
     * @return sequenceFlow or null
     */
    public static FlowElement getSequenceFlow(Map<String, FlowElement> flowModel, String sourceNodeKey, String targetNodeKey) {
        FlowElement sourceNode = getFlowElement(flowModel, sourceNodeKey);
        FlowElement targetNode = getFlowElement(flowModel, targetNodeKey);
        List<String> outgoingList = sourceNode.getOutgoing();
        List<String> incomingList = targetNode.getIncoming();
        for (String outgoing : outgoingList) {
            for (String incoming : incomingList) {
                if (outgoing.equals(incoming)) {
                    return getFlowElement(flowModel, outgoing);
                }
            }
        }
        return null;
    }

    /**
     * Parse flowModelStr to FlowModel without view info eg: bounds
     *
     * @param flowModelStr
     * @return flowModel
     */
    public static FlowModel parseModelFromString(String flowModelStr) {
        return JsonUtil.toBean(flowModelStr, FlowModel.class);
    }

    public static String getConditionFromSequenceFlow(FlowElement flowElement) {
        Map<String, Object> properties = flowElement.getProperties();
        return (String) properties.get(Constants.ELEMENT_PROPERTIES.CONDITION);
    }

    public static boolean isDefaultCondition(FlowElement flowElement) {
        Map<String, Object> properties = flowElement.getProperties();
        String isDefaultStr = (String) properties.get(Constants.ELEMENT_PROPERTIES.DEFAULT_CONDITION);
        return BooleanUtils.toBoolean(isDefaultStr);
    }

    public static String getHookInfos(FlowElement flowElement) {
        Map<String, Object> properties = flowElement.getProperties();
        return (String) properties.get(Constants.ELEMENT_PROPERTIES.HOOK_INFO_IDS);
    }

    public static int getElementType(String elementKey, Map<String, FlowElement> flowElementMap) {
        FlowElement flowElement = flowElementMap.get(elementKey);
        if (flowElement != null) {
            return flowElement.getType();
        }
        return -1;
    }

    public static boolean isElementType(String elementKey, Map<String, FlowElement> flowElementMap, int targetElementType) {
        int sourceElementType = getElementType(elementKey, flowElementMap);
        return sourceElementType == targetElementType;
    }

    public static String getElementName(FlowElement flowElement) {
        if (flowElement == null) {
            return StringUtils.EMPTY;
        }
        Map<String, Object> properties = flowElement.getProperties();
        if (MapUtils.isEmpty(properties)) {
            return StringUtils.EMPTY;
        }
        return StringUtils.defaultString((String) properties.get(Constants.ELEMENT_PROPERTIES.NAME), StringUtils.EMPTY);
    }
}
