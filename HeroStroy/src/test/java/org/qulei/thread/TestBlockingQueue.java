package org.qulei.thread;

import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 测试阻塞队列
 */
public class TestBlockingQueue {
    @Test
    public void test() {
        //test1();
        test2();
    }

    public void test1() {
        //阻塞队列
        BlockingQueue<Integer> blockingQueue = new LinkedBlockingDeque<Integer>();
        //第一个线程网队列里加数
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //往队列里加数
                blockingQueue.offer(i);
            }
        });
        //第二个线程往队列里加数
        Thread t2 = new Thread(() -> {
            for (int i = 10; i < 20; i++) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //往队列里加数
                blockingQueue.offer(i);
            }
        });
        //第三个线程从队列里取数
        Thread t3 = new Thread(() -> {
            try {
                while (true) {
                    //从队列里取数打印
                    Integer val = blockingQueue.take();
                    System.out.println("获取的数为：" + val);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test2() {
        MyExecutorService es = new MyExecutorService();
        //第一个线程网队列里加数
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final int currVal = i;
                es.submit(() -> {
                    System.out.println("i = " + currVal);
                });
            }
        });
        //第二个线程往队列里加数
        Thread t2 = new Thread(() -> {
            for (int i = 10; i < 20; i++) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final int currVal = i;
                es.submit(() -> {
                    System.out.println("i = " + currVal);
                });
            }
        });

        t1.start();
        t2.start();


        try {
            t1.join();
            t2.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/**
 * 模拟 ExecutorService
 */
class MyExecutorService {
    /**
     * 阻塞队列，阻塞队列里塞入的是Runnable接口
     */
    private final BlockingQueue<Runnable> _blockingQueue = new LinkedBlockingDeque<>();
    /**
     * 内置线性
     */
    private final Thread _thread;

    /**
     * 类默认构造器
     */
    MyExecutorService() {
        _thread = new Thread(() -> {
            try {
                while (true) {
                    Runnable r = _blockingQueue.take();
                    if (null != r) {
                        r.run();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        _thread.start();
    }

    /**
     * 提交一个 Runnable
     *
     * @param r Runnable
     */
    public void submit(Runnable r) {
        if (null != r) {
            _blockingQueue.offer(r);
        }
    }
}
