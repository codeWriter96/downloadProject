package com.wang.downloadProject.download.manage;

import com.wang.downloadProject.download.constant.Constant;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class StartDownload {
    //单例模式
    private static final StartDownload downloadRes = new StartDownload();
    private StartDownload() {
    }
    public static StartDownload newInstance(){
        return downloadRes;
    }

    //采用ThreadPoolExecutor创建五个线程下载
    private final ThreadPoolExecutor  poolExecutor = new ThreadPoolExecutor(Constant.THREAD_NUM,Constant.THREAD_NUM,0, TimeUnit.SECONDS,new ArrayBlockingQueue<>(Constant.THREAD_NUM));
    private CountDownLatch countDownLatch = null;
    public boolean downloadRes(String webPath, String locPath,long FileLength){
        boolean flag =false;
        URL url =null;
        AtomicLong length = new AtomicLong(FileLength);
        try {
            //获取url
            url = new URL(webPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //start download
        return startDownload(url,webPath,locPath,length);
    }

    //多线程下载
    private boolean startDownload(URL url, String webPath, String locPath, AtomicLong length){
        countDownLatch = new CountDownLatch(Constant.THREAD_NUM);
        boolean flag = false;

        //获取文件后缀名字
        int index = webPath.lastIndexOf("/");
        String name = webPath.substring(index+1);
        //文件路径全名
        String fileName = null;
        if(locPath.endsWith("\\")){
            fileName = locPath+name;
        }else {
            fileName = locPath+"/"+name;
        }

        //less MIN_LENGTH singleThread
        if (length.longValue()< Constant.MIN_LENGTH){
            flag = singleThread(url,fileName);
        }else {
            //多线程下载
            mutiThread(url, length, fileName);
            //判断是否下载完成，创建阻塞
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //合并文件
            merge(fileName);
            //删除文件
            clearTemp(fileName);
        }
        return flag;
    }

    //删除零时文件
    private void clearTemp(String fileName) {
        for (int i = 0; i <Constant.THREAD_NUM ; i++) {
            File file = new File(fileName+".temp" + i);
            file.delete();
        }
    }

    //单线程下载
    private boolean singleThread(URL url, String fileName){
        HttpURLConnection httpURLConnection = null;
        InputStream is = null;
        BufferedOutputStream bos = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            //设置用户代理
            httpURLConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
            is = httpURLConnection.getInputStream();

            //创建输出流
            bos = new BufferedOutputStream(new FileOutputStream(fileName));
            int len =-1;
            while((len=is.read())!=-1){
                bos.write(len);
            }
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            //关闭流
            if(bos != null){
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(is !=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    //多线程下载
    private void mutiThread(URL url, AtomicLong length, String fileName){
        //计算分快数
        long size = length.longValue()/Constant.THREAD_NUM;
        for (int i = 0; i <Constant.THREAD_NUM ; i++) {
            long startPos = size*i;
            long endPos = size*(i+1);
            DownloadMission downloadMission = new DownloadMission(countDownLatch,url,fileName,startPos,endPos,i);
            Future<Boolean> future = poolExecutor.submit(downloadMission);
        }

    }

    //合并文件
    public boolean merge(String fileName){
        //写只要一个，读要多个
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(fileName));
            int len = -1;
            for (int i = 0; i < Constant.THREAD_NUM; i++) {
                BufferedInputStream bis = null;
                try {
                    String fileName1 = fileName + ".temp" + i;
                    bis = new BufferedInputStream(new FileInputStream(fileName1));
                    while ((len = bis.read()) != -1) {
                        bos.write(len);
                    }
                } finally {
                    if(bis !=null){
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        } finally {
            //关闭流
            if(bos != null){
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
