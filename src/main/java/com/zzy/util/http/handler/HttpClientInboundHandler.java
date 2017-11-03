package com.zzy.util.http.handler;

import com.zzy.util.http.HttpSendingObj;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientInboundHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientInboundHandler.class);

    private HttpClientCallback callback;
    private HttpSendingObj sendingObj;

    public HttpClientInboundHandler(HttpClientCallback callback, HttpSendingObj sendingObj) {
        this.callback = callback;
        this.sendingObj = sendingObj;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpResponse)
        {
            HttpResponse response = (HttpResponse) msg;
            logger.info("HTTP response:" + String.valueOf(response.status()));
            if(response.status().code() == 200){
                callback.afterCallingSuccess(sendingObj);
//                httpReqService.updateReqDetail(reqDetail, true);
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            }
        }
        if(msg instanceof HttpContent)
        {
            HttpContent content = (HttpContent)msg;
            ByteBuf buf = content.content();
            logger.info("HTTP response body:" + buf.toString(io.netty.util.CharsetUtil.UTF_8));
            buf.release();
        }
    }
}