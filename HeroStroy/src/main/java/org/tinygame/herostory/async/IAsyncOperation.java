package org.tinygame.herostory.async;

/**
 * 定义一个同步接口
 *
 * @author QuLei
 */
public interface IAsyncOperation {
    /**
     * 获取绑定Id
     *
     * @return 绑定Id
     */
    default int getBindId() {
        return 0;
    }

    /**
     * 执行异步方法
     */
    void doAsync();

    /**
     * 执行完成操作 有些操作不一定要执行
     */
    default void doFinish() {

    }

}
