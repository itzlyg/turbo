package com.turbo.demo.pojo.request;

import com.turbo.demo.util.Constant;
import com.turbo.engine.param.DeployFlowParam;

/**
 * @Author: james zhangxiao
 * @Date: 4/1/22
 * @Description:
 */
public class DeployFlowRequest extends DeployFlowParam {

    public DeployFlowRequest() {
        super(Constant.tenant, Constant.caller);
    }
}
