package com.turbo.engine.bo;

import java.util.Map;

public class ElementInstanceBO {

    private String modelKey;
    private String modelName;
    private Map<String, Object> properties;
    private int status;

    public ElementInstanceBO() {
        super();
    }

    public ElementInstanceBO(String modelKey, int status) {
        super();
        this.modelKey = modelKey;
        this.status = status;
    }

    public String getModelKey() {
        return modelKey;
    }

    public void setModelKey(String modelKey) {
        this.modelKey = modelKey;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
