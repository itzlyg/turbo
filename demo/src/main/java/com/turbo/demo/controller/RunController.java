package com.turbo.demo.controller;

import com.turbo.demo.pojo.request.HookRequest;
import com.turbo.demo.pojo.response.HookResponse;
import com.turbo.demo.service.AfterSaleServiceImpl;
import com.turbo.demo.service.LeaveServiceImpl;
import com.turbo.engine.util.JsonUtil;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RunController {
    private static final Logger log = LoggerFactory.getLogger(RunController.class);
    @Resource
    private AfterSaleServiceImpl service;

    @Resource
    private LeaveServiceImpl leaveService;

    @GetMapping(value = "/run")
    public String run() {
        service.run();
        return String.valueOf(LocalDateTime.now());
    }

    @GetMapping(value = "/leave")
    public String leave() {
        leaveService.run();
        return String.valueOf(LocalDateTime.now());
    }


    @PostMapping(value = "/hook")
    public HookResponse hookData(@RequestBody HookRequest request){
        log.info(JsonUtil.toJson(request));
        HookResponse response = new HookResponse();
        Map<String, Object> data = new HashMap<>();
        String hookInfoParam = request.getHookInfoParam();
        String[] params = StringUtils.split(hookInfoParam, ",");
        for (int i = 0; i < params.length; i++) {
            data.put(params[i], String.valueOf(i + 1));
        }
        response.setStatus(1);
        response.setData(data);
        return response;
    }

}
