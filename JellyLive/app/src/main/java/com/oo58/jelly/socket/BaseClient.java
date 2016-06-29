
package com.oo58.jelly.socket;

import android.os.Handler;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;


/**
 * Desc: 基础的socket连接封装
 * Created by sunjinfang on 2016/5/11.
 */
public class BaseClient {

	ClientBootstrap bootstrap;
	Channel ch;
	ChannelFuture channelFuture = null;

	/**
	 * 关闭socket连接
	 */
	public void disconnect() {
		try {
			Channel localChannel = this.channelFuture.awaitUninterruptibly()
					.getChannel();
			if (this.ch != null) {
				this.ch.disconnect();
				this.ch.close();
				this.ch = null;
			}
			if (localChannel != null)
				localChannel.close().awaitUninterruptibly();
			if (this.bootstrap != null)
				this.bootstrap.releaseExternalResources();
			if (this.channelFuture != null)
				this.channelFuture.getChannel().close();
			return;
		} catch (Exception localException) {
			while (true)
				localException.printStackTrace();
		}
	}

	/**
	 * 发送一条消息
	 * @param paramString  消息体
	 * @return boolean
     */
	public boolean sendmsg(byte[] paramString) {
		if ((this.ch != null) && (this.ch.isConnected())) {
			this.ch.write(ChannelBuffers.wrappedBuffer(paramString));
		}
		for (boolean bool = true;; bool = false) {
			return bool;
		}
	}

	/**
	 * 连接服务器
	 * @param url 服务器地址
	 * @param port 端口号
	 * @param paramHandler  消息的回调
	 * @param msgid  返回消息时 handler的 接收id
	 * @param errormsgid 返回失败 消息时 handler的 接收id
     * @throws Exception
     */
	public void start(String url, int port,
			final Handler paramHandler,final int msgid,final int errormsgid) throws Exception {
		ChannelFactory factory = new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
		ClientBootstrap bootstrap = new ClientBootstrap(factory);
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("encode", new StringEncoder());
				pipeline.addLast("decode", new DecoderUtils());
				pipeline.addLast("handler", new ClientHandler(paramHandler,msgid,errormsgid));
				return pipeline;
			}
		});
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		channelFuture = bootstrap.connect(new InetSocketAddress(url,port));
		channelFuture.awaitUninterruptibly();
		ch = channelFuture.awaitUninterruptibly().getChannel();
	}
}