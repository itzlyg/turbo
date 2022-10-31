package com.turbo.engine.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
* 实例数据表表
* @Description
* @Copyright Copyright (c) 2022
* @author xieyubin
* @since 2022-10-31 15:17:34
*/
@TableName("ei_instance_data")
public class InstanceData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键id */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /** 节点执行实例id */
    @TableField("node_instance_id")
    private String nodeInstanceId;

    /** 流程执行实例id */
    @TableField("flow_instance_id")
    private String flowInstanceId;

    /** 实例数据id */
    @TableField("instance_data_id")
    private String instanceDataId;

    /** 流程模型部署id */
    @TableField("flow_deploy_id")
    private String flowDeployId;

    /** 流程模型id */
    @TableField("flow_module_id")
    private String flowModuleId;

    /** 节点唯一标识 */
    @TableField("node_key")
    private String nodeKey;

    /** 业务方标识 */
    @TableField("tenant_id")
    private String tenantId;

    /** 数据列表json */
    @TableField("instance_data")
    private String instanceData;

    /** 操作类型;1.实例初始化 2.系统执行 3.系统主动获取 4.上游更新 5.任务提交 6.任务撤回 */
    @TableField("type")
    private Integer type;

    /** 归档状态 */
    @TableField("archive")
    private Integer archive;

    /** 租户 */
    @TableField("tenant")
    private String tenant;

    /** 调用方 */
    @TableField("caller")
    private String caller;

    /** 创建时间 */
    @TableField(value = "created_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createdTime;
    /** 最后更新时间 */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

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

    public String getFlowInstanceId() {
        return flowInstanceId;
    }

    public void setFlowInstanceId(String flowInstanceId) {
        this.flowInstanceId = flowInstanceId;
    }

    public String getInstanceDataId() {
        return instanceDataId;
    }

    public void setInstanceDataId(String instanceDataId) {
        this.instanceDataId = instanceDataId;
    }

    public String getFlowDeployId() {
        return flowDeployId;
    }

    public void setFlowDeployId(String flowDeployId) {
        this.flowDeployId = flowDeployId;
    }

    public String getFlowModuleId() {
        return flowModuleId;
    }

    public void setFlowModuleId(String flowModuleId) {
        this.flowModuleId = flowModuleId;
    }

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getInstanceData() {
        return instanceData;
    }

    public void setInstanceData(String instanceData) {
        this.instanceData = instanceData;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getArchive() {
        return archive;
    }

    public void setArchive(Integer archive) {
        this.archive = archive;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}
