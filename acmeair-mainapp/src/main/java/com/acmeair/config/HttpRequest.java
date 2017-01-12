package com.acmeair.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Administrator on 2017/1/6.
 */
public class HttpRequest {

    private static String param = "uid0@email.com";
    private static String url = "http://192.168.0.190/customer/acmeair-cs/rest/api/customer/byid/" + param;
    private static String username = "";
    private static int PAPAMNUM = 10;

    private static String generateUserName(){
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for(int i=0; i<PAPAMNUM; i++){
            stringBuilder.append("uid" + random.nextInt(500) + "@email.com;");
        }
        return stringBuilder.toString().substring(0,stringBuilder.toString().length()-1);
    }

    public static String sendGet(String sessionId, long time) {
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url+"?" + "sendtime=" + time + "&username=" + generateUserName());
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("Cookie","sessionid="+sessionId);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
            // 建立实际的连接
            connection.connect();

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
}
