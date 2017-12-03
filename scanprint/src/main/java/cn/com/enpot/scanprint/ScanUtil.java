package cn.com.enpot.scanprint;

import android.content.Context;
import android.content.IntentFilter;

import com.android.barcodescandemo.ScannerInerface;

/**
 * Created by Howe on 2017/8/21.
 */

public class ScanUtil
{
    public static IntentFilter FILTER;
    public static ScannerInerface SCANNER;
    
    public static void Scan(Context mContext)
    {
        if (ScanUtil.SCANNER == null)
        {
            ScanUtil.SCANNER = new ScannerInerface(mContext);
            // 扫描功能
            ScanUtil.SCANNER.open();
            ScanUtil.SCANNER.setOutputMode(1);//使用广播模式
            ScanUtil.SCANNER.enablePlayBeep(false);
            
            ScanUtil.FILTER = new IntentFilter("android.intent.action.SCANRESULT");
        }
    }
}
