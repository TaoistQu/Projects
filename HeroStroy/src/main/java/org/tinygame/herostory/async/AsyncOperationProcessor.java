package org.tinygame.herostory.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MainThreadProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步操作服务
 *
 * @author QuLei
 */
public final class AsyncOperationProcessor {
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncOperationProcessor.class);
    /**
     * 单例对象
     */
    static private final AsyncOperationProcessor _instance = new AsyncOperationProcessor();
    /**
     * 创建一个单线程数组
     */
    private final ExecutorService[] _esArray = new ExecutorService[8];

    /**
     * 私有化构造器
     */
    private AsyncOperationProcessor() {
        for (int i = 0; i < _esArray.length; i++) {
            //线程名称
            final String threadName = "AsyncOperationProcessor" + i;
            _esArray[i] = Executors.newSingleThreadExecutor((newRunable) -> {
                Thread newThread = new Thread(newRunable);
                newThread.setName(threadName);
                return newThread;
            });
        }

    }


    /**
     * 获取单例对象
     *
     * @return 异步操作对象
     */
    static public AsyncOperationProcessor getInstance() {
        return _instance;
    }

    /**
     * 处理异步操作
     *
     * @param asyncOp 异步操作
     */
    public void process(IAsyncOperation asyncOp) {
        if (null == asyncOp) {
            return;
        }
        //根据bindId获取线程索引
        int bindId = Math.abs(asyncOp.getBindId());
        int esIndex = bindId % _esArray.length;
        _esArray[esIndex].submit(() -> {
            try {
                //执行异步操作
                asyncOp.doAsync();
                //异步执行完返回到主线程中执行结束操作
                MainThreadProcessor.getInstance().process(asyncOp::doFinish);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        });
    }
}

