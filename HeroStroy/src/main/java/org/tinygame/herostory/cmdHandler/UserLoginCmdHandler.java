package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.login.LoginService;
import org.tinygame.herostory.login.db.UserEntity;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 用户登陆指令处理
 *
 * @author QuLei
 */
public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserLoginCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {
        if (null == ctx || null == cmd) {
            return;
        }
        String userName = cmd.getUserName();
        String password = cmd.getPassword();
        LOGGER.info(
                "用户登陆, userName = {}, password = {}",
                userName,
                password
        );
        UserEntity userEntity = null;
        try {
            userEntity = LoginService.getInstance().userLogin(userName, password);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return;
        }
        if (null == userEntity) {
            LOGGER.error("用户登陆失败, userName = {}", cmd.getUserName());
            return;
        }
        LOGGER.info("用户登陆成功, userId = {}, userName = {}",
                userEntity.getUserId(),
                userEntity.getUserName()
        );
        //新建用户
        User newUser = new User();
        newUser.setUserId(userEntity.getUserId());
        newUser.setUserName(userEntity.getUserName());
        newUser.setHeroAvatar(userEntity.getHeroAvatar());
        newUser.setCurrHp(100);
        //将用户加入到管理器中
        UserManager.addUser(newUser);
        //将用户Id附着在Channel上
        ctx.channel().attr(AttributeKey.valueOf("userId")).set(newUser.getUserId());

        //登陆结果构建者
        GameMsgProtocol.UserLoginResult.Builder resultBuilder
                = GameMsgProtocol.UserLoginResult.newBuilder();
        resultBuilder.setUserId(newUser.getUserId());
        resultBuilder.setUserName(newUser.getUserName());
        resultBuilder.setHeroAvatar(newUser.getHeroAvatar());
        //构建结果并发送
        GameMsgProtocol.UserLoginResult newResult = resultBuilder.build();
        ctx.writeAndFlush(newResult);
    }
}
