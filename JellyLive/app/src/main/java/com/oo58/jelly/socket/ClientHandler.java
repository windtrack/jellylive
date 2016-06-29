package com.oo58.jelly.socket;

import android.os.Handler;
import android.util.Log;


import com.oo58.jelly.entity.room.ActivityMsg;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * Desc: 服务器消息回调
 * Created by sunjinfang on 2016/5/11.
 */
public class ClientHandler extends SimpleChannelUpstreamHandler {

    private Handler mHandler; //消息回调
    private int msgid; //成功的回调id
    private int errormsgid;//失败的回调id

    /**
     * 消息回调的初始化
     *
     * @param paramHandler        回调的接收体
     * @param sendMessageHandleId 成功的回调id
     * @param errorHandleId       失败的回调id
     */
    public ClientHandler(Handler paramHandler, int sendMessageHandleId, int errorHandleId) {
        this.mHandler = paramHandler;
        this.msgid = sendMessageHandleId;
        this.errormsgid = errorHandleId;
    }

    /**
     * socket连接成功
     *
     * @param paramChannelHandlerContext
     * @param paramChannelStateEvent
     * @throws Exception
     */
    public void channelConnected(
            ChannelHandlerContext paramChannelHandlerContext,
            ChannelStateEvent paramChannelStateEvent) throws Exception {
        Log.i("sjf", "------------------------ ClientHandler socket连接--------------------------");
        if (this.mHandler != null) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(80001, null));
        }

    }

    /**
     * socket连接异常
     *
     * @param paramChannelHandlerContext
     * @param paramExceptionEvent
     * @throws Exception
     */
    public void exceptionCaught(ChannelHandlerContext paramChannelHandlerContext, ExceptionEvent paramExceptionEvent) throws Exception {
        Log.i("sjf", "------------------------ClientHandler socket异常--------------------------");
        Channel localChannel = paramExceptionEvent.getChannel();
        if (this.mHandler != null) {
            this.mHandler.sendEmptyMessage(errormsgid);
        }

        if (localChannel != null)
            localChannel.close();
    }

    /**
     * 服务器返回的消息 回调到当前接收位置
     *
     * @param paramChannelHandlerContext
     * @param paramMessageEvent
     * @throws Exception
     */
    public void messageReceived(
            ChannelHandlerContext paramChannelHandlerContext,
            MessageEvent paramMessageEvent) throws Exception {
        //获取返回的消息 ActivityMsg msg
        Object buffs = paramMessageEvent.getMessage();
        if (buffs instanceof ActivityMsg) {
            ActivityMsg msg = (ActivityMsg) buffs;
            if (this.mHandler != null) {
                this.mHandler.sendMessage(this.mHandler.obtainMessage(msgid, msg));
            }
        }
    }

}