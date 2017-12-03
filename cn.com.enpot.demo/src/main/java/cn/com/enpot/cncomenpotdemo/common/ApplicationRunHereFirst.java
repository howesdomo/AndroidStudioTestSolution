package cn.com.enpot.cncomenpotdemo.common;

import android.app.Application;

/**
 * Created by Howe on 2017/7/26.
 */

public class ApplicationRunHereFirst extends Application
{
    
    @Override
    public void onCreate()
    {
        super.onCreate();
        
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        
    }
}