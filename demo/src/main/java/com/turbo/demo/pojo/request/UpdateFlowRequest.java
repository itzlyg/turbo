package com.turbo.demo.pojo.request;

import com.turbo.demo.util.Constant;
import com.turbo.engine.param.UpdateFlowParam;

/**
 * @Author: james zhangxiao
 * @Date: 4/1/22
 * @Description:
 */
public class UpdateFlowRequest extends UpdateFlowParam {

    public UpdateFlowRequest() {
        super(Constant.tenant, Constant.caller);
    }
}
