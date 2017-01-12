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

    private static String containerName = "cs";

    private static String collectContainerIDUrl = "http://192.168.0.190:2375/containers/" + containerName + "/json";

    private static String collectConfigUrl = "http://localhost:8081/Restaurant/httpServlet";

    public static String[] collectionConfigs(){
        if(containerID  != null){
            String  result = sendRequest(collectConfigUrl + "?containerID=" + containerID);
            return result.split("&");
        }else {
            String temp = sendRequest(collectContainerIDUrl);
            containerID = parseCollectContainerID(temp);
            String  result = sendRequest(collectConfigUrl + "?containerID=" + containerID);
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
