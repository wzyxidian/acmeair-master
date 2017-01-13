package com.acmeair.web;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/6.
 */
public class CollectInfo {

    private static String containerID = null;

    /**
     * 要查看性能的容器名称，默认为cs，不会改变
     */
    private static String containerName = "cs";

    /**
     * http服务运行的宿主机的ip，要根据部署位置的不同修改ip，不能为localhost
     */
    private static String ip = "192.168.0.190";
    /**
     * 向docker守护进行发起的请求，通过容器名称获取容器ID
     */
    private static String collectContainerIDUrl = "http://" + ip +":2375/containers/" + containerName + "/json";

    /**
     * 向http服务器发送的请求，通过该请求获取cu，ru
     */
    private static String collectConfigUrl = "http://" + ip +":8081/Restaurant/httpServlet";

    /**
     * 获取容器此时的cu，ru
     * @return
     */
    public static String[] collectionConfigs(){
        if(containerID  != null){
            System.out.println("containerID:"+containerID);
            String  result = sendRequest(collectConfigUrl + "?containerID=" + containerID);
            System.out.println("result : " + result);
            return result.split("&");
        }else {
            String temp = sendRequest(collectContainerIDUrl);
            containerID = parseCollectContainerID(temp);
            System.out.println("get containerID:"+containerID);
            String  result = sendRequest(collectConfigUrl + "?containerID=" + containerID);
            System.out.println("get result : "+ result);
            return result.split("&");
        }
    }

    /**
     * 发送get请求，得到返回结果
     * @param url
     * @return
     */
    public static String sendRequest(String url) {
        String result = "";
        BufferedReader in = null;
        URL realUrl  = null;
        try {
            realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 将获取containerID的结果进行解析，从而拿到ID
     * @param str http请求返回的结果
     * @return containerID
     */
    private static String parseCollectContainerID(String str){
        JSONObject jsonObject = JSONObject.fromObject(str);
        return jsonObject.get("Id").toString();
    }
}
