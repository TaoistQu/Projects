package org.tinygame.herostory.login.db;

import org.apache.ibatis.annotations.Param;

/**
 * @author QuLei
 */
public interface IUserDao {
    /**
     * 根据用户名获取用户实体
     *
     * @param userName 用户名
     * @return 用户实体
     */
    UserEntity getUserByName(@Param("userName") String userName);

    /**
     * 添加用户实体
     *
     * @param newUserEntity 用户实体
     */
    void insertInto(UserEntity newUserEntity);
}
