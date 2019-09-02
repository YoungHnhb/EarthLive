package com.liyy.earthlive.tool;

public interface DownloadCallback {

    void downloadSuccess(String fileName);

    void downloadError(int errorCode);
}
