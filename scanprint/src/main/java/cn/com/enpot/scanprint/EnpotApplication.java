package cn.com.enpot.scanprint;

import android.app.Application;

/**
 * Created by Howe on 2017/8/21.
 */

public class EnpotApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
    
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        
        StaticInfo.InitStaticInfo(this);
        
        ScanUtil.Scan(this);
    }
}
