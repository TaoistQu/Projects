package org.tinygame.herostory.rank;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.async.AsyncOperationProcessor;
import org.tinygame.herostory.async.IAsyncOperation;
import org.tinygame.herostory.util.RedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * 排行榜服务
 *
 * @author QuLei
 */
public final class RankService {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(RankService.class);
    /**
     * 单例对象
     */
    static private final RankService _instance = new RankService();

    /**
     * 私有化构造方法
     */
    private RankService() {
    }

    /**
     * 获取单例对象
     *
     * @return 排行榜服务对象
     */
    public static RankService getInstance() {
        return _instance;
    }

    /**
     * 获取排名列表
     *
     * @param callBack 回调函数
     */
    public void getRank(Function<List<RankItem>, Void> callBack) {
        if (null == callBack) {
            return;
        }
        IAsyncOperation asynOp = new AsyncGetRank() {
            @Override
            public void doFinish() {
                callBack.apply(this.getRankItemList());
            }
        };
        AsyncOperationProcessor.getInstance().process(asynOp);
    }

    /**
     * 刷新排行榜
     *
     * @param winnerId 赢家Id
     * @param loserId  输家Id
     */
    public void refreshRank(int winnerId, int loserId) {
        try (Jedis redis = RedisUtil.getRedis()) {
            //增加用户的输赢操作
            redis.hincrBy("User_" + winnerId, "Win", 1);
            redis.hincrBy("User_" + loserId, "Lose", 1);
            String winStr = redis.hget("User_" + winnerId, "Win");
            int winInt = Integer.parseInt(winStr);
            //修改排行榜
            redis.zadd("Rank", winInt, String.valueOf(winnerId));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 异步方式获取排名列表
     */
    private class AsyncGetRank implements IAsyncOperation {
        /**
         * 排名列表
         */
        private List<RankItem> _rankItemList = null;

        /**
         * 获取排名列表
         *
         * @return 排名列表
         */
        public List<RankItem> getRankItemList() {
            return _rankItemList;
        }

        @Override
        public void doAsync() {
            try (Jedis redis = RedisUtil.getRedis()) {
                //获取字符串值集合
                Set<Tuple> valSet = redis.zrevrangeWithScores("Rank", 0, 9);
                List<RankItem> rankItemList = new ArrayList<>();
                int rankId = 0;
                for (Tuple t : valSet) {
                    //获取用户Id
                    int userId = Integer.parseInt(t.getElement());
                    //获取用户基本信息
                    String jsonStr = redis.hget("User_" + userId, "BasicInfo");
                    if (StringUtils.isBlank(jsonStr)) {
                        continue;
                    }
                    JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                    RankItem newItem = new RankItem();
                    newItem.setRankId(++rankId);
                    newItem.setUserId(userId);
                    newItem.setUserName(jsonObject.getString("userName"));
                    newItem.setHeroAvatar(jsonObject.getString("heroAvatar"));
                    newItem.setWin((int) t.getScore());
                    rankItemList.add(newItem);
                }
                _rankItemList = rankItemList;
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
