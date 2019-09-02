package com.liyy.earthlive.tool;

import android.text.TextUtils;

import com.liyy.earthlive.util.Constants;
import com.liyy.earthlive.util.SPHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DownloadTask implements Runnable {

    private static final String SP_LAST_DATE_KEY = "SP_EARTH_LIVE_LAST_DATE";

    private DownloadCallback mCallback;


    public DownloadTask(DownloadCallback callback) {
        mCallback = callback;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.LATEST_URL)
                .build();
        try {
            Response dateResponse = client.newCall(request).execute();
            if (dateResponse.body() == null) {
                mCallback.downloadError(dateResponse.code());
                return;
            }
            String responseStr = dateResponse.body().string();
            JSONObject response = new JSONObject(responseStr);
            String lastDate = formatDate(response.getString("date"));
            if (lastDate.equals(SPHelper.getInstance().getStringSharePreference(SP_LAST_DATE_KEY, ""))) {
                mCallback.downloadSuccess(Constants.IMAGE_FILE);
                return;
            } else {
                SPHelper.getInstance().setStringSharePreference(SP_LAST_DATE_KEY, lastDate);
            }
            Request getImageRequest = new Request.Builder()
                    .url(String.format(Constants.GET_IMAGE_URL, lastDate.replace("_", "/")))
                    .build();
            Response getImageResponse = client.newCall(getImageRequest).execute();
            inputStream = getImageResponse.body().byteStream();
            RandomAccessFile randomAccessFile = new RandomAccessFile(Tools.getOutputPath() + Constants.IMAGE_FILE, "rw");
            byte[] bytes = new byte[1024];
            int length;
            while ((length = inputStream.read(bytes)) != -1) {
                randomAccessFile.write(bytes, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                    mCallback.downloadSuccess(Constants.IMAGE_FILE);
                } catch (IOException e) {
                    e.printStackTrace();
                    mCallback.downloadError(-2);
                }
            }
        }
    }

    private String formatDate(String str) {
        // "2019-08-14 13:30:00"
        // to
        // "2019_08_14_133000"
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        return str.replaceAll("-| ", "_").replace(":", "");
    }
}
