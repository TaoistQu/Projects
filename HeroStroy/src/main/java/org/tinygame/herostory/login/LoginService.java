package org.tinygame.herostory.login;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.login.db.IUserDao;
import org.tinygame.herostory.login.db.UserEntity;

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
    public UserEntity userLogin(String userName, String password) {
        if (null == userName || null == password) {
            return null;
        }
        try (SqlSession mysqlSession = MySqlSessionFactory.openSession()) {
            //获取DAO对象  注意：这个IUserDao 接口没有具体实现
            IUserDao dao = mysqlSession.getMapper(IUserDao.class);
            LOGGER.info("当前线程 = {}", Thread.currentThread().getName());
            //通过用户名获取用户实体
            UserEntity userEntity = dao.getUserByName(userName);
            if (null != userEntity) {
                //判断用户密码
                if (!password.equals(userEntity.getPassword())) {
                    LOGGER.error(
                            "用户密码错误, userId = {},userName = {}"
                            , userEntity.getUserId(), userName
                    );
                    throw new RuntimeException("用户密码错误！！！");
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
            return userEntity;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }

    }

}
