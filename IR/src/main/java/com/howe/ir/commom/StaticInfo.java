package com.howe.ir.commom;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Environment;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.enpot.utils.DateTime;
import com.enpot.utils.EnpotLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.howe.ir.R;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;


/**
 * Created by Howe on 2017/3/22.
 */

public class StaticInfo
{
    //region 本项目特有
    protected static boolean isDebugMode = true;

    public static boolean IsDebugMode()
    {
        return StaticInfo.isDebugMode;
    }

    private static String appName = "Howe遥控器";

    //endregion 本项目特有

    /**
     * 初始化StaticInfo信息
     * <p>
     * 其中包含 DataBaseVersionCode, DataBasePath<br/>
     * IMEI, IP, Port, EndPoint
     *
     * @param context
     */
    public static void InitStaticInfo(Context context)
    {
        StaticInfo.DataBaseVersionCode = GetSharedPreferencesValue(context, StaticInfo.SharedPKey_DataBaseVersionKey);

        if (StaticInfo.DataBaseVersionCode == null || StaticInfo.DataBaseVersionCode.isEmpty())
        {
            StaticInfo.DataBaseVersionCode = "1";
            StaticInfo.SetSharedPreferencesKeyValue(context, StaticInfo.SharedPKey_DataBaseVersionKey, StaticInfo.DataBaseVersionCode);
        }

        StaticInfo.DataBasePath = context.getApplicationContext().getDatabasePath(StaticInfo.DataBaseName).getPath();
        StaticInfo.IMEI = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

        if (StaticInfo.IMEI != null && !StaticInfo.IMEI.isEmpty())
        {
            StaticInfo.IMEIName = "设备序列号 " + StaticInfo.IMEI;
        }

        StaticInfo.LoadIPConfig(context);

        //region 加载本程序所需使用的所有音效文件
        StaticInfo.soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        defaultSoundMap = new HashMap<Integer, Integer>();
        defaultSoundMap.put(1, StaticInfo.soundPool.load(context, R.raw.beep, 1));
        defaultSoundMap.put(2, StaticInfo.soundPool.load(context, R.raw.error, 1));
        //endregion

        //region 加载震动设置

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        //endregion
    }

    //region 程序名称

    /**
     * 获取App名称
     *
     * @return
     */
    public static String GetAppName()
    {
        return StaticInfo.appName;
    }

    protected static String versionName = "";

    /**
     * 获取版本名
     *
     * @param activity
     * @return
     */
    public static String GetVersionName(Activity activity)
    {
        if (StaticInfo.versionName != null && !StaticInfo.versionName.isEmpty())
        {
            return StaticInfo.versionName;
        }
        try
        {
            PackageManager packageManager = activity.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(activity.getPackageName(), 0);
            StaticInfo.versionName = packInfo.versionName;
            StaticInfo.versionCode = packInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return StaticInfo.versionName;
    }

    //endregion

    //region 程序版本号

    protected static int versionCode = -9999;

    /**
     * 获取版本号
     *
     * @param activity
     * @return
     */
    public static int GetVersionCode(Activity activity)
    {
        if (StaticInfo.versionCode != -9999)
        {
            return StaticInfo.versionCode;
        }
        try
        {
            PackageManager packageManager = activity.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(activity.getPackageName(), 0);
            StaticInfo.versionName = packInfo.versionName;
            StaticInfo.versionCode = packInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return StaticInfo.versionCode;
    }

    //endregion

    //region WebService 配置

    /**
     * 服务IP
     */
    public static String IP = "192.168.1.215";

    /**
     * 服务端口
     */
    public static String Port = "17901";

    /**
     * 安卓服务名称
     */
    protected static final String androidServer = "APP_Server/APP_WebService.asmx";

    /**
     * 获取安卓服务名称
     */
    public static String GetAndroidServer()
    {
        return StaticInfo.androidServer;
    }

    /**
     * 权限服务名称
     */
    protected static final String securityServer = "SecurityServer/SecurityService.asmx";

    /**
     * 获取权限服务名称
     */
    public static String GetSecurityServer()
    {
        return StaticInfo.securityServer;
    }

    public static String EndPoint = "http://" + StaticInfo.IP + ":" + StaticInfo.Port + "/" + StaticInfo.androidServer;

    public static void UpdateEndPoint()
    {
        StaticInfo.EndPoint = "http://" + StaticInfo.IP + ":" + StaticInfo.Port + "/" + StaticInfo.androidServer;
    }

    /**
     * 获取最新EndPoint 先更新 StaticInfo.EndPoint, 在返回最新的值
     *
     * @return
     */
    public static String GetLastestEndPoint()
    {
        StaticInfo.UpdateEndPoint();
        return StaticInfo.EndPoint;
    }

    //endregion

    //region 数据库基础设置

    /**
     * 数据库文件路径
     */
    public static String DataBasePath;

    /**
     * 数据库名称
     */
    public static final String DataBaseName = "SellingEasySQLite.db";

    /**
     * 数据库版本号
     */
    public static String DataBaseVersionCode;

    //endregion

    //region 存储设备路径配置

    /**
     * SD卡存储路径
     */
    protected static String sdCard_FilePath;

    /**
     * 获取SD卡存储路径<br/>
     * 输出不带斜杠 (例子: "/sdcard/雄冠安卓框架")
     *
     * @return
     */
    public static String GetSDCard_FilePath()
    {
        if (StaticInfo.sdCard_FilePath == null || StaticInfo.sdCard_FilePath.isEmpty())
        {
            File path = Environment.getExternalStorageDirectory();
            StaticInfo.sdCard_FilePath = path.toString() + "/" + StaticInfo.appName;
        }

        return StaticInfo.sdCard_FilePath;
    }

    /**
     * SD卡错误日志存储路径
     */
    protected static String sdCard_ErrorLogFilePath;

    /**
     * 获取SD卡错误日志存储路径<br/>
     * 输出不带斜杠 (例子: "/sdcard/雄冠安卓框架/ErrorLogs")
     *
     * @return
     */
    public static String GetSDCard_ErrorLogFilePath()
    {
        if (StaticInfo.sdCard_ErrorLogFilePath == null || StaticInfo.sdCard_ErrorLogFilePath.isEmpty())
        {
            StaticInfo.sdCard_ErrorLogFilePath = StaticInfo.GetSDCard_FilePath() + "/ErrorLogs";
        }
        return StaticInfo.sdCard_ErrorLogFilePath;
    }

    //endregion

    //region 安卓设备基本信息

    /**
     * IMEI<br/>
     * 非手机设备为空字符串
     */
    public static String IMEI;

    /**
     * IMEIName
     * <p>
     * 非手机设备为空字符串
     */
    public static String IMEIName;

    /**
     * 用户安卓系统版本号 例如 ( 4.1.1; 4.3 )
     */
    public static final String Android_Version_RELEASE = android.os.Build.VERSION.RELEASE;

    /**
     * 用户安卓系统版本编号 例如 16 (4.1.1)
     */
    public static final int Android_Version_SDK_INT = android.os.Build.VERSION.SDK_INT;

    //endregion

    //region SharedPreferences Key Code

    public static final boolean SaveAccount = true;

    public static final boolean SavePassword = true;

	/*
     * SharedPreferences -- 保存的数据主要是类似于配置信息格式的数据， 因此保存的数据主要是简单类型的键值对(key-value),它保存的是一个XML文件。
	 */

    /**
     * 操作SharePreferences的标识
     */
    public static final String ConfigTag = "CONFING_TAG";

    /**
     * SharedPreferences Key - IP
     */
    public static final String SharedPKey_IP = "SPKey_IP";

    /**
     * SharedPreferences Key - Port
     */
    public static final String SharedPKey_Port = "SPKey_Port";

    /**
     * SharedPreferences Key - 数据库版本
     */
    public static final String SharedPKey_DataBaseVersionKey = "SPKey_DataBaseVersion";

    /**
     * SharedPreferences Key - 账号
     */
    public static final String SharedPKey_Account = "SPKey_UserName";

    /**
     * SharedPreferences Key - 密码
     */
    public static final String SharedPKey_Password = "SPKey_Password";

    /**
     * 读取 IP 配置
     *
     * @param context
     */
    public static void LoadIPConfig(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences(StaticInfo.ConfigTag, 0);
        String spIP = sp.getString("IP", "");
        if (!spIP.isEmpty())
        {
            StaticInfo.IP = spIP;
        }

        String spPort = sp.getString("Port", "");
        if (!spPort.isEmpty())
        {
            StaticInfo.Port = spPort;
        }

        StaticInfo.UpdateEndPoint();
    }

    /**
     * 保存配置文件
     *
     * @param context
     * @param ip
     * @param port
     */
    public static void SaveIPConfig(Context context, String ip, String port)
    {
        SharedPreferences sp = context.getSharedPreferences(StaticInfo.ConfigTag, 0);
        StaticInfo.IP = ip;
        StaticInfo.Port = port;

        StaticInfo.UpdateEndPoint();

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(StaticInfo.IP, StaticInfo.IP);
        editor.putString(StaticInfo.Port, StaticInfo.Port);
        editor.commit();
    }

    protected static final String defaultValue = "";

    public static String GetSharedPreferencesValue(Context context, String key)
    {
        return context.getSharedPreferences(StaticInfo.ConfigTag, 0).getString(key, StaticInfo.defaultValue);
    }

    public static boolean SetSharedPreferencesKeyValue(Context context, String key, String value)
    {
        try
        {
            SharedPreferences.Editor editor = context.getSharedPreferences(StaticInfo.ConfigTag, 0).edit();
            editor.putString(key, value);
            editor.commit();
            return true;
        }
        catch (Exception ex)
        {
            Log.e(ex.toString(), ex.getMessage());
            return false;
        }
    }

    //endregion

    //region 播放声音

    private static HashMap<Integer, Integer> defaultSoundMap;
    private static SoundPool soundPool;

    public static void PlayBeepSound()
    {
        try
        {
            soundPool.play(defaultSoundMap.get(1), 1, 1, 0, 0, 1);
        }
        catch (Exception ex)
        {
            EnpotLog.e("Play Sound", ex);
        }
    }

    public static void PlayErrorSound()
    {
        try
        {
            soundPool.play(defaultSoundMap.get(2), 1, 1, 0, 0, 1);
        }
        catch (Exception ex)
        {
            EnpotLog.e("Play Sound", ex);
        }
    }

    //endregion

    //region 震动

    private static Vibrator vibrator;

    /**
     * 震动
     */
    private static long[] vibrator_pattern_success = {100, 400}; // 停止 开启 停止 开启
    private static long[] vibrator_pattern_error = {100, 400, 100, 400, 100, 400, 100, 400};

    public static void PlaySuccessVibrator()
    {
        vibrator.vibrate(StaticInfo.vibrator_pattern_success, -1);
    }

    public static void PlayErrorVibrator()
    {
        vibrator.vibrate(StaticInfo.vibrator_pattern_error, -1);
    }

    //endregion

    //region Java-UUID & C#-GUID

    public static String GetUUIDString()
    {
        return java.util.UUID.randomUUID().toString();
    }

    public static String GetGUIDString()
    {
        return StaticInfo.GetUUIDString();
    }

    // endregion

    //region Gson

    public static Gson GsonForWebService;

    static
    {
        if (GsonForWebService == null)
        {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); // 设置日期的格式，遇到这个格式的数据转为Date对象

            //region 定义 com.enpot.utils.DateTime 序列化&反序列化 适配器
            JsonSerializer<DateTime> myDateTimeSerializer = new JsonSerializer<DateTime>()
            {
                @Override
                public JsonPrimitive serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context)
                {
                    return new JsonPrimitive(src.toString());
                }
            };

            JsonDeserializer<DateTime> myDateTimeDeserializer = new JsonDeserializer<DateTime>()
            {
                @Override
                public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
                {
                    return new DateTime(json.getAsJsonPrimitive().getAsString());
                }
            };

            gsonBuilder.registerTypeAdapter(DateTime.class, myDateTimeSerializer);
            gsonBuilder.registerTypeAdapter(DateTime.class, myDateTimeDeserializer);

            //endregion 定义 com.enpot.utils.DateTime 序列化&反序列化 适配器

            GsonForWebService = gsonBuilder.create();
        }
    }

    //endregion

    //region QRCode Declare

    public static String ReadObj;

    public static String WriteObj;

    //endregion
}