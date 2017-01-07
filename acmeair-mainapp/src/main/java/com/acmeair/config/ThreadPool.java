package com.acmeair.config;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/1/6.
 */
public class ThreadPool {

//    private ServerSocket serverSocket=null;
//
//    private int port=6100;
//    //线程池中线程的数量最多为60个线程
//    private static final int THREAD_NUM=80;
//    //容量为60的一个线程池
//    //public static final Executor exec=Executors.newFixedThreadPool(THREAD_NUM);
//    public static final ExecutorService exec=Executors.newCachedThreadPool();
//
//    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    private Hashtable<String,Socket> allSocketHash=new Hashtable<String,Socket>();
//
//    private Jedis jedis=null;
//
//
//
//    //20160122添加，车钥匙分离使用
//    private static int desionTime=5;//默认5秒收不到匹配的钥匙id为车钥匙分离
//
//    //车钥分离开始检查的动作，即必须在接收到数据之后才开始检查
//
//
//    //读取配置文件，获取多长时间判断为车钥分离
//    public static void getDesionTime(){
//
//        try{
//
//            System.out.println("读取车钥匙分离配置时间。。。。。");
//
//            InputStream is=null;
//
//            String configPath=System.getProperty("user.dir")+"/config/cheyaofenli.properties";
//
//            try {
//
//                is=new BufferedInputStream(new FileInputStream(configPath));
//
//            } catch (FileNotFoundException e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//            }
//
//            Properties prop=new Properties();
//
//            prop.load(is);
//
//            desionTime=Integer.valueOf(prop.getProperty("desiontime"));
//
//            System.out.println("获取的车钥匙分离配置时间："+desionTime+"秒");
//        }
//        catch(Exception e){
//
//            System.out.println("读取车钥匙分离时间出错："+e.getMessage());
//
//            e.printStackTrace();
//        }
//
//    }
//
//
//    public ThreadPool(){
//
//        try {
//
//            System.out.println("接收数据服务开启，监听端口："+port);
//
//            serverSocket=new ServerSocket(port);
//
//            ((ThreadPoolExecutor)exec).setCorePoolSize(THREAD_NUM);
//
//            //((ThreadPoolExecutor)exec).setMaximumPoolSize(100);
//            //超过30秒无响应，则关闭该线程
//            ((ThreadPoolExecutor)exec).setKeepAliveTime(30, TimeUnit.SECONDS);
//
//            receiveData();
//
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    public void receiveData(){
//
//        try {
//
//            //临时方法，加入车、钥匙分离判断，参数为配置的车钥匙分离时间，20160122
//            exec.submit(new CheckCheIDRunnable(desionTime));
//
//            while(true){
//
//                Socket socket=serverSocket.accept();
//
//                String socketIP=socket.getInetAddress().getHostAddress();
//
//                System.out.println("SocketIP:"+socketIP);
//
//                int socketPort=socket.getLocalPort();
//
//                System.out.println("新机具接入，IP地址为："+socketIP+"本地端口为："+socketPort+"远端端口为："+socket.getPort()+"时间为："+sdf.format(new Date()));
//                //加入线程池进行执行
//                exec.submit(new TransAndDealReceiveDataThread(socket,desionTime));
//
//                int size=((ThreadPoolExecutor)exec).getActiveCount();
//
//                System.out.println("当前活跃线程数量为："+size);
//
//                jedis=RedisUtils.getJedis();
//
//                jedis.rpush("threadpoolsizelist", size+" "+sdf.format(new Date()));
//
//                RedisUtils.releaseJedis(jedis);
//
//            }
//
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//
//            jedis=RedisUtils.getJedis();
//
//            jedis.rpush("mainthreaderrorlist", e.getMessage()+" "+sdf.format(new Date()));
//
//            RedisUtils.releaseJedis(jedis);
//        }
//    }
//
//    public static void main(String[] args) {
//        // TODO Auto-generated method stub
//        //加载车ID和钥匙ID的对应关系,20160122
//        loadCheIDAndKeyID();
//
//        //读取车钥分离配置时间,20160122
//        getDesionTime();
//
//        new ControlReceiveData();
//    }
//
//    //临时方法，从oracle中加载一次《车id，钥匙id》信息到Redis数据库中
//    public static void loadCheIDAndKeyID(){
//
//        try{
//
//            //获取jedis连接
//            Jedis jedis=RedisUtils.getJedis();
//
//            System.out.println("初始化加载车id和钥匙id对应关系。。。");
//
//            DeployControlFacade df=new DeployControlFacade();
//
//            HashMap<String,String> hash=df.getCoupleID();
//
//            Set<String> keyset=hash.keySet();
//
//            Iterator it=keyset.iterator();
//
//            while(it.hasNext()){
//
//                String key=(String)it.next();
//
//                System.out.println("车id："+key+", 钥匙id："+hash.get(key));
//                //加入到钥匙hash中,键为车钥匙id，值为车id(每到来一个id，先判断是否是钥匙id，不是则为车id，刷新车id的时间
//                //等钥匙id到来时，刷新对应的的车id的有效时间)
//                jedis.hset("yaoshihash", hash.get(key), key);
//                //初始化每一辆车的报警标志位（初始为0），eidnoticehash，键：车辆id，值：0或1
//                jedis.hset("eidnoticehash", key, "0");
//                //存储车辆id上一次的RID位置,eidridhash,键：车辆id，值车辆上一次所在的RID(初始化没有，为no)
//                jedis.hset("eidridhash", key, "no");
//                //判断该车id目前是否有记录到达过，初始化时是no，表示没有记录到达过
//                jedis.hset("firsthash", key, "no");
//            }
//
//            RedisUtils.releaseJedis(jedis);
//
//        }
//        catch(Exception e){
//
//            System.out.println("加载车id和钥匙id对应关系出错");
//
//            e.printStackTrace();
//        }
//    }
//
//    //判断车辆是否存在车与钥匙分离一分钟
//    //遍历eidtimehash中所有eid的时间，超过一分钟则写入报警记录
//    class CheckCheIDRunnable implements Runnable{
//        //车钥匙分离时间，默认5秒钟
//        int desionTime=5;
//
//        Jedis jedis=null;
//        //参数为判定车钥匙分离的时间
//        public CheckCheIDRunnable(int desionTime){
//
//            this.desionTime=desionTime;
//
//            jedis=RedisUtils.getJedis();
//        }
//
//        @Override
//        public void run() {
//            // TODO Auto-generated method stub
//            try{
//
//                while(true){
//
//                    //获取aliveset中所有存在的车辆id
//                    Set<String> aliveItems=jedis.smembers("aliveset");
//
//                    System.out.println("保活集合："+aliveItems);
//
//                    Iterator<String> iter=aliveItems.iterator();
//
//                    while(iter.hasNext()){
//                        //车id
//                        String cheID=iter.next();
//
//                        //System.out.println(cheID+"的报货时间："+jedis.ttl(cheID));
//
//                        //车辆id过了规定时间仍没有得到匹配的钥匙id
//                        if(jedis.ttl("alive"+cheID)<=0){
//
//                            //得到车当前的RID
//                            String rid=jedis.hget("eidridhash", cheID);
//
//                            System.out.println("车钥分离，车id："+cheID+"当前所在RID："+rid);
//
//                            ThreadHelper.writeFocusInfo(rid, cheID, String.valueOf(System.currentTimeMillis()), "人车分离");
//                            //从aliveset集合中删除该cheID
//                            jedis.srem("aliveset", cheID);
//                            //更改报警标志位为1
//                            jedis.hset("eidnoticehash", cheID, "1");
//                        }
//                    }
//
//                    //休眠10秒钟
//                    Thread.sleep(5*1000);
//
//					/*//当前时间
//					long nowTime=System.currentTimeMillis();
//					//获取到所有eid和对应时间
//					Map<String,String> eidtimehash=jedis.hgetAll("eidtimehash");
//					//得到所有eid
//					Set<String> eidKeySet=eidtimehash.keySet();
//					Iterator it=eidKeySet.iterator();
//					while(it.hasNext()){
//
//						String cheID=(String) it.next();
//
//						String cheTimeString=eidtimehash.get(cheID);
//
//						long cheTime=Long.valueOf(cheTimeString);
//						//超过配置的钥匙分离的时间
//						if(nowTime-cheTime>desionTime*1000){
//
//							System.out.println("车钥分离超时，eid为："+cheID);
//							//写入报警记录,报警类型为"车钥分离"
//							ThreadHelper.writeFocusInfo("无rid",cheID,cheTimeString,"车钥分离超时");
//						}
//						else{
//
//							//没有超过一分钟
//						}
//					}*/
//
//                }
//
//            }
//            catch(Exception e){
//
//                System.out.println("车与钥匙分离报警线程出错，重新启动新线程");
//
//                e.printStackTrace();
//                //该线程出现异常，清除该线程，并提交新线程到线程池
//                exec.submit(new CheckCheIDRunnable(desionTime));
//            }
//        }
//    }
}

