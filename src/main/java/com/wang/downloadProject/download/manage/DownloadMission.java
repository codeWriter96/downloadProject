package com.wang.downloadProject.download.manage;

import com.wang.downloadProject.utils.HttpUtils;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class DownloadMission implements Callable<Boolean> {
    private URL url;
    private String fileName;
    private int i;
    private long startPos;
    private long endPos;
    private CountDownLatch countDownLatch;

    public DownloadMission(CountDownLatch countDownLatch, URL url, String fileName, long startPos, long endPos, int i) {
        this.url = url;
        this.fileName =fileName;
        this.i = i;
        this.startPos = startPos;
        this.endPos = endPos;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public Boolean call(){
        HttpURLConnection httpURLConnection = HttpUtils.getHttpURLConnection(url, startPos, endPos, i);
        String fileName1 = fileName+".temp"+i;
        System.out.println("名字"+fileName1+"字节数"+httpURLConnection.getRequestProperty("RANGE"));

        InputStream is = null;
        BufferedOutputStream bos = null;
        try{
            is = httpURLConnection.getInputStream();
            //开启下载
            bos = new BufferedOutputStream(new FileOutputStream(fileName1));
            int len = -1;
            while((len = is.read())!=-1){
                bos.write(len);
            }
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
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
            //计数器减1
            countDownLatch.countDown();
        }
        return true;
    }
}
