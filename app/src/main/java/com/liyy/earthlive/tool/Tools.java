package com.liyy.earthlive.tool;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;

import java.io.File;

public class Tools {

    public static String getOutputPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    public static Bitmap fillBitmap(Bitmap bitmap, int width, int height) {
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
