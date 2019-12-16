package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * @author QuLei
 */
public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserEntryCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd cmd) {
        if (null == ctx || null == cmd) {
            return;
        }
        //获取用户Id
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf(
                "userId")).get();
        if (null == userId) {
            return;
        }
        //获取已有用户
        User existUser = UserManager.getUserById(userId);
        if (null == existUser) {
            LOGGER.error("用户不存在,userId = {}", userId);
            return;
        }
        String heroAvatar = existUser.getHeroAvatar();
        GameMsgProtocol.UserEntryResult.Builder resultBuilder
                = GameMsgProtocol.UserEntryResult.newBuilder();
        resultBuilder.setUserId(userId);
        resultBuilder.setHeroAvatar(heroAvatar);
        //构建结构发送
        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
        Broadcaster.broadMsg(newResult);
    }
}
