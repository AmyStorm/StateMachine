package com.zzy.util.http.handler;


import com.zzy.util.http.HttpSendingObj;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public interface HttpClientCallback {

    void beforeCalling(Map<String, TreeSet<HttpSendingObj>> objMap);

    boolean isCallingSuccess(HttpSendingObj sendingObj);

    void afterCallingSuccess(HttpSendingObj sendingObj);

    Map<String, TreeSet<HttpSendingObj>> beforeReconnectCalling(List<HttpSendingObj> objList);
}
