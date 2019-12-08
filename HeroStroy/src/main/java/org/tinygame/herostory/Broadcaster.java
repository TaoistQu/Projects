package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author QuLei
 */
public final class Broadcaster {
    /**
     * 客户端信道数组, 一定要使用 static, 否则无法实现群发
     */
    private static final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 私有化构造方法
     */
    private Broadcaster() {
    }

    /**
     * 添加信道
     *
     * @param channel
     */
    public static void addChannel(Channel channel) {
        _channelGroup.add(channel);
    }

    /**
     * 广播消息
     *
     * @param msg
     */
    public static void broadMsg(GeneratedMessageV3 msg) {
        if (null == msg) {
            return;
        }
        _channelGroup.writeAndFlush(msg);
    }

    /**
     * 移除信道
     *
     * @param channel
     */
    public static void removeChannel(Channel channel) {
        _channelGroup.remove(channel);
    }
}
