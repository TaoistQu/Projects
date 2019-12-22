package org.tinygame.herostory.mq;

import lombok.Getter;
import lombok.Setter;

/**
 * 战斗结果消息
 *
 * @author QuLei
 */
@Setter
@Getter
public class VictorMsg {
    /**
     * 赢家Id
     */
    private int winnerId;
    /**
     * 输家Id
     */
    private int loserId;
}
