package org.tinygame.herostory.model;

import lombok.*;

/**
 * 移动状态
 *
 * @author QuLei
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MoveState {
    /**
     * 起始位置x
     */
    private float fromPosX;
    /**
     * 起始位置y
     */
    private float fromPosY;
    /**
     * 目标位置x
     */
    private float toPosX;
    /**
     * 目标位置y
     */
    private float toPosY;
    /**
     * 开始时间
     */
    private long startTime;
}
