package com.enpot.utils;

import android.util.Log;

import java.io.Console;

/**
 * Created by Howe on 2017/3/23.
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
