package org.tinygame.herostory.rank;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 排名项
 *
 * @author QuLei
 */
@Setter
@Getter
@ToString
public class RankItem {
    /**
     * 排名Id
     */
    private int rankId;
    /**
     * 用户Id
     */
    private int userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 英雄角色
     */
    private String heroAvatar;
    /**
     * 赢得次数
     */
    private int win;
}
