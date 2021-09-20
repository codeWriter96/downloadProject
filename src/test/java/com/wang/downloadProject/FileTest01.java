package com.wang.downloadProject;

import com.wang.downloadProject.download.constant.Constant;
import com.wang.downloadProject.download.manage.DownloadMission;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class FileTest01 {

    @Test
    public void Test01() {

        //写只要一个，读要多个
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream("L:\\新建文件夹/PCQQ2021.exe"));
            int len = -1;
            for (int i = 0; i < Constant.THREAD_NUM; i++) {
                BufferedInputStream bis = null;
                try {
                    String fileName1 = "L:\\新建文件夹/PCQQ2021.exe" + ".temp" + i;
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
    }
}
