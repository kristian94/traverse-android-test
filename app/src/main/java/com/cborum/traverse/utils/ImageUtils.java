package com.cborum.traverse.utils;

import android.graphics.Bitmap;

/**
 * Created by Kristian Nielsen on 09-11-2016.
 */

public class ImageUtils {
    // Skamløst stjålet fra StackoverFlow... cropper et billede kvadratisk
    public static Bitmap cropToSquare(Bitmap bitmap) {
        try{
            int width = bitmap.getWidth();

            int height = bitmap.getHeight();
            int newWidth = (height > width) ? width : height;
            int newHeight = (height > width) ? height - (height - width) : height;
            int cropW = (width - height) / 2;
            cropW = (cropW < 0) ? 0 : cropW;
            int cropH = (height - width) / 2;
            cropH = (cropH < 0) ? 0 : cropH;
            Bitmap cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);

            return cropImg;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }


    }




}
