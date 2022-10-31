package com.turbo.engine.model;

public class InstanceDataModel {
    private String key;
    private String type;
    private Object value;

    public InstanceDataModel(){

    }
    public InstanceDataModel(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public InstanceDataModel(String key, String type, Object value) {
        this.key = key;
        this.type = type;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
