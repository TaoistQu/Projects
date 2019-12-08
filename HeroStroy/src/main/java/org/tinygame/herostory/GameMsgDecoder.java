package org.tinygame.herostory;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author QuLei
 */
public class GameMsgDecoder extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgDecoder.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof BinaryWebSocketFrame)) {
            return;
        }
        //WebSocket二进制消息会通过HttpServerCodec解码成BinaryWebSocketFrame对象
        BinaryWebSocketFrame frame = (BinaryWebSocketFrame) msg;
        ByteBuf byteBuf = frame.content();
        byteBuf.readerIndex(2);
        short msgCode = byteBuf.readShort();
        Message.Builder msgBuilder = GameMsgRecognizer.getBuilderByMsgCode(msgCode);
        if (null == msgBuilder) {
            LOGGER.error("无法识别的消息, msgCode = {}", msgCode);
            return;
        }
        byte[] msgBody = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(msgBody);
        msgBuilder.clear();
        msgBuilder.mergeFrom(msgBody);
        Message newMsg = msgBuilder.build();
        if (null != newMsg) {
            ctx.fireChannelRead(newMsg);
        }
    }
}
