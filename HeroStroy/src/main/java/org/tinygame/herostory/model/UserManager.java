package org.tinygame.herostory.model;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玩家管理器
 *
 * @author QuLei
 */
public final class UserManager {
    /**
     * 玩家字典
     */
    private static final Map<Integer, User> _userMap = new ConcurrentHashMap<>();

    /**
     * 私有化构造器
     */
    private UserManager() {
    }

    /**
     * 添加玩家
     *
     * @param newUser
     */
    public static void addUser(User newUser) {
        if (null != newUser) {
            _userMap.put(newUser.getUserId(), newUser);
        }
    }

    /**
     * 根据Id移除玩家
     *
     * @param userId
     */
    public static void removeUserById(int userId) {
        _userMap.remove(userId);
    }

    /**
     * 玩家列表
     *
     * @return
     */
    public static Collection<User> listUser() {
        return _userMap.values();
    }

    /**
     * 根据用户id寻找用户
     *
     * @param userId
     * @return
     */
    public static User getUserById(final int userId) {
        return _userMap.get(userId);
    }
}
