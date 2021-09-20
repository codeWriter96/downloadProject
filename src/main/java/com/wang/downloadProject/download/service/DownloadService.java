package com.wang.downloadProject.download.service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface DownloadService {

    boolean createDownload(String webPath, String locPath, HttpServletRequest request);

    String getPath();

    boolean startDownload(String webPath, String locPath,long length);
}
