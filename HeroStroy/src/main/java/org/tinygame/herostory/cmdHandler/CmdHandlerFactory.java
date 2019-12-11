package org.tinygame.herostory.cmdHandler;

import com.google.protobuf.GeneratedMessageV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.util.PackageUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 指令处理工厂
 *
 * @author QuLei
 */
public class CmdHandlerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmdHandlerFactory.class);
    static final private Map<Class<?>, ICmdHandler<? extends GeneratedMessageV3>> _handlerMap = new HashMap<>();

    /**
     * 私有化构造方法
     */
    private CmdHandlerFactory() {
    }

    /**
     * 初始化
     */
    static public void init() {
        //获取包名称
        final String packageName = CmdHandlerFactory.class.getPackage().getName();

        Set<Class<?>> clazzSet = PackageUtil.listSubClazz(packageName, true, ICmdHandler.class);
        for (Class<?> clazz : clazzSet) {
            if ((clazz.getModifiers() & Modifier.ABSTRACT) != 0) {
                //如果是抽象类
                continue;
            }
            //获取方法数组
            Method[] methodArray = clazz.getDeclaredMethods();
            //消息类型
            Class<?> msgType = null;
            for (Method currMethod : methodArray) {
                if (!currMethod.getName().equals("handle")) {
                    //如果不是handle方法
                    continue;
                }

                Class<?>[] paramTypeArray = currMethod.getParameterTypes();
                if (paramTypeArray.length < 2 || paramTypeArray[1] == GeneratedMessageV3.class
                        || !GeneratedMessageV3.class.isAssignableFrom(paramTypeArray[1])) {
                    continue;
                }
                msgType = paramTypeArray[1];
                break;
            }

            if (msgType == null) {
                continue;
            }
            try {
                ICmdHandler<?> newHandler = (ICmdHandler<?>) clazz.newInstance();
                LOGGER.info(
                        "关联 {} <==> {}",
                        msgType.getName(),
                        clazz.getName()
                );
                _handlerMap.put(msgType, newHandler);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    public static ICmdHandler<? extends GeneratedMessageV3> create(Class<?> msgClazz) {
        if (null == msgClazz) {
            return null;
        }
        return _handlerMap.get(msgClazz);
    }
}
