package com.zzy.api;


import com.zzy.entity.ReqBatch;
import com.zzy.entity.ReqDetail;

import java.util.List;

public interface HttpReqService {

    ReqBatch getReqBatch(String reqBatchId);

    ReqBatch saveReqBatch(String objIdentifier);

    ReqDetail saveReqDetail(String reqBatchId, String url, String body, String method, String req);

    void updateReqBatch(ReqBatch reqBatch, boolean isSuccess);

    void updateReqDetail(ReqDetail reqDetail, boolean isSuccess);

    ReqDetail getReqDetailById(String reqDetailId);

    List<ReqDetail> getFailedReqDetails(List<String> objIds);

    List<ReqDetail> getFailedReqDetailsByReqBatch(String reqBatchId);
}
