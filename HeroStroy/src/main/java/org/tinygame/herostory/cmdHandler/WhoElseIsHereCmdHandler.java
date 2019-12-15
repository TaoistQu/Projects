package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import org.tinygame.herostory.model.MoveState;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 谁在场指令处理器
 *
 * @author QuLei
 */
public class WhoElseIsHereCmdHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd cmd) {
        if (null == ctx || cmd == null) {
            return;
        }
        GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder =
                GameMsgProtocol.WhoElseIsHereResult.newBuilder();
        for (User currUser : UserManager.listUser()) {
            if (null == currUser) {
                continue;
            }
            //构建每一个玩家信息
            GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder =
                    GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
            userInfoBuilder.setUserId(currUser.getUserId());
            userInfoBuilder.setHeroAvatar(currUser.getHeroAvatar());

            //构建移动状态
            MoveState moveState = currUser.getMoveState();
            GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.Builder mvStateBuilder =
                    GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.newBuilder();
            mvStateBuilder.setFromPosX(moveState.getFromPosX());
            mvStateBuilder.setFromPosY(moveState.getFromPosY());
            mvStateBuilder.setToPosX(moveState.getToPosX());
            mvStateBuilder.setToPosY(moveState.getToPosY());
            mvStateBuilder.setStartTime(mvStateBuilder.getStartTime());
            //将用户移动状态设置给用户信息
            userInfoBuilder.setMoveState(mvStateBuilder);
            //将用户信息添加到结果消息
            resultBuilder.addUserInfo(userInfoBuilder);
        }
        GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
        ctx.writeAndFlush(newResult);
    }
}
