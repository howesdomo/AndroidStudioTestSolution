package cn.com.enpot.cncomenpotdemo.common;

import android.util.Log;

/**
 * Created by Howe on 2017/7/26.
 */

public class EnpotLog
{
    public static void e(String tag, Exception ex)
    {
        String errorMsg = "method : " + tag + "e:" + ex.getMessage() + "/r/n" + ex.getStackTrace();
        Log.e("ENPOT ERROR", errorMsg);
    }
    
    public static void e(String tag, String exMsg)
    {
        Log.e("ENPOT ERROR", exMsg);
    }
    
    public static void i(String info)
    {
        Log.i("ENPOT INFO", info);
    }
}
