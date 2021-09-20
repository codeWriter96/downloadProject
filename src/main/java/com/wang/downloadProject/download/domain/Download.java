package com.wang.downloadProject.download.domain;

public class Download {

    private String id;
    private String webPath;
    private String locPath;
    private String createTime;

    public Download() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWebPath() {
        return webPath;
    }

    public void setWebPath(String webPath) {
        this.webPath = webPath;
    }

    public String getLocPath() {
        return locPath;
    }

    public void setLocPath(String locPath) {
        this.locPath = locPath;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
