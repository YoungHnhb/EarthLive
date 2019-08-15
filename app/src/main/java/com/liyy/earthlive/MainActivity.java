package com.liyy.earthlive;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.liyy.earthlive.tool.DownloadFile;

import java.io.IOException;
import java.lang.ref.WeakReference;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DownloadFile downloadFile;
    private MergeImageHandler mergeImageHandler;
    private ImageView mWallPaper;
    private Button mGetWallPaper;
    private Button mSetWallPaper;
    private Bitmap resultBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mergeImageHandler = new MergeImageHandler(MainActivity.this);
        downloadFile = new DownloadFile(mergeImageHandler);
        mWallPaper = (ImageView) findViewById(R.id.iv_wallpaper);
        mGetWallPaper = (Button) findViewById(R.id.download);
        mSetWallPaper = (Button) findViewById(R.id.set_wallpaper);
        mGetWallPaper.setOnClickListener(this);
        mSetWallPaper.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download:
                downLoadWallpaper();
                break;
            case R.id.set_wallpaper:
                setWallPaper();
                break;
        }
    }

    private void downLoadWallpaper() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadFile.start();
            }
        }).start();
    }

    private void setWallPaper() {
        DisplayMetrics display = getResources().getDisplayMetrics();
        int width = display.widthPixels;
        int height = display.heightPixels;
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(MainActivity.this);
//        wallpaperManager.suggestDesiredDimensions(width, height);
        try {
            wallpaperManager.setBitmap(resultBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class MergeImageHandler extends Handler {

        public final static int DOWNLOAD_DONE = 0x200;
        private WeakReference<MainActivity> ref;
        private Context context;

        public MergeImageHandler(MainActivity activity) {
            ref = new WeakReference<>(activity);
            this.context = activity.getApplicationContext();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case DOWNLOAD_DONE:
                    renderImage();
                    break;
            }
        }

        private void renderImage() {
            MainActivity activity = ref.get();
            if (activity == null) {
                return;
            }
            String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + DownloadFile.IMAGE_FILE_NAME;
            Bitmap img0_0 = BitmapFactory.decodeFile(fileName);
            DisplayMetrics display = activity.getResources().getDisplayMetrics();
            int width = display.widthPixels;
            int height = display.heightPixels;
            activity.resultBitmap = fillBitmap(img0_0, width, height);
            activity.mWallPaper.setImageBitmap(activity.resultBitmap);
            activity.mSetWallPaper.setVisibility(View.VISIBLE);
        }

        private Bitmap fillBitmap(Bitmap bitmap, int width, int height) {
            int imgWidth = bitmap.getWidth();
            int imgHeight = bitmap.getHeight();
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            height = (int) (imgWidth * 1.0 / width * height);
            Bitmap resultBitmap = Bitmap.createBitmap(imgWidth, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(resultBitmap);
            canvas.drawRect(0, 0, width, height, paint);
            canvas.drawBitmap(bitmap, 0, height / 2 - imgHeight / 2, null);
            return resultBitmap;
        }

    }
}
