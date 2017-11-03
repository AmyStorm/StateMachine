package com.zzy.util.http;

import com.zzy.util.http.handler.HttpClientCallback;
import com.zzy.util.http.handler.HttpClientInboundHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class HttpNettyClient{

    private Logger logger = LoggerFactory.getLogger(HttpNettyClient.class);
    private HttpClientCallback callback;

    private Map<String, TreeSet<HttpSendingObj>> callingObjMap = new HashMap<String, TreeSet<HttpSendingObj>>();
    private Long connectionTimeout = 10 * 1000L;

    public Map<String, TreeSet<HttpSendingObj>> getCallingObjMap() {
        return callingObjMap;
    }

    public HttpNettyClient(List<HttpSendingObj> httpSendingObjList, HttpClientCallback callback){
        if(callback == null){
            throw new RuntimeException("回调方法不能为空");
        }
        this.callback = callback;
        initContainer(httpSendingObjList);
    }
    public HttpNettyClient(Map<String, TreeSet<HttpSendingObj>> callingObjMap, HttpClientCallback callback){
        this.callback = callback;
        this.callingObjMap = callingObjMap;
    }

    private void initContainer(List<HttpSendingObj> httpSendingObjList){
        convertMap(arrangeMap(httpSendingObjList));
    }

    private Map<String, TreeSet<HttpSendingObj>> arrangeMap(List<HttpSendingObj> httpSendingObjList){
        for(HttpSendingObj obj : httpSendingObjList){
            if(callingObjMap.get(obj.getGroupId()) == null){
                TreeSet<HttpSendingObj> treeSet = new TreeSet<HttpSendingObj>();
                treeSet.add(obj);
                callingObjMap.put(obj.getGroupId(), treeSet);
            }else{
                callingObjMap.get(obj.getGroupId()).add(obj);
            }
        }

        return callingObjMap;
    }
    private void convertMap(Map<String, TreeSet<HttpSendingObj>> executingObjMap){
        if(executingObjMap.size() > 0){
            callback.beforeCalling(executingObjMap);
        }
    }

    public void connect() throws UnsupportedEncodingException, InterruptedException, URISyntaxException {

            if(callingObjMap.size() > 0){
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                try {
                    for (String sendingObjKey : callingObjMap.keySet()) {
                        HttpSendingObj lastReq = null;
                        for(HttpSendingObj ele : callingObjMap.get(sendingObjKey)){
                            Bootstrap bootstrap = new Bootstrap();
                            URI uri = ele.getExecutingUri();
                            bootstrap.remoteAddress(uri.getHost(), uri.getPort());
                            bootstrap = createBootstrap(bootstrap, workerGroup, ele);
                            connecting(bootstrap, uri, ele.httpBody(), ele.method(), 0, lastReq);
                            lastReq = ele;
                        }
                    }

                }catch(Exception e){
                    logger.error("连接失败", e);
                }
                finally {
//                    workerGroup.shutdownGracefully();
                }
            }
    }
    public Bootstrap createBootstrap(Bootstrap bootstrap, EventLoopGroup eventLoop, final HttpSendingObj reqDetail) {
        if (bootstrap != null) {
            bootstrap.group(eventLoop);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
                    ch.pipeline().addLast(new HttpResponseDecoder());
                    // 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
                    ch.pipeline().addLast(new HttpRequestEncoder());
//                    ch.pipeline().addLast(new ReadTimeoutHandler(10));
//                    ch.pipeline().addLast(new WriteTimeoutHandler(10));
//                    ch.pipeline().addLast(reconnectInboundHandler);
//                    ch.pipeline().addLast(new SyncOutboundHandler(httpReqService, lastDetail));
                    ch.pipeline().addLast(new HttpClientInboundHandler(callback, reqDetail));
                }
            });
//            bootstrap.connect().addListener(new ReconnectListener(this));
        }
        return bootstrap;
    }

    private ChannelFuture connecting(final Bootstrap b, final URI uri, final String message, final HttpMethod method, final Integer times, final HttpSendingObj lastReq) throws InterruptedException, UnsupportedEncodingException {
        if(uri == null){
            throw new RuntimeException("uri is not valid");
        }
        ChannelFuture f = b.connect(uri.getHost(), uri.getPort()).sync();
        ChannelFutureListener futureListener = new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    logger.info("连接服务器成功");
                } else {
                    logger.info("连接服务器失败");
                    if(times > 5){
                        logger.info("连接服务器失败超过5次");
                    }else{
                        //  3秒后重新连接
                        future.channel().eventLoop().schedule(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    connecting(b, uri, message, method,times + 1, lastReq);
                                } catch (InterruptedException | UnsupportedEncodingException e) {
                                    logger.info("连接服务器出错", e);
                                }
                            }
                        }, 5, TimeUnit.SECONDS);
                    }
                }
            }
        };

        ChannelFutureListener lastSuccListener = new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    final long stub = System.currentTimeMillis();
                    while(true){
                        boolean isConnectSuccess = callback.isCallingSuccess(lastReq);
                        if(!isConnectSuccess){
                            if(System.currentTimeMillis() - stub <= connectionTimeout){
                                Thread.sleep(500);
                                continue;
                            }else{
                                logger.error("timeout break!");
                                future.removeListener(this);
                                future.channel().close();
                                break;
                            }
                        }else{
                            break;
                        }
                    }
                }
            }
        };
        f.addListener(futureListener);
        if(lastReq != null){
            f.addListener(lastSuccListener);
        }
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method,
                uri.toASCIIString(), Unpooled.wrappedBuffer(message != null ? message.getBytes("UTF-8") : new byte[0]));

        // 构建http请求
        request.headers().set(HttpHeaderNames.HOST, uri.getHost());
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
        // 发送http请求
        f.channel().write(request);
        f.channel().flush();
        f.channel().closeFuture();
        return f;
    }

    public void reconnect(List<HttpSendingObj> list) throws InterruptedException, UnsupportedEncodingException, URISyntaxException {
        if(list != null && callback != null){
//            Map<String, TreeSet<HttpSendingObj>> map = callback.beforeReconnectCalling(list);
//            HttpNettyClient nettyClient = new HttpNettyClient(map, callback);
//            nettyClient.connect();
            this.callingObjMap = callback.beforeReconnectCalling(list);
            this.connect();
        }
    }
}