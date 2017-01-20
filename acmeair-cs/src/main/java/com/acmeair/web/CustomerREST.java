/*******************************************************************************
 * Copyright (c) 2013 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.acmeair.web;

import com.acmeair.service.CustomerService;
import com.acmeair.service.ServiceLocator;
import com.acmeair.web.dto.CustomerInfo;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.util.Map.Entry;
import java.io.FileWriter;
import java.util.concurrent.*;

@Path("/customer")
public class CustomerREST {

    private static int poolSize = 5; //核心池大小 / 并发线程数
    static ThreadPoolExecutor executor = new ThreadPoolExecutor(poolSize, poolSize, 200, TimeUnit.MILLISECONDS, new QueueTest<Runnable>(200), new ThreadPoolExecutor.DiscardPolicy());
    static int index = 0; //请求数量
    static int count = 0; //执行完的任务数量
    public static Map<String, ArrayList<String>> map = new ConcurrentHashMap<String, ArrayList<String>>();
    static int dbcount = 0; //请求数据库数量
    static int size = 101; //判断是否开始写入文件

    private CustomerService customerService = ServiceLocator.instance().getService(CustomerService.class);

    @Context
    private HttpServletRequest request;

    private boolean validate(String customerid) {
        String loginUser = (String) request.getAttribute(RESTCookieSessionFilter.LOGIN_USER);
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("validate : loginUser " + loginUser + " customerid " + customerid);
        }
        return customerid.equals(loginUser);
    }

    protected Logger logger = Logger.getLogger(CustomerService.class.getName());

    @GET
    @Path("/byid/{custid}")
    @Produces("text/plain")
    public void getCustomer(@CookieParam("sessionid") String sessionid, @PathParam("custid") String customerid, @QueryParam("sendtime") String sendtime, @QueryParam("username") String username) {

        MyTask myTask = new MyTask(index++, sessionid, customerid, sendtime, username);
        System.out.println(System.nanoTime() + "start task: " + index);
        executor.execute(myTask);
        System.out.println("poolSize: " + executor.getPoolSize() + " , queueWaitSize: " +
                executor.getQueue().size());

    }

    class MyTask implements Runnable {
        private int taskNum; //任务编号
        private String sessionid; //session id
        private String customerid; //customer id
        private String sendtime; //请求发送时间
        private String username; //用户名
        private int ti = 100; //数据库输入数据
        private int nr = 10000000; //数据库表记录条数
        private int z = 200000; //数据库并发连接数
        private int to = 100; //数据库输出数据
        private int fp = 1000000; //程序复杂度
        private long t3;
        private String[] s1;

        public MyTask(int num, String sessionid, String customerid, String sendtime, String username) {
            this.taskNum = num;
            this.sessionid = sessionid;
            this.customerid = customerid;
            this.sendtime = sendtime;
            this.username = username;
        }

        @Override
        public void run() {
            try {

                t3 = System.nanoTime();
                s1 = CollectInfo.collectionConfigs();
                getInfo(sessionid, customerid, sendtime, username);
                count++;
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (count == size) {
                    System.out.println("begin to write");
                    try {

                        String line = System.getProperty("line.separator");
                        StringBuffer str = new StringBuffer();
                        FileWriter fw = new FileWriter("/test/" + System.currentTimeMillis() + ".txt", true);
                        Map<String, ArrayList<String>> map1 = new HashMap<String, ArrayList<String>>();
                        System.out.println("map size: "+map.size());
                        for (Entry<String, ArrayList<String>> vo : map
                                .entrySet()) {
                            for (int i = 0; i < vo.getValue().size(); i++) {
                                if (vo.getValue().get(i).indexOf("count") != -1) {
                                    map1.put(vo.getKey(), vo.getValue());
                                    System.out.println("map content: "+vo.getKey());

                                }
                            }
                        }
                        System.out.println("map1.size(): " + map1.size());
                        for (Entry<String, ArrayList<String>> vo : map1
                                .entrySet()) {
                            str.append(vo.getKey() + " : ");
                            for (int j = 0; j < vo.getValue().size(); j++) {
                                str.append(vo.getValue().get(j)).append(",");
                            }
                            str.append(line);
                        }

                        fw.write(str.toString());
                        fw.close();

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        /**
         * 通过customerid获取customer信息
         *
         * @param sessionid
         * @param customerid
         * @param sendtime
         * @param username
         */
        public void getInfo(String sessionid, String customerid, String sendtime, String username) {
            System.out.println("send time: " + sendtime);
            if (map.containsKey("Task" + Integer.toString(taskNum))) {
                ArrayList<String> value = map.get("Task"
                        + Integer.toString(taskNum));
                value.add("t0 = " + Long.parseLong(sendtime));
                int num = sendtime.getBytes().length + username.getBytes().length;
                value.add("t0 msi = " + num);
            } else {
                ArrayList<String> value = new ArrayList<String>();
                value.add("t0 = " + Long.parseLong(sendtime));
                int num = sendtime.getBytes().length + username.getBytes().length;
                value.add("t0 msi = " + num);
                map.put("Task" + Integer.toString(taskNum), value);
            }
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("getCustomer : session ID " + sessionid + " userid " + customerid);
            }
            try {
                // make sure the user isn't trying to update a customer other than the one currently logged in
                if (!validate(customerid)) {
                    System.out.println("error");
                }

                int[] array = new int[fp];
                sortNum(array, fp);
                String[] s = CollectInfo.collectionConfigs();
                long t4 = System.nanoTime();

                StringBuilder stringBuilder = new StringBuilder();
                Random random = new Random();
                for(int i=0; i<ti; i++){
                    stringBuilder.append("uid" + random.nextInt(10000) + "@email.com;");
                }
                String tiSize = stringBuilder.toString().substring(0,stringBuilder.toString().length()-1);

                if (map.containsKey("Task" + Integer.toString(taskNum))) {

                    ArrayList<String> value = map.get("Task"
                            + Integer.toString(taskNum));
                    value.add("t3 = " + t3);
                    value.add("t3 Cu = " + s1[0]);
                    value.add("t3 Ru = " + s1[1]);
                    value.add("t4 = " + t4);
                    value.add("t4 Cu = " + s[0]);
                    value.add("t4 Ru = " + s[1]);
                    value.add("t4 fp = " + fp);
                    value.add("ti = " + ti);
                    value.add("nr = " + nr);
                    value.add("z = " + z);

                } else {

                    ArrayList<String> value = new ArrayList<String>();
                    value.add("t3 = " + t3);
                    value.add("t3 Cu = " + s1[0]);
                    value.add("t3 Ru = " + s1[1]);
                    value.add("t4 = " + t4);
                    value.add("t4 Cu = " + s[0]);
                    value.add("t4 Ru = " + s[1]);
                    value.add("t4 fp = " + fp);
                    value.add("ti = " + ti);
                    value.add("nr = " + nr);
                    value.add("z = " + z);
                    map.put("Task" + Integer.toString(taskNum), value);
                }
                String[] customerIds = tiSize.split(";");
                dbcount++;
                String ss = customerService.getCustomersByUsernames(customerIds);
                long t5 = System.nanoTime();
                dbcount--;
                if (map.containsKey("Task" + Integer.toString(taskNum))) {

                    ArrayList<String> value = map.get("Task"
                            + Integer.toString(taskNum));
                    value.add("t5 = " + t5);
                    int num = ss.getBytes().length;
                    value.add("t5 mso = " + num);
                    value.add("t5 to = " + to);
                    value.add("count = " + dbcount);

                } else {

                    ArrayList<String> value = new ArrayList<String>();
                    value.add("t5 = " + t5);
                    int num = ss.getBytes().length;
                    value.add("t5 mso = " + num);
                    value.add("t5 to = " + to);
                    value.add("count = " + dbcount);
                    map.put("Task" + Integer.toString(taskNum), value);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String toString() {
            return "Task" + taskNum;
        }

        public void sortNum(int[] array, int num) {

            // 生成随机数
            for (int i = 0; i < array.length; i++) {
                array[i] = (int) (Math.random() * num);
            }
            // 快排
            sort2(array);

        }

        public void sort2(int[] a) {
            qSort(a, 0, a.length - 1);
        }

        public void qSort(int[] a, int begin, int end) {
            int middle;
            if (begin < end) { // 一定要有这个判断终止，否则递归无法停止，将内存溢出
                middle = partition(a, begin, end);
                qSort(a, begin, middle - 1);
                qSort(a, middle + 1, end);
            }
        }

        public int partition(int[] a, int begin, int end) {
            int midvalue = a[begin];
            while (begin < end) {
                while (begin < end && a[end] >= midvalue) {
                    end--;
                }
                swap(a, begin, end);
                while (begin < end && a[begin] < midvalue) {
                    begin++;
                }
                swap(a, begin, end);
            }
            return begin; // 此时的a[begin]=a[end]=midvalue，返回的数组下标为排好序的
        }

        public void swap(int[] a, int begin, int end) {
            int temp = a[end];
            a[end] = a[begin];
            a[begin] = temp;
        }
    }


    @POST
    @Path("/byid/{custid}")
    @Produces("text/plain")
    public Response putCustomer(@CookieParam("sessionid") String sessionid, CustomerInfo customer) {
        String username = customer.getUsername();
        if (!validate(username)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        String customerFromDB = customerService.getCustomerByUsernameAndPassword(username, customer.getPassword());
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("putCustomer : " + customerFromDB);
        }

        if (customerFromDB == null) {
            // either the customer doesn't exist or the password is wrong
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        customerService.updateCustomer(username, customer);

        //Retrieve the latest results
        customerFromDB = customerService.getCustomerByUsernameAndPassword(username, customer.getPassword());
        return Response.ok(customerFromDB).build();
    }

    @POST
    @Path("/validateid")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("text/plain")
    public Response validateCustomer(@FormParam("login") String login, @FormParam("password") String password) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("validateid : login " + login + " password " + password);
        }

        String validCustomer = null;

        if (customerService.validateCustomer(login, password)) {
            validCustomer = "true";
        } else {
            validCustomer = "false";
        }

        String s = "{\"validCustomer\":\"" + validCustomer + "\"}";

        return Response.ok(s).build();
    }


}
