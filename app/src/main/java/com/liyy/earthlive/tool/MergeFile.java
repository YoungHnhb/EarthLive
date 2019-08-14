package com.liyy.earthlive.tool;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class MergeFile {

    public static Bitmap mergeImageHorizontal(Bitmap img1, Bitmap img2) {
        int w1 = img1.getWidth();
        int w2 = img2.getWidth();
        int h1 = img1.getHeight();
        int h2 = img2.getHeight();
        int height = Math.max(h1, h2);
        Bitmap result = Bitmap.createBitmap(w1 + w2, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(img1, 0, 0, null);
        canvas.drawBitmap(img2, w1, 0, null);
        return result;
    }

    public static Bitmap mergeImageVertical(Bitmap img1, Bitmap img2) {
        int w1 = img1.getWidth();
        int w2 = img2.getWidth();
        int h1 = img1.getHeight();
        int h2 = img2.getHeight();
        int width = Math.max(w1, w2);
        Bitmap result = Bitmap.createBitmap(width, h1 + h2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(img1, 0, 0, null);
        canvas.drawBitmap(img2, 0, h1, null);
        return result;
    }

}
