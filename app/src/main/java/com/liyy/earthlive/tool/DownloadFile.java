package com.liyy.earthlive.tool;

import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;

import com.liyy.earthlive.MainActivity.MergeImageHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadFile {

    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(20 * 1000, TimeUnit.SECONDS)
            .build();

    private Handler mHandler;
    private static final String LATEST_URL = "http://himawari8-dl.nict.go.jp/himawari8/img/D531106/latest.json";
    private static final String GET_IMAGE_URL = "http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/%s_0_0.png";
    public static final String IMAGE_FILE_NAME = "Earth_Live.png";

    public DownloadFile(Handler handler) {
        mHandler = handler;
    }

    public void start() {
        Request getDateRequest = new Request.Builder()
                .url(LATEST_URL).build();
        Call getDateCall = client.newCall(getDateRequest);
        String dateURI = "";
        try {
            Response dateResponse = getDateCall.execute();
            String responseStr = dateResponse.body().string();
            if (TextUtils.isEmpty(responseStr)) {
                System.out.println("Get latest date error!");
                return;
            }
            JSONObject response = new JSONObject(responseStr);
            dateURI = formatDate(response.getString("date"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request getImageRequest = new Request.Builder()
                .url(String.format(GET_IMAGE_URL, dateURI))
                .build();

        Call call = client.newCall(getImageRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("download error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] resultByte = response.body().bytes();
                String requestURL = call.request().url().toString();
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                FileOutputStream outputStream = new FileOutputStream(new File(path, IMAGE_FILE_NAME));
                outputStream.write(resultByte, 0, resultByte.length);
                outputStream.close();
                System.out.println("success" + requestURL);
                mHandler.sendEmptyMessage(MergeImageHandler.DOWNLOAD_DONE);
            }
        });

    }

    private String formatDate(String str) {
        // "2019-08-14 13:30:00"
        // to
        // "2019/08/14/133000"
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        return str.replaceAll("-| ", "/").replace(":", "");
    }

}
