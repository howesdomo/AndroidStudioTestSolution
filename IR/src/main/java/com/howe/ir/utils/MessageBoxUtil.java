package com.howe.ir.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.enpot.utils.MessageBox;

import com.howe.ir.commom.StaticInfo;
import com.howe.ir.model.SOAPResult;

/**
 * Created by Howe on 2017/4/1.
 */

public class MessageBoxUtil
{
    /**
     * 播放错误提示音
     */
    public static boolean IsExceptionPlayErrorSound = true;

    /**
     * 业务逻辑错误播放错误提示音
     */
    public static boolean IsBusinessExceptionPlayErrorSound = true;

    public static String ErrorTitle = "错误";

    public static int ShowInfoDialog(Activity activity, String info)
    {
        MessageBox msgBox = new MessageBox(activity);
        return msgBox.showDialog(info, "提示");
    }

    public static int ShowComfirm(Activity activity, String info)
    {
        MessageBox msgBox = new MessageBox(activity, MessageBox.MessageBoxStyle_Confirm);
        return msgBox.showDialog(info, "提示");
    }

    /***
     * 联网等待提示
     * @param c
     */
    public static void ShowWaitingToast(Context c)
    {
        if (IsExceptionPlayErrorSound)
        {
            StaticInfo.PlayErrorSound();
        }
        String e = "正在连接服务器，请稍后。。。";
        Toast.makeText(c, e, Toast.LENGTH_LONG).show();
    }

    public static void ShowExceptionToast(Context c, String e)
    {
        if (IsExceptionPlayErrorSound)
        {
            StaticInfo.PlayErrorSound();
        }

        Toast.makeText(c, e, Toast.LENGTH_LONG).show();
    }

    public static void ShowExceptionToast(Context c, Exception e)
    {
        if (IsExceptionPlayErrorSound)
        {
            StaticInfo.PlayErrorSound();
        }

        String errorMsg = e.getMessage() + "\r\n" + e.getStackTrace();
        Toast.makeText(c, errorMsg, Toast.LENGTH_LONG).show();
    }

    /**
     * 仿照C# 的 MessageBox.ShowDialog() 线程阻塞的
     *
     * @param activity
     * @param e
     */
    public static void ShowExceptionDialog(Activity activity, String e)
    {
        if (IsExceptionPlayErrorSound)
        {
            StaticInfo.PlayErrorSound();
        }

        MessageBox msgBox = new MessageBox(activity);
        msgBox.showDialog(e, ErrorTitle);
    }

    /**
     * 仿照C# 的 MessageBox.ShowDialog() 线程阻塞的
     *
     * @param activity
     * @param e
     */
    public static void ShowExceptionDialog(Activity activity, Exception e)
    {
        if (IsExceptionPlayErrorSound)
        {
            StaticInfo.PlayErrorSound();
        }

        MessageBox msgBox = new MessageBox(activity);
        String errorMsg = e.getMessage() + "\r\n" + e.getStackTrace();
        msgBox.showDialog(errorMsg, ErrorTitle);
    }

    //region SOAPResult 系统异常

    public static void ShowExceptionToast(Context c, SOAPResult soapResult)
    {
        if (IsExceptionPlayErrorSound)
        {
            StaticInfo.PlayErrorSound();
        }

        Toast.makeText(c, soapResult.ExceptionInfo, Toast.LENGTH_LONG).show();
    }

    /**
     * 仿照C# 的 MessageBox.ShowDialog() 线程阻塞的
     *
     * @param activity
     * @param soapResult
     */
    public static void ShowExceptionDialog(Activity activity, SOAPResult soapResult)
    {
        if (IsExceptionPlayErrorSound)
        {
            StaticInfo.PlayErrorSound();
        }

        MessageBox msgBox = new MessageBox(activity);
        msgBox.showDialog(soapResult.ExceptionInfo, ErrorTitle);
    }

    //endregion

    //region SOAPResult 业务逻辑错误

    public static void ShowBusinessExceptionToast(Context c, SOAPResult soapResult)
    {
        if (IsBusinessExceptionPlayErrorSound)
        {
            StaticInfo.PlayErrorSound();
        }

        Toast.makeText(c, soapResult.BusinessExceptionInfo, Toast.LENGTH_LONG).show();
    }

    /**
     * 仿照C# 的 MessageBox.ShowDialog() 线程阻塞的
     *
     * @param activity
     * @param soapResult
     */
    public static void ShowBusinessExceptionDialog(Activity activity, SOAPResult soapResult)
    {
        if (IsBusinessExceptionPlayErrorSound)
        {
            StaticInfo.PlayErrorSound();
        }

        MessageBox msgBox = new MessageBox(activity);
        msgBox.showDialog(soapResult.BusinessExceptionInfo, ErrorTitle);
    }

    /**
     * 仿照C# 的 MessageBox.ShowDialog() 线程阻塞的
     *
     * @param activity
     * @param errorMsg
     */
    public static void ShowBusinessExceptionDialog(Activity activity, String errorMsg)
    {
        if (IsBusinessExceptionPlayErrorSound)
        {
            StaticInfo.PlayErrorSound();
        }

        MessageBox msgBox = new MessageBox(activity);
        msgBox.showDialog(errorMsg, ErrorTitle);
    }

    //endregion 业务逻辑错误
}
