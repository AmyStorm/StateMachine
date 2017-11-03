package com.zzy.entity;

import java.util.Date;

public class ReqBatch {
    private String reqBatchId;

    private String reqBatchStatus = "0";

    private String objIdentifier;

    private Date createTime = new Date();

    private Date updateTime = new Date();

    public ReqBatch(){

    }
    public ReqBatch(String reqBatchId, String objIdentifier) {
        this.reqBatchId = reqBatchId;
        this.objIdentifier = objIdentifier;
    }

    public String getReqBatchId() {
        return reqBatchId;
    }

    public void setReqBatchId(String reqBatchId) {
        this.reqBatchId = reqBatchId == null ? null : reqBatchId.trim();
    }

    public String getReqBatchStatus() {
        return reqBatchStatus;
    }

    public void setReqBatchStatus(String reqBatchStatus) {
        this.reqBatchStatus = reqBatchStatus == null ? null : reqBatchStatus.trim();
    }

    public String getObjIdentifier() {
        return objIdentifier;
    }

    public void setObjIdentifier(String objIdentifier) {
        this.objIdentifier = objIdentifier == null ? null : objIdentifier.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}