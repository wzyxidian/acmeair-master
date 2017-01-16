package com.acmeair.config;


import java.sql.Time;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/1/6.
 */
public class ThreadPool {

    /**
     * 设置线程池中线程数目，因为这个客户端只是用来模拟并发请求的，此值为不可调整的值，分配1核，2G内存，线程数目为2
     */
    private static int threadNum = 10;

    /**
     * 并发访问为5,10,100,1000,2000，而服务端的线程池队列为200，所以休眠的时间为400,200,20,2,1
     */
    private static int sleep = 20;

    /**
     * 通过线程池来发送请求，线程池中最大线程数为2，通过修改sleep时间来控制并发量
     * @param sessionid
     */
    public static void sendRequest(String sessionid){
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threadNum);
        long time = System.currentTimeMillis();
        for(int i = 0; i < threadNum; i++){
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    while(System.currentTimeMillis() - time <= 2 * 60 * 1000){
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

