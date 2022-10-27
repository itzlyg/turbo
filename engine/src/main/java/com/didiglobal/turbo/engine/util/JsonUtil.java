package com.didiglobal.turbo.engine.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.List;

public class JsonUtil {

    public static <T> List<T> toBeans (String json, Class<T> clazz){
        return JSON.parseArray(json, clazz);
    }

    public static <T> T toBean (String json, Class<T> clazz){
        return JSON.parseObject(json, clazz);
    }

    public static String toJson (Object data){
        return JSONObject.toJSONString(data);
    }
}
