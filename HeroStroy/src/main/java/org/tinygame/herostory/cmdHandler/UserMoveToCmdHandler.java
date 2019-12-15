package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.model.MoveState;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 玩家入场指令处理器
 *
 * @author QuLei
 */
public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd> {
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserMoveToCmdHandler.class);

    /**
     * @param ctx
     * @param cmd
     */
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {
        if (null == cmd || null == ctx) {
            return;
        }
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (null == userId) {
            return;
        }
        //获取移动对象
        User moveUser = UserManager.getUserById(userId);
        if (null == moveUser) {
            LOGGER.error("未找到玩家，userId={}", userId);
            return;
        }
        //获取移动状态
        MoveState moveState = moveUser.getMoveState();
        //设置位置和开始时间
        moveState.setFromPosX(cmd.getMoveFromPosX());
        moveState.setFromPosY(cmd.getMoveFromPosY());
        moveState.setToPosX(cmd.getMoveToPosX());
        moveState.setToPosY(cmd.getMoveToPosY());
        moveState.setStartTime(System.currentTimeMillis());

        GameMsgProtocol.UserMoveToResult.Builder resultBuilder =
                GameMsgProtocol.UserMoveToResult.newBuilder();
        resultBuilder.setMoveUserId(userId);
        resultBuilder.setMoveFromPosX(moveState.getFromPosX());
        resultBuilder.setMoveFromPosY(moveState.getFromPosY());
        resultBuilder.setMoveToPosX(moveState.getToPosX());
        resultBuilder.setMoveToPosY(moveState.getToPosY());
        resultBuilder.setMoveStartTime(moveState.getStartTime());
        GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();
        Broadcaster.broadMsg(newResult);
    }
}
