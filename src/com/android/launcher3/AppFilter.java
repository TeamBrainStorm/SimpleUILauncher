package com.android.launcher3;

import android.content.ComponentName;
import android.text.TextUtils;
import android.util.Log;

/**
 * 1.Class.forName()返回的是类，通过类名加载类（反射中通常用到）
 * 2.newInstance()和new 都能生成一个对象。newInstance()生成对象只能调用无参的构造函数，new关键字生成对象没有这个限制。
 */
public abstract class AppFilter {

    private static final boolean DBG = false;
    private static final String TAG = "AppFilter";

    public abstract boolean shouldShowApp(ComponentName app);

    public static AppFilter loadByName(String className) {
        if (TextUtils.isEmpty(className)) return null;
        if (DBG) Log.d(TAG, "Loading AppFilter: " + className);
        try {
            Class<?> cls = Class.forName(className); //1.
            return (AppFilter) cls.newInstance();   //2.
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Bad AppFilter class", e);
            return null;
        } catch (InstantiationException e) {
            Log.e(TAG, "Bad AppFilter class", e);
            return null;
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Bad AppFilter class", e);
            return null;
        } catch (ClassCastException e) {
            Log.e(TAG, "Bad AppFilter class", e);
            return null;
        }
    }

}
