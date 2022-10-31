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
* 节点执行日志表表
* @Description
* @Copyright Copyright (c) 2022
* @author xieyubin
* @since 2022-10-31 15:17:34
*/
@TableName("ei_node_instance_log")
public class NodeInstanceLog implements Serializable {

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

    /** 节点唯一标识 */
    @TableField("node_key")
    private String nodeKey;

    /** 业务方标识 */
    @TableField("tenant_id")
    private String tenantId;

    /** 操作类型;1.系统执行 2.任务提交 3.任务撤销 */
    @TableField("type")
    private Integer type;

    /** 状态;1.处理成功 2.处理中 3.处理失败 4.处理已撤销 */
    @TableField("fl_status")
    private Integer flStatus;

    /** 归档状态;0未删除，1删除 */
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getFlStatus() {
        return flStatus;
    }

    public void setFlStatus(Integer flStatus) {
        this.flStatus = flStatus;
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
