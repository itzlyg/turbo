package com.turbo.engine.config;

import com.turbo.engine.common.Constants;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {
    @Resource
    private HookProperties properties;

    @Bean(name = "turboRestTemplate")
    public RestTemplate turboRestTemplate(){
        Integer timeout = properties.getTimeOut();
        if (timeout == null) {
            timeout = Constants.DEFAULT_TIMEOUT;
        }
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        return new RestTemplate(factory);
    }
}
