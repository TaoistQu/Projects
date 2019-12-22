package org.tinygame.herostory.mq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.rank.RankService;

import java.util.List;

/**
 * 消息队列消费者
 *
 * @author QuLei
 */
public final class MQConsumer {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(MQConsumer.class);

    /**
     * 私有化构造器
     */
    private MQConsumer() {
    }

    static public void init() {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("herostory");
        consumer.setNamesrvAddr("192.168.160.75:9876");
        try {
            consumer.subscribe("Victor", "*");
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext ctx) {
                    for (MessageExt msgExt : list) {
                        VictorMsg mqMsg = JSONObject.parseObject(
                                msgExt.getBody(),
                                VictorMsg.class
                        );
                        LOGGER.info("从消息队列中收到战斗结果,winnerId={},loserId={}", mqMsg.getWinnerId(), mqMsg.getLoserId());
                        //刷新排行榜
                        RankService.getInstance().refreshRank(mqMsg.getWinnerId(), mqMsg.getLoserId());
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            consumer.start();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


}
