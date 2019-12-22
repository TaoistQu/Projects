package org.tinygame.herostory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdHandler.CmdHandlerFactory;
import org.tinygame.herostory.login.MySqlSessionFactory;
import org.tinygame.herostory.mq.MQProducer;
import org.tinygame.herostory.util.RedisUtil;


/**
 * @author QuLei
 */
public class ServerMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);

    /**
     * 应用主函数
     *
     * @param args
     */
    public static void main(String[] args) {
        CmdHandlerFactory.init();
        GameMsgRecognizer.init();
        MySqlSessionFactory.init();
        RedisUtil.init();
        MQProducer.init();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(
                                        new HttpServerCodec(), //Http服务器编解码器
                                        new HttpObjectAggregator(65535), //内容长度限制
                                        new WebSocketServerProtocolHandler("/websocket"), //WebSocket协议解析，这里处理握手、ping、pong等信息
                                        new GameMsgDecoder(), //自定义消息解码器
                                        new GameMsgEncoder(),//自定义消息编码器
                                        new GameMsgHandler());
                    }
                });
        try {
            // 绑定 12345 端口,
            // 注意: 实际项目中会使用 argArray 中的参数来指定端口号
            ChannelFuture future = bootstrap.bind(12345).sync();
            if (future.isSuccess()) {
                LOGGER.info("服务器启动成功！！！");
            }
            // 等待服务器信道关闭,
            // 也就是不要立即退出应用程序, 让应用程序可以一直提供服务
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
