package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息识别器
 *
 * @author QuLei
 */
public class GameMsgRecognizer {
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgRecognizer.class);
    /**
     * 消息代码和消息体字典
     */
    static private final Map<Integer, GeneratedMessageV3> _msgCodeAndMsgBodyMap = new HashMap<>();
    /**
     * 消息类型和消息编码字典
     */
    private static final Map<Class<?>, Integer> _msgClazzAndMsgCodeMap = new HashMap<>();

    /**
     * 私有化构造方法
     */
    private GameMsgRecognizer() {
    }

    /**
     * 初始化
     */
    public static void init() {
        Class<?>[] innerClazzArray = GameMsgProtocol.class.getDeclaredClasses();
        for (Class<?> innerClazz : innerClazzArray) {
            if (!GeneratedMessageV3.class.isAssignableFrom(innerClazz)) {
                continue;
            }
            String clazzName = innerClazz.getSimpleName().toLowerCase();
            for (GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()) {
                String strMsgCode = msgCode.name().replaceAll("_", "").toLowerCase();
                if (!strMsgCode.startsWith(clazzName)) {
                    continue;
                }
                try {
                    Object returnObject = innerClazz.getDeclaredMethod("getDefaultInstance").invoke(innerClazz);
                    LOGGER.info("{} <==> {}", innerClazz.getSimpleName(), msgCode.getNumber());
                    _msgClazzAndMsgCodeMap.put(innerClazz, msgCode.getNumber());
                    _msgCodeAndMsgBodyMap.put(msgCode.getNumber(), (GeneratedMessageV3) returnObject);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
    }

    /**
     * 根据消息编号获取构建者
     *
     * @param msgCode
     * @return
     */
    public static Message.Builder getBuilderByMsgCode(int msgCode) {
        if (msgCode < 0) {
            return null;
        }
        GeneratedMessageV3 msg = _msgCodeAndMsgBodyMap.get(msgCode);
        if (null == msg) {
            return null;
        }

        return msg.newBuilderForType();
    }

    /**
     * 根据消息类获取消息编号
     *
     * @param msgClazz
     * @return
     */
    public static int getMsgCodeByMsgClazz(Class<?> msgClazz) {
        if (null == msgClazz) {
            return -1;
        }

        Integer msgCode = _msgClazzAndMsgCodeMap.get(msgClazz);
        if (null != msgCode) {
            return msgCode.intValue();
        }
        return -1;
    }
}
