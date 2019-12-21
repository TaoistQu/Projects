package org.tinygame.herostory.login;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.async.AsyncOperationProcessor;
import org.tinygame.herostory.async.IAsyncOperation;
import org.tinygame.herostory.login.db.IUserDao;
import org.tinygame.herostory.login.db.UserEntity;

import java.util.function.Function;

/**
 * 登录服务
 *
 * @author QuLei
 */
public class LoginService {
    /**
     * 日志对象
     */
    static final private Logger LOGGER = LoggerFactory.getLogger(LoginService.class);
    /**
     * 单例对象
     */
    private static final LoginService _instance = new LoginService();

    /**
     * 私有化构造器
     */
    private LoginService() {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    public static LoginService getInstance() {
        return _instance;
    }

    /**
     * 用户登陆
     *
     * @param userName 用户名
     * @param password 密码
     * @return 用户对象
     */
    public void userLogin(String userName, String password, Function<UserEntity, Void> callback) {
        if (null == userName ||
                null == password) {
            return;
        }
        IAsyncOperation asyncOp = new AsyncGetUserByName(userName, password) {
            @Override
            public int getBindId() {
                return userName.charAt(userName.length() - 1);
            }

            @Override
            public void doFinish() {
                if (null == callback) {
                    return;
                }
                callback.apply(this.getUserEntity());
            }
        };
        AsyncOperationProcessor.getInstance().process(asyncOp);
    }

    /**
     * 异步方式获取对象
     */
    private class AsyncGetUserByName implements IAsyncOperation {
        /**
         * 用户名称
         */
        private final String userName;
        /**
         * 用户密码
         */

        private final String password;
        /**
         * 用户实体
         */
        private UserEntity _userEntity = null;

        /**
         * 类参数构造方法
         *
         * @param userName 用户名
         * @param password 用户密码
         */
        AsyncGetUserByName(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        /**
         * 获取用户对象
         *
         * @return 用户对象
         */
        public UserEntity getUserEntity() {
            return _userEntity;
        }

        @Override
        public void doAsync() {
            try (SqlSession mysqlSession = MySqlSessionFactory.openSession()) {
                //获取DAO对象  注意：这个IUserDao 接口没有具体实现
                IUserDao dao = mysqlSession.getMapper(IUserDao.class);
                LOGGER.info("当前登陆线程 = {}", Thread.currentThread().getName());
                //通过用户名获取用户实体
                UserEntity userEntity = dao.getUserByName(userName);
                if (null != userEntity) {
                    //判断用户密码
                    if (!password.equals(userEntity.getPassword())) {
                        LOGGER.error(
                                "用户密码错误, userId = {},userName = {}"
                                , userEntity.getUserId(), userName
                        );
                    }
                } else {
                    // 如果用户实体为空, 则新建用户!
                    userEntity = new UserEntity();
                    userEntity.setUserName(userName);
                    userEntity.setPassword(password);
                    userEntity.setHeroAvatar("Hero_Shaman"); // 默认使用萨满

                    // 将用户实体添加到数据库
                    dao.insertInto(userEntity);
                }
                _userEntity = userEntity;
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
