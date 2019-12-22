package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.mq.MQProducer;
import org.tinygame.herostory.mq.VictorMsg;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * @author QuLei
 */
public class UserAttkCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAttkCmdHandler.class);

    /**
     * 广播减血消息
     *
     * @param targetUserId 被攻击者 Id
     * @param subtractHp   减血量
     */
    private static void broadcastSubtractHp(int targetUserId, int subtractHp) {
        if (targetUserId <= 0 || subtractHp <= 0) {
            return;
        }
        GameMsgProtocol.UserSubtractHpResult.Builder resultBuilder =
                GameMsgProtocol.UserSubtractHpResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserId);
        resultBuilder.setSubtractHp(subtractHp);
        GameMsgProtocol.UserSubtractHpResult newResult = resultBuilder.build();
        Broadcaster.broadMsg(newResult);
    }

    /**
     * 广播死亡消息
     *
     * @param targetUserId 被攻击者Id
     */
    private static void broadcastDie(int targetUserId) {
        if (targetUserId <= 0) {
            return;
        }
        GameMsgProtocol.UserDieResult.Builder resultBuilder =
                GameMsgProtocol.UserDieResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserId);
        GameMsgProtocol.UserDieResult newResult = resultBuilder.build();
        Broadcaster.broadMsg(newResult);
    }

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd cmd) {
        if (null == ctx || null == cmd) {
            return;
        }
        //获取攻击者Id
        Integer attkUserId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (null == attkUserId) {
            return;
        }

        Integer targetUserId = cmd.getTargetUserId();
        GameMsgProtocol.UserAttkResult.Builder resultBuilder = GameMsgProtocol.UserAttkResult.newBuilder();
        resultBuilder.setAttkUserId(attkUserId);
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserAttkResult newResult = resultBuilder.build();
        Broadcaster.broadMsg(newResult);

        //获取被攻击者
        User targetUser = UserManager.getUserById(targetUserId);
        if (null == targetUser) {
            return;
        }
        //打印线程名称
        LOGGER.info("当前线程 = {}", Thread.currentThread().getName());
        int subtractHp = 10;
        targetUser.tract(subtractHp);
        //广播减血消息
        broadcastSubtractHp(targetUserId, subtractHp);
        if (targetUser.getCurrHp() <= 0) {
            //广播死亡消息
            broadcastDie(targetUserId);
            if (!targetUser.isDied()) {
                targetUser.setDied(true);
                //发送消息到MQ
                VictorMsg mqMsg = new VictorMsg();
                mqMsg.setWinnerId(attkUserId);
                mqMsg.setLoserId(targetUserId);
                MQProducer.sendMsg("Victor", mqMsg);
            }
        }
    }

}
