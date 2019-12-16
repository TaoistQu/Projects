package org.tinygame.herostory.login.db;

import lombok.*;

/**
 * 用户实体
 *
 * @author QuLei
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserEntity {
    /**
     * 用户Id
     */
    private int userId;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 用户密码
     */
    private String password;

    /**
     * 英雄形象
     */
    private String heroAvatar;
}
