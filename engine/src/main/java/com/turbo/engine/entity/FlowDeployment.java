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
* 流程部署表表
* @Description
* @Copyright Copyright (c) 2022
* @author xieyubin
* @since 2022-10-31 15:07:51
*/
@TableName("em_flow_deployment")
public class FlowDeployment implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /** 流程模型部署id */
    @TableField(value = "flow_deploy_id")
    private String flowDeployId;

    /** 流程模型id */
    @TableField("flow_module_id")
    private String flowModuleId;

    /** 流程名称 */
    @TableField("flow_name")
    private String flowName;

    /** 流程业务标识 */
    @TableField("flow_key")
    private String flowKey;

    /** 业务方标识 */
    @TableField("tenant_id")
    private String tenantId;

    /** 表单定义 */
    @TableField("flow_model")
    private String flowModel;

    /** 操作人 */
    @TableField("operator")
    private String operator;

    /** 状态;1.已部署 3.已下线 */
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

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getFlowKey() {
        return flowKey;
    }

    public void setFlowKey(String flowKey) {
        this.flowKey = flowKey;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getFlowModel() {
        return flowModel;
    }

    public void setFlowModel(String flowModel) {
        this.flowModel = flowModel;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
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
