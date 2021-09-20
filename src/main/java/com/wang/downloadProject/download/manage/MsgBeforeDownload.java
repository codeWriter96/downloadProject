package com.wang.downloadProject.download.manage;

import com.wang.downloadProject.Exception.PathException;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MsgBeforeDownload {
    private static final MsgBeforeDownload downloadRes = new MsgBeforeDownload();
    private MsgBeforeDownload() {
    }
    public static MsgBeforeDownload newInstance(){
        return downloadRes;
    }


    /*
    * 解析下载文件
    * */
    public static boolean startDownLoad(String webPath, String locPath , HttpServletRequest request) {
        boolean flag =false;
        HttpURLConnection httpURLConnection =null;
        try{
            //获取URL
            URL url = new URL(webPath);
            //新建HttpURLConnection，是从http服务器获取数据
            httpURLConnection = (HttpURLConnection) url.openConnection();
            //向文件所在服务器附加发送浏览器标识（用户代理）
            httpURLConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
            //获取要下载内容的大小
            long length = httpURLConnection.getContentLengthLong();
            if(length==-1){
                throw new PathException("该网站无任何资源");
            }
            //将结果放入会话作用域内
            request.getSession().setAttribute("webPath",webPath);
            request.getSession().setAttribute("locPath",locPath);
            request.getSession().setAttribute("length",length);
            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(httpURLConnection!=null){
                httpURLConnection.disconnect();
            }
        }
        return flag;
    }
}
