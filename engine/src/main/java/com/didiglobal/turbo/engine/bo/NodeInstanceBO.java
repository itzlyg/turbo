package com.didiglobal.turbo.engine.bo;

public class NodeInstanceBO {
    //used while updateById
    private String id;
    private String nodeInstanceId;
    private String nodeKey;
    private String sourceNodeInstanceId;
    private String sourceNodeKey;
    private String instanceDataId;
    private int status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNodeInstanceId() {
        return nodeInstanceId;
    }

    public void setNodeInstanceId(String nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
    }

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public String getSourceNodeInstanceId() {
        return sourceNodeInstanceId;
    }

    public void setSourceNodeInstanceId(String sourceNodeInstanceId) {
        this.sourceNodeInstanceId = sourceNodeInstanceId;
    }

    public String getSourceNodeKey() {
        return sourceNodeKey;
    }

    public void setSourceNodeKey(String sourceNodeKey) {
        this.sourceNodeKey = sourceNodeKey;
    }

    public String getInstanceDataId() {
        return instanceDataId;
    }

    public void setInstanceDataId(String instanceDataId) {
        this.instanceDataId = instanceDataId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
