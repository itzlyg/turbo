package com.didiglobal.turbo.engine.util;

import java.net.URI;
import java.net.URISyntaxException;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

public class HttpUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    private static final RestTemplate rest = new RestTemplate();

    private HttpUtil() {}

    /**
     * POST 请求
     * @Description
     * @param url 地址
     * @param params 参数
     * @param clazz 返参类型
     * @return 返回值
     * @throws Exception
     */
    public static <T> T postJson (String url, @Nullable Object params, Class<T> clazz) throws URISyntaxException {
        assert params != null;
        log.info("开始post请求，请求地址{}，请求参数{}，返回参数类型{}", url, params, clazz.getName());
        URI uri;
        T t;
        try {
            uri = new URI(url);
            t = rest.postForObject(uri, params, clazz);
        } catch (Exception e) {
            log.error("post请求异常，{}", e.getMessage());
            throw e;
        }
        return t;
    }
}
