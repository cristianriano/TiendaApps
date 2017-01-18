package com.example.cristianr.tiendaapps.helpers;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by cristianr on 17/01/2017.
 */

public class CacheHelper {

    public static final String DIR_NAME = ".app_store_dir";

    public static boolean saveImage(Bitmap bitmap, String filename){
        // Get folder to save cache images
        File cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), DIR_NAME);
        // Create it in case don't exists
        if(!cacheDir.exists())
            cacheDir.mkdir();
        // Get file path hashing the name
        File file = new File(cacheDir.getAbsolutePath(), String.valueOf(filename.hashCode()));
        if (file.exists())
            return true;

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static File getFile(String filename){
        File file;
        try{
            File cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), DIR_NAME);
            file = new File(cacheDir.getAbsolutePath(), String.valueOf(filename.hashCode()));
            if(!file.exists())
                return  null;
        }
        catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
        return file;
    }

}
