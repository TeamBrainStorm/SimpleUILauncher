package com.android.launcher3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import com.android.launcher3.compat.LauncherActivityInfoCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class IconProvider {

    private static final boolean DBG = false;
    private static final String TAG = "IconProvider";

    protected String mSystemState;
    private HashMap<String,Drawable> themIconMap;

    public IconProvider() {
        updateSystemStateString();
        initThemeIcon();
    }

    public static IconProvider loadByName(String className, Context context) {
        if (TextUtils.isEmpty(className)) return new IconProvider();
        if (DBG) Log.d(TAG, "Loading IconProvider: " + className);
        try {
            Class<?> cls = Class.forName(className);
            return (IconProvider) cls.getDeclaredConstructor(Context.class).newInstance(context);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | ClassCastException | NoSuchMethodException | InvocationTargetException e) {
            Log.e(TAG, "Bad IconProvider class", e);
            return new IconProvider();
        }
    }

    public void updateSystemStateString() {
        mSystemState = Locale.getDefault().toString();
    }

    public String getIconSystemState(String packageName) {
        return mSystemState;
    }

    // 获取图标存到缓存
    public Drawable getIcon(LauncherActivityInfoCompat info, int iconDpi) {

        // 遍历集合，匹配包名，如果有相同则取图标
        Iterator iter = themIconMap.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry entry = (Map.Entry)iter.next();
            String key = (String) entry.getKey();
            if(key.equals(info.getComponentName().getPackageName())){
                return (Drawable) entry.getValue();
            }
        }

        return info.getIcon(iconDpi);
    }

    // 获取zip包中的图片，存到HashMap中，key：String文件名中的包名部分  value: Drawable
    private void initThemeIcon (){

        File file = new File("/system/media/icon.zip");
        themIconMap = new HashMap<>();
        ZipFile zipFile = null;
        ZipInputStream zipIn = null;
        ZipEntry zipEn = null;
        Drawable drawable = null;
        String name;

        try {
            zipFile = new ZipFile("/system/media/icon.zip");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if(file != null){
                zipIn = new ZipInputStream(new FileInputStream(file));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            if(zipFile != null){
                while ((zipEn = zipIn.getNextEntry()) != null) {
                    if (!zipEn.isDirectory()) {
                        name = fileName(zipEn.getName());
                        drawable = bitmap2Drawable(BitmapFactory.decodeStream(zipFile.getInputStream(zipEn)));
                        Log.i(TAG,"zip--file name " + name);
                        Log.i(TAG,"zip--drawable " + drawable);
                        themIconMap.put(name,drawable);
                    }
                    zipIn.closeEntry();
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 截取文件名(icon/com.qq.ddz.png)中的包名
    private String fileName(String name){

        String subset1;
        if(name != null){
            subset1 = name.substring(name.indexOf("/")+1,name.length());
            return subset1.substring(0,subset1.lastIndexOf("."));
        }else{
            return null;
        }
    }

    //bitmap转drawable
    private Drawable bitmap2Drawable(Bitmap bitmap) {
        BitmapDrawable bd = new BitmapDrawable(bitmap);
        Drawable d = (Drawable) bd;
        return d;
    }
}
