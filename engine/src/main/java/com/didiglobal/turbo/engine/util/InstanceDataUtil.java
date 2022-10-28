package com.didiglobal.turbo.engine.util;

import com.didiglobal.turbo.engine.common.DataType;
import com.didiglobal.turbo.engine.model.InstanceDataModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

public class InstanceDataUtil {

    private InstanceDataUtil() {}

    public static Map<String, InstanceDataModel> getInstanceDataMap(List<InstanceDataModel> instanceDataList) {
        if (CollectionUtils.isEmpty(instanceDataList)) {
            return new HashMap<>();
        }
        Map<String, InstanceDataModel> instanceDataMap = new HashMap<>();
        instanceDataList.forEach(instanceData -> {
            instanceDataMap.put(instanceData.getKey(), instanceData);
        });
        return instanceDataMap;
    }

    public static Map<String, InstanceDataModel> getInstanceDataMap(String instanceDataStr) {
        if (StringUtils.isBlank(instanceDataStr)) {
            return new HashMap<>();
        }
        List<InstanceDataModel> instanceDataList = JsonUtil.toBeans(instanceDataStr, InstanceDataModel.class);
        return getInstanceDataMap(instanceDataList);
    }

    public static List<InstanceDataModel> getInstanceDataList(Map<String, InstanceDataModel> instanceDataMap) {
        if (MapUtils.isEmpty(instanceDataMap)) {
            return new ArrayList<>();
        }
        List<InstanceDataModel> instanceDataList = new ArrayList<>();
        instanceDataMap.forEach((key, instanceData) -> {
            instanceDataList.add(instanceData);
        });
        return instanceDataList;
    }

    public static String getInstanceDataListStr(Map<String, InstanceDataModel> instanceDataMap) {
        if (MapUtils.isEmpty(instanceDataMap)) {
            return JsonUtil.toJson(CollectionUtils.EMPTY_COLLECTION);
        }
        return JsonUtil.toJson(instanceDataMap.values());
    }

    public static Map<String, Object> parseInstanceDataMap(Map<String, InstanceDataModel> instanceDataMap) {
        if (MapUtils.isEmpty(instanceDataMap)) {
            return new HashMap<>();
        }
        Map<String, Object> dataMap = new HashMap<>();
        instanceDataMap.forEach((keyName, instanceData) -> {
            dataMap.put(keyName, parseInstanceData(instanceData));
        });
        return dataMap;
    }

    private static Object parseInstanceData(InstanceDataModel instanceData) {
        if (instanceData == null) {
            return null;
        }
        String dataTypeStr = instanceData.getType();
        DataType dataType = DataType.getType(dataTypeStr);

        // TODO: 2019/12/16
        return instanceData.getValue();
    }
}
