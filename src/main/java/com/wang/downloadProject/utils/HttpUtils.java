package com.wang.downloadProject.utils;

import com.wang.downloadProject.download.constant.Constant;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {
    public static HttpURLConnection getHttpURLConnection(URL url, long startPos, long endPos,int i){
        //获取连接
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //设置用户代理
        httpURLConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");

        if(i ==0 ){
            //分块
            httpURLConnection.setRequestProperty("RANGE","bytes="+startPos+"-"+endPos);
        } else if(i== Constant.THREAD_NUM-1){
            //分块
            startPos++;
            httpURLConnection.setRequestProperty("RANGE","bytes="+startPos+"-");
        }else {
            //分块
            startPos++;
            httpURLConnection.setRequestProperty("RANGE","bytes="+startPos+"-"+endPos);
        }
        return httpURLConnection;
    }
}
