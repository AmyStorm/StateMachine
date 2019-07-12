package com.zzy.util.http;

import com.zzy.api.HttpReqService;
import com.zzy.entity.ReqBatch;
import com.zzy.entity.ReqDetail;
import com.zzy.util.http.handler.HttpClientCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class HttpClientInvoker {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientInvoker.class);
    private static HttpReqService httpReqService = null;
//    private static HttpReqService httpReqService = (HttpReqService) SpringBeanUtils.getBean(HttpReqService.class);
    private static HttpClientCallback callback = new HttpClientCallback() {

        private Map<HttpSendingObj, ReqDetail> objMapping = new HashMap<HttpSendingObj, ReqDetail>();
        private Map<String, HttpSendingObj> objReverseMapping = new HashMap<String, HttpSendingObj>();
        @Override
        public void beforeCalling(Map<String, TreeSet<HttpSendingObj>> objMap) {
            for(Map.Entry<String, TreeSet<HttpSendingObj>> sendingObjKey : objMap.entrySet()){
                ReqBatch reqBatch = httpReqService.saveReqBatch(sendingObjKey.getKey());
                int seq = 1;
                for(HttpSendingObj ele : sendingObjKey.getValue()){
                    URI uri = ele.getExecutingUri();
                    ReqDetail reqDetail = httpReqService.saveReqDetail(reqBatch.getReqBatchId(), uri.toString(), ele.httpBody(), ele.method().name(), String.valueOf(seq));
                    seq++;
                    objMapping.put(ele, reqDetail);
                    objReverseMapping.put(reqDetail.getReqDetailId(), ele);
                }
            }
        }

        @Override
        public boolean isCallingSuccess(HttpSendingObj sendingObj) {
            if(objMapping.get(sendingObj) != null){
                ReqDetail req = httpReqService.getReqDetailById(objMapping.get(sendingObj).getReqDetailId());
                return "1".equals(req.getSendStatus());
            }else{
                return false;
            }
        }

        @Override
        public void afterCallingSuccess(HttpSendingObj sendingObj) {
            ReqDetail reqDetail = objMapping.get(sendingObj);
            if(reqDetail != null){
                httpReqService.updateReqDetail(reqDetail, true);
                List<ReqDetail> failedReqDetails = httpReqService.getFailedReqDetailsByReqBatch(reqDetail.getReqBatchId());
                if(failedReqDetails != null && failedReqDetails.size() > 0){
                    //未成功
                }else{
                    //已成功
                    ReqBatch reqBatch = httpReqService.getReqBatch(reqDetail.getReqBatchId());
                    if(reqBatch != null){
                        httpReqService.updateReqBatch(reqBatch, true);
                    }
                }
            }
        }

        @Override
        public Map<String, TreeSet<HttpSendingObj>> beforeReconnectCalling(List<HttpSendingObj> objList) {
        final List<String> reqDetailIds = new ArrayList<String>();
        Map<String, TreeSet<HttpSendingObj>> map = new HashMap<String, TreeSet<HttpSendingObj>>();
        for (HttpSendingObj obj : objList) {
            if(objMapping.get(obj) != null){
                reqDetailIds.add(objMapping.get(obj).getReqDetailId());
            }
        }
        List<ReqDetail> failedReqDetails = httpReqService.getFailedReqDetails(reqDetailIds);
        if(failedReqDetails != null && failedReqDetails.size() > 0){

            for(ReqDetail detail : failedReqDetails){
                if(detail != null){
                    HttpSendingObj httpSendingObj = objReverseMapping.get(detail.getReqDetailId());
                    if(httpSendingObj != null){
                        if(map.get(detail.getReqBatchId()) != null){
                            map.get(detail.getReqBatchId()).add(httpSendingObj);
                        }else{
                            TreeSet<HttpSendingObj> treeSet = new TreeSet<HttpSendingObj>();
                            treeSet.add(httpSendingObj);
                            map.put(httpSendingObj.getGroupId(), treeSet);
                        }
                    }
                }
            }
        }else{
            logger.info("无失败条目");
        }
        return map;
    }
};
    private HttpClientInvoker(){

    }

    public static void invokeHttpRequest(final List<? extends HttpSendingObj> httpSendingObjList, int retryTime){
        final List<HttpSendingObj> list = new ArrayList<HttpSendingObj>();
        list.addAll(httpSendingObjList);
        HttpNettyClient client = null;
        try {
            client = new HttpNettyClient(list, callback);
            client.connect();
        } catch (UnsupportedEncodingException | InterruptedException | URISyntaxException e) {
            logger.error("请求失败", e);
        }

        Timer timer = new Timer();
        if(retryTime <= 0){
            retryTime = 1;
        }
        for(int i = 0; i < retryTime; i++){
            final HttpNettyClient finalClient = client;
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        finalClient.reconnect(list);
                    } catch (InterruptedException | UnsupportedEncodingException | URISyntaxException e) {
                        logger.error("重新请求失败", e);
                    }
                }
            };
            timer.schedule(task, 30 * (i + 1) * 1000L);
        }
    }
}
