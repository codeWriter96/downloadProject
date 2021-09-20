package com.wang.downloadProject.download.service.impl;

import com.wang.downloadProject.Exception.InsertException;
import com.wang.downloadProject.Exception.PathException;
import com.wang.downloadProject.download.dao.DownloadDao;
import com.wang.downloadProject.download.domain.Download;
import com.wang.downloadProject.download.manage.MsgBeforeDownload;
import com.wang.downloadProject.download.manage.StartDownload;
import com.wang.downloadProject.download.service.DownloadService;
import com.wang.downloadProject.utils.DateTimeUtil;
import com.wang.downloadProject.utils.FilePath;
import com.wang.downloadProject.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
public class DownloadServiceImpl implements DownloadService {
    @Autowired
    DownloadDao downloadDao;

    public DownloadServiceImpl() {
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean createDownload(String webPath, String locPath, HttpServletRequest request) {
        boolean flag = false;
        //判断下载地址是否为空
        if(webPath==null||locPath==null){
           throw new PathException("输入的下载地址或保存地址为空");
        }

        //开始多线程下载
        boolean res0 = MsgBeforeDownload.startDownLoad(webPath,locPath,request);
        if(!res0){
            throw new InsertException("获取下载信息失败，下载地址有误");
        }

        //新建download
        Download download = new Download();
        download.setId(UUIDUtil.getUUID());
        download.setCreateTime(DateTimeUtil.getSysTime());
        download.setLocPath(locPath);
        download.setWebPath(webPath);
        //在mysql中插入sql
        Integer res1 = downloadDao.insertHistory(download);
        if(res1!=1){
            throw new InsertException("下载失败");
        }
        flag =true;
        return flag;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public String getPath() {
        return FilePath.getPath();
    }

    @Override
    public boolean startDownload(String webPath, String locPath,long length) {
        StartDownload startDownload = StartDownload.newInstance();
        return startDownload.downloadRes(webPath,locPath,length);
    }
}
