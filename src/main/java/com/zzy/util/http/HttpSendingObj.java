package com.zzy.util.http;

import io.netty.handler.codec.http.HttpMethod;

import java.net.URI;

public interface HttpSendingObj extends Comparable{
    URI getExecutingUri();
    String getGroupId();
    String httpBody();
    HttpMethod method();
}
