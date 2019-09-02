package com.liyy.earthlive.main;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.liyy.earthlive.R;
import com.liyy.earthlive.base.BaseActivity;
import com.liyy.earthlive.base.BasePresenter;
import com.liyy.earthlive.service.DownloadService;
import com.liyy.earthlive.tool.Tools;
import com.liyy.earthlive.util.Constants;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends BaseActivity implements View.OnClickListener,
        EasyPermissions.PermissionCallbacks {

    private ImageView mWallPaper;
    private Button mGetWallPaper;
    private Button mSetWallPaper;

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        mWallPaper = (ImageView) findViewById(R.id.iv_wallpaper);
        mGetWallPaper = (Button) findViewById(R.id.download);
        mSetWallPaper = (Button) findViewById(R.id.set_wallpaper);
    }

    @Override
    public void setListener() {
        mGetWallPaper.setOnClickListener(this);
        mSetWallPaper.setOnClickListener(this);
    }

    @Override
    protected void processLogic() {
        methodRequiresTwoPermission();
        startService();
        initReceiver();
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.RECEIVER_ACTION_GET_IMAGE_SUCCESS);
        filter.addAction(Constants.RECEIVER_ACTION_GET_IMAGE_FAILED);
        registerReceiver(mReceiver, filter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case Constants.RECEIVER_ACTION_GET_IMAGE_SUCCESS:
                    renderImage();
                    break;
                case Constants.RECEIVER_ACTION_GET_IMAGE_FAILED:

                    break;
            }
        }
    };

    private void methodRequiresTwoPermission() {
        String[] perms = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.SET_WALLPAPER};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            renderImage();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "获取文件保存权限", 0, perms);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download:
                downLoadWallpaper();
                break;
            case R.id.set_wallpaper:
                setWallPaper();
                mDownloadBinder.refresh();
                break;
        }
    }

    private void downLoadWallpaper() {
        sendBroadcast(new Intent(Constants.RECEIVER_ACTION_UPDATE_WALLPAPER));
    }

    private void setWallPaper() {
        sendBroadcast(new Intent(Constants.RECEIVER_ACTION_SET_WALLPAPER));
    }

    private DownloadService.DownloadBinder mDownloadBinder;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDownloadBinder = (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private void startService() {
        Intent startIntent = new Intent(this, DownloadService.class);
        startService(startIntent);
        bindService(startIntent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        renderImage();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        methodRequiresTwoPermission();
    }

    private void renderImage() {
        String fileName = Tools.getOutputPath() + "/" + Constants.IMAGE_FILE;
        Bitmap mResultBitmap = BitmapFactory.decodeFile(fileName);
        if (mResultBitmap == null) {
            return;
        }
        DisplayMetrics display = getResources().getDisplayMetrics();
        int width = display.widthPixels;
        int height = display.heightPixels;
        mResultBitmap = Tools.fillBitmap(mResultBitmap, width, height);
        mWallPaper.setImageBitmap(mResultBitmap);
        mSetWallPaper.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Update Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
}
