package com.acg12.redenvelope.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

/**
 * Description
 * Created by chengwanying on 2018/1/25.
 * Company BeiJing ACG12
 */

public class BaseApplication extends Application implements Thread.UncaughtExceptionHandler {

    /**
     * 全局Context，原理是因为Application类是应用最先运行的，所以在我们的代码调用时，该值已经被赋值过了
     */
    private static BaseApplication mInstance;
    /**
     * 主线程Looper
     */
    private static Looper mMainLooper;




    @SuppressWarnings("deprecation")
    @Override
    public void onCreate() {
        super.onCreate();

        mMainLooper = getMainLooper();
        mInstance = this;

    }



    public static BaseApplication getApplication() {
        return mInstance;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        android.os.Process.killProcess(android.os.Process.myPid());// 专注自杀的方法,只能自杀的方法.
        System.exit(0);
    }


    /**
     * 获取主线程的looper
     */
    public static Looper getMainThreadLooper() {
        return mMainLooper;
    }

    private Handler applicationHandler;

    public Handler getApplicationHandler() {
        if (applicationHandler == null) {
            applicationHandler = new Handler(mMainLooper == null ? getMainLooper() : mMainLooper);
        }
        return applicationHandler;
    }


}
