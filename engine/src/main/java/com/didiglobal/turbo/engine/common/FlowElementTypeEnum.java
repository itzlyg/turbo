package com.didiglobal.turbo.engine.common;

public enum FlowElementTypeEnum {
    SEQUENCE_FLOW(1, "sequenceFlowExecutor"),

    START_EVENT(2, "startEventExecutor"),

    END_EVENT(3, "endEventExecutor"),

    USER_TASK(4, "userTaskExecutor"),

    EXCLUSIVE_GATEWAY(6, "exclusiveGatewayExecutor"),
    ;

    private int code;

    private String name;

    FlowElementTypeEnum(int code, String name){
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static FlowElementTypeEnum byCode (int code){
        for (FlowElementTypeEnum e : values()) {
            if (code == e.getCode()) {
                return e;
            }
        }
        return null;
    }
}
