package com.enpot.commons;

import android.app.Application;
import android.app.Service;
import android.os.Vibrator;

import com.baidu.mapapi.SDKInitializer;

import cn.com.enpot.enpot_location.LocationMonitorService;
import cn.com.enpot.enpot_location.commom.StaticInfo;

/**
 * Created by Howe on 2017/6/13.
 */

public class EnpotApplication extends Application
{
    public LocationMonitorService locationMonitorService = null;
    
    @Override
    public void onCreate()
    {
        super.onCreate();
        
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    
        StaticInfo.InitStaticInfo(this);
        
        locationMonitorService = new LocationMonitorService(getApplicationContext());
        SDKInitializer.initialize(getApplicationContext());
    }
}