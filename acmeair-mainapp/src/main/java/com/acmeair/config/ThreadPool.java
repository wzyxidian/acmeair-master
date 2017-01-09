package com.acmeair.config;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/1/6.
 */
public class ThreadPool {

    /**
     * 设置线程池中线程数目，因为这个客户端只是用来模拟并发请求的，此值为不可调整的值，分配1核，2G内存，线程数目为2
     */
    private static int threadNum = 2;

    /**
     * 并发访问为100,200,500,1000,2000，而服务端的线程池队列为200，所以休眠的时间为1,2,4,10,20
     */
    private static int sleep = 10;

    /**
     *
     * @param sessionid
     */
    public static void sendRequest(String sessionid){
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threadNum);
        for(int i = 0; i < threadNum; i++){
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        System.out.println("系统当前时间： " + System.nanoTime());
                        String result = HttpRequest.sendGet(sessionid,System.nanoTime());
                        System.out.println(Thread.currentThread() + " --- " + result);
                        try {
                            Thread.sleep(sleep);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
}

