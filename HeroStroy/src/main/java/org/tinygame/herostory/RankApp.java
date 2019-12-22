package org.tinygame.herostory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.mq.MQConsumer;
import org.tinygame.herostory.util.RedisUtil;

/**
 * 排行榜应用程序
 *
 * @author QuLei
 */
public class RankApp {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(RankApp.class);

    /**
     * 应用入口函数
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        MQConsumer.init();
        RedisUtil.init();
        LOGGER.info("排行榜服务启动！！！");
    }
}
