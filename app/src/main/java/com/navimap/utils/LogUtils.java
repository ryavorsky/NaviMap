package com.navimap.utils;

import android.util.Log;
import com.navimap.settings.Constants;

public class LogUtils {
    public static void i(String msg) {
        if (Constants.INFO_MODE) {
            Log.i(Constants.LOG_TAG, msg);
        }
    }

    public static void d(String msg) {
        if (Constants.DEBUG_MODE) {
            Log.d(Constants.LOG_TAG, msg);
        }
    }

    public static void d(String msg, Throwable e) {
        if (Constants.DEBUG_MODE) {
            Log.d(Constants.LOG_TAG, msg, e);
        }
    }

    public static void e(String msg, Throwable e) {
        Log.e(Constants.LOG_TAG, msg, e);
    }

    public static void e(String msg) {
        Log.e(Constants.LOG_TAG, msg);
    }

    public static void e(Throwable e) {
        if (e != null) {
            Log.e(Constants.LOG_TAG, e.getMessage(), e);
        }
    }

}
