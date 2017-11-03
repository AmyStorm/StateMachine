package com.zzy.entity;

import java.util.Date;

public class ReqDetail {
    private String reqDetailId;

    private String reqBatchId;

    private String sendStatus = "0";

    private String sendTarget;

    private String sendBody;

    private String sendSeq;

    private String sendMethod;

    private Date createTime = new Date();

    private Date updateTime = new Date();

    public ReqDetail(){

    }

    public ReqDetail(String reqDetailId, String reqBatchId, String sendTarget, String sendBody, String sendMethod, String sendSeq) {
        this.reqDetailId = reqDetailId;
        this.reqBatchId = reqBatchId;
        this.sendTarget = sendTarget;
        this.sendBody = sendBody;
        this.sendMethod = sendMethod;
        this.sendSeq = sendSeq;
    }

    public String getReqDetailId() {
        return reqDetailId;
    }

    public void setReqDetailId(String reqDetailId) {
        this.reqDetailId = reqDetailId == null ? null : reqDetailId.trim();
    }

    public String getReqBatchId() {
        return reqBatchId;
    }

    public void setReqBatchId(String reqBatchId) {
        this.reqBatchId = reqBatchId == null ? null : reqBatchId.trim();
    }

    public String getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus == null ? null : sendStatus.trim();
    }

    public String getSendTarget() {
        return sendTarget;
    }

    public void setSendTarget(String sendTarget) {
        this.sendTarget = sendTarget == null ? null : sendTarget.trim();
    }

    public String getSendBody() {
        return sendBody;
    }

    public void setSendBody(String sendBody) {
        this.sendBody = sendBody == null ? null : sendBody.trim();
    }

    public String getSendSeq() {
        return sendSeq;
    }

    public void setSendSeq(String sendSeq) {
        this.sendSeq = sendSeq == null ? null : sendSeq.trim();
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

    public String getSendMethod() {
        return sendMethod;
    }

    public void setSendMethod(String sendMethod) {
        this.sendMethod = sendMethod;
    }
}