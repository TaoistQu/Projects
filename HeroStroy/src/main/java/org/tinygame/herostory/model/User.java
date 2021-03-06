package org.tinygame.herostory.model;

import lombok.*;

/**
 * @author QuLei
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
    /**
     * 移动位置
     */
    private final MoveState moveState = new MoveState();
    /**
     * 玩家Id
     */
    private int userId;
    /**
     * 用户名字
     */
    private String userName;
    /**
     * 英雄形象
     */
    private String heroAvatar;
    /**
     * 当前血量
     */
    private int currHp;
    /**
     * 已死亡
     */
    private boolean died;

    public void tract(int subtractHp) {
        currHp = currHp - subtractHp;
    }
}
