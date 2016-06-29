package com.oo58.jelly.socket;




import com.oo58.jelly.entity.room.ActivityMsg;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * @author sunjinfang
 *
 */
public class DecoderUtils  extends FrameDecoder{


	@Override
	protected Object decode(ChannelHandlerContext arg0, Channel arg1,
			ChannelBuffer in) throws Exception {
		ActivityMsg msg = new ActivityMsg();
		in.markReaderIndex();
		if (in.readableBytes() > 4) {
			int length = in.readInt();
			if (length < 0) {
				in.resetReaderIndex();
				return null;
			} else if (in.readableBytes() < length - 4) {
				in.resetReaderIndex();
				return null;
			}
			int tid = in.readInt();
			int bodyLen = in.readInt();
			byte[] value = new byte[bodyLen];
			in.readBytes(value);
			String text = BigEndianUtils.readUTF(value);
			msg.setTid(tid);
			msg.setMsg(text);
		}
		return msg;
	}
}
