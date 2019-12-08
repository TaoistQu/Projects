package org.tinygame.herostory.cmdHandler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author QuLei
 */
public interface ICmdHandler<TCmd extends GeneratedMessageV3> {
    /**
     * @param ctx
     * @param cmd
     */
    void handle(ChannelHandlerContext ctx, TCmd cmd);
}
