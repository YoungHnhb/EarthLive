package com.liyy.earthlive.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;

import com.liyy.earthlive.R;
import com.liyy.earthlive.main.MainActivity;
import com.liyy.earthlive.tool.DownloadCallback;
import com.liyy.earthlive.tool.DownloadTask;
import com.liyy.earthlive.tool.Tools;
import com.liyy.earthlive.util.Constants;
import com.liyy.earthlive.util.SPHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DownloadService extends Service {

    private static String TAG = "DownloadService";
    private Context mContext;
    private static final String SP_UPDATE_DATE_KEY = "SP_UPDATE_DATE_KEY";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

    private DownloadBinder mBinder = new DownloadBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Log.e(TAG, "onCreate: " + TAG);
        startNotification();
        initReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: " + TAG);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    private void startNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setTicker("Notification comes")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setContentTitle("EarthLive")
                .setContentText("Last Update Time:" + simpleDateFormat.format(new Date()));
        startForeground(1, notificationBuilder.build());
    }

    private void initReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Constants.RECEIVER_ACTION_UPDATE_SET_WALLPAPER);
        intentFilter.addAction(Constants.RECEIVER_ACTION_UPDATE_WALLPAPER);
        intentFilter.addAction(Constants.RECEIVER_ACTION_SET_WALLPAPER);
        registerReceiver(mReceiver, intentFilter);
    }

    public class DownloadBinder extends Binder {

        public void refresh() {
            // TODO: 2019/8/26 refresh image
            Log.e(TAG, "refresh: in download Service");
        }
    }

    private boolean checkUpdateTime() {
        long nowTime = System.currentTimeMillis();
        long cycle = 30 * 60 * 1000;
        long realLatestTime = SPHelper.getInstance().getLongSharePreference(SP_UPDATE_DATE_KEY, 0);
        return ((nowTime - realLatestTime) >= cycle);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case Intent.ACTION_TIME_TICK:
                    Log.e("RRR", "ACTION_TIME_TICK");
                    if (checkUpdateTime()) {
                        updateWallpaper(true);
                        SPHelper.getInstance().setLongSharePreference(SP_UPDATE_DATE_KEY, System.currentTimeMillis());
                    }
                    break;
                case Constants.RECEIVER_ACTION_UPDATE_WALLPAPER:
                    updateWallpaper(false);
                    break;
                case Constants.RECEIVER_ACTION_SET_WALLPAPER:
                    setWallpaper();
                    break;
            }
        }
    };

    private void updateWallpaper(final boolean setWallpaper) {
        new Thread(new DownloadTask(new DownloadCallback() {
            @Override
            public void downloadSuccess(String fileName) {
                mContext.sendBroadcast(new Intent(Constants.RECEIVER_ACTION_GET_IMAGE_SUCCESS));
                if (setWallpaper) {
                    setWallpaper();
                }
            }

            @Override
            public void downloadError(int errorCode) {
                mContext.sendBroadcast(new Intent(Constants.RECEIVER_ACTION_GET_IMAGE_FAILED));
            }
        })).start();
    }

    private void setWallpaper() {
        String fileName = Tools.getOutputPath() + Constants.IMAGE_FILE;
        Bitmap mResultBitmap = BitmapFactory.decodeFile(fileName);
        if (mResultBitmap == null) {
            return;
        }
        DisplayMetrics display = getResources().getDisplayMetrics();
        int width = display.widthPixels;
        int height = display.heightPixels;
        mResultBitmap = Tools.fillBitmap(mResultBitmap, width, height);
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setBitmap(mResultBitmap, null, false, WallpaperManager.FLAG_SYSTEM);
                wallpaperManager.setBitmap(mResultBitmap, null, false, WallpaperManager.FLAG_LOCK);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        startNotification();
    }

}
