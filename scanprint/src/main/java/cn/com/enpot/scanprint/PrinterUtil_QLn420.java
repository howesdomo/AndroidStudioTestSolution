package cn.com.enpot.scanprint;

import android.os.AsyncTask;

import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Howe on 2017/8/21.
 */

public class PrinterUtil_QLn420
{
    public static final String BasePath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    
    public static final String SendOutCartonPath = "/PrintTemplate.dll";
    
    private Connection mConnection = null;
    private ZebraPrinter mPrinter = null;
    
    public Observable<Boolean> Print(final String left, final String top, final String content, final String qty)
    {
        final String templateZPL = StaticInfo.PrintZPLTemplate;
        return Observable.create(new Observable.OnSubscribe<Boolean>()
        {
            @Override
            public void call(Subscriber<? super Boolean> subscriber)
            {
                if (subscriber.isUnsubscribed() == false)
                {
                    try
                    {
                        String path = "";
                        if (mConnection == null)
                        {
                            mConnection = new BluetoothConnection(StaticInfo.BluetoothMACAddress);
                        }
                        
                        mConnection.open();
                        mPrinter = ZebraPrinterFactory.getInstance(mConnection);
                        
                        path = BasePath + SendOutCartonPath;
                        
                        String zpl = MessageFormat.format(templateZPL, left, top, content, qty);
                        createFile(mPrinter, path, zpl);
                        File f3 = new File(path);
                        mPrinter.sendFileContents(f3.getAbsolutePath());
                        
                        mConnection.close();
                        
                        subscriber.onNext(true);
                        subscriber.onCompleted();
                        
                    }
                    catch (Exception ex)
                    {
                        String errMsg = ex.getMessage();
                        if (errMsg.equals("Could not connect to device: Bluetooth is off"))
                        {
                            errMsg = "无法连接蓝牙打印机：PDA未开启蓝牙。";
                        }
                        else if (errMsg.equals("Could not connect to device: read failed, socket might closed or timeout, read ret: -1"))
                        {
                            errMsg = "无法连接蓝牙打印机：检查蓝牙打印机电源。";
                        }
                        
                        errMsg = errMsg + "\r\n确认重试？";
                        
                        subscriber.onError(new Exception(errMsg));
                    }
                }
            }
        });
        
    }
    
    //    public static final String BasePath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
//
//    public static final String PrintZPLTemplatePath = "/PrintZPLTemplate.dll";
//
//    public static final String RemainCartonPath = "/RemainCarton.dll";
//
//    public static final String SendOutCartonPath = "/SendOutCarton.dll";
//
//    private Connection mConnection = null;
//    private ZebraPrinter mPrinter = null;
//
//    /**
//     * 打印箱码
//     *
//     * @param templateZPL
//     * @param remainCartonZPL
//     * @param sendOutCartonZPL
//     * @throws Exception
//     */
//    public void SendFileAsync(String templateZPL, String remainCartonZPL, String sendOutCartonZPL, CallbackHandler<String> handler)
//    {
//        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>()
//        {
//            /**
//             * 1、准备运行：onPreExecute(),该回调方法在任务被执行之后立即由UI线程调用。这个步骤通常用来建立任务，在UI上显示进度条。
//             */
//            @Override
//            protected void onPreExecute()
//            {
//                super.onPreExecute();
//            }
//
//            /**
//             * 2、正在后台运行：doInBackground(Params...),该回调方法由后台线程在onPreExecute()方法执行结束后立即调用。
//             * 通常在这里执行耗时的后台计算，计算的结果必须由该方法返回，并被传递到onPostExecute()中。
//             * 在该方法内也可使用publishProgress(Progress...)来发布一个或多个进度单位(units of progress)，
//             * 这些值将会在onProgressUpdate(Progress...)中被发布到UI线程。
//             * @param p
//             * @return
//             */
//            @Override
//            protected String doInBackground(Void... p)
//            {
//                String errMsg = "";
//                try
//                {
//
//                    String path = "";
//                    if (mConnection == null)
//                    {
//                        mConnection = new BluetoothConnection(StaticInfo.BluetoothMACAddress);
//                        // mConnection = new TcpConnection("192.168.1.168", 6101); // 备用端口 9100
//                    }
//
//                    if (mConnection.isConnected() == false) // 重启打印机后仍然为 true, 用这个来判断并不安全
//                    {
//                        mConnection.open();
//                        mPrinter = ZebraPrinterFactory.getInstance(mConnection);
//
//
//                        // 1
//                        path = BasePath + PrintZPLTemplatePath;
//                        createFile(mPrinter, path, templateZPL);
//                        File f = new File(path);
//                        mPrinter.sendFileContents(f.getAbsolutePath());
//                    }
//                    else
//                    {
//                        try
//                        {
//                            PrinterStatus status = mPrinter.getCurrentStatus();
//                        }
//                        catch (Exception ex)
//                        {
//                            mConnection = new BluetoothConnection(StaticInfo.BluetoothMACAddress);
//                            mConnection.open();
//                            mPrinter = ZebraPrinterFactory.getInstance(mConnection);
//
//
//                            // 1
//                            path = BasePath + PrintZPLTemplatePath;
//                            createFile(mPrinter, path, templateZPL);
//                            File f = new File(path);
//                            mPrinter.sendFileContents(f.getAbsolutePath());
//                        }
//                    }
//
//                    // 2
//                    if (StringUtils.isNotBlank(remainCartonZPL)) // 尾数箱不为空
//                    {
//                        path = BasePath + RemainCartonPath;
//                        createFile(mPrinter, path, remainCartonZPL);
//                        File f2 = new File(path);
//                        mPrinter.sendFileContents(f2.getAbsolutePath());
//                    }
//
//                    // 3
//                    path = BasePath + SendOutCartonPath;
//                    createFile(mPrinter, path, sendOutCartonZPL);
//                    File f3 = new File(path);
//                    mPrinter.sendFileContents(f3.getAbsolutePath());
//
//                    // mConnection.close(); // TODO 测试时为了两台设备可使用同台打印机
//                }
//                catch (Exception ex)
//                {
//                    errMsg = ex.getMessage();
//                    if (errMsg.equals("Could not connect to device: Bluetooth is off"))
//                    {
//                        errMsg = "无法连接蓝牙打印机：PDA未开启蓝牙。";
//                    }
//                    else if (errMsg.equals("Could not connect to device: read failed, socket might closed or timeout, read ret: -1"))
//                    {
//                        errMsg = "无法连接蓝牙打印机：检查蓝牙打印机电源。";
//                    }
//
//                    errMsg = errMsg + "\r\n确认重试？";
//                }
//
//                return errMsg;
//            }
//
//            /**
//             * 3. 进度更新：onProgressUpdate(Progress...),该方法由UI线程在publishProgress(Progress...)方法调用完后被调用，一般用于动态地显示一个进度条。
//             * @param values
//             */
//            @Override
//            protected void onProgressUpdate(Void... values)
//            {
//                super.onProgressUpdate(values);
//            }
//
//            /**
//             * 4. 完成后台任务：onPostExecute(Result),当后台计算结束后调用。后台计算的结果会被作为参数传递给该方法。
//             * @param result
//             */
//            @SuppressWarnings("unused")
//            @Override
//            protected void onPostExecute(final String result)
//            {
//                handler.execute(result);
//            }
//
//            /**
//             * 5、取消任务：onCancelled ()，在调用AsyncTask的cancel()方法时调用
//             * @param t
//             */
//            @Override
//            protected void onCancelled(String t)
//            {
//                super.onCancelled(t);
//            }
//
//        };
//
//        task.execute();
//    }
//
//    public void closeConnection() throws Exception
//    {
//        if (mConnection != null)
//        {
//            if (mConnection.isConnected() == true)
//            {
//                mConnection.close();
//            }
//        }
//    }
//
//    public void TestPrinterAsync(String mac, String templateZPL, Integer left, Integer top, CallbackHandler<String> handler)
//    {
//        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>()
//        {
//            /**
//             * 1、准备运行：onPreExecute(),该回调方法在任务被执行之后立即由UI线程调用。这个步骤通常用来建立任务，在UI上显示进度条。
//             */
//            @Override
//            protected void onPreExecute()
//            {
//                super.onPreExecute();
//            }
//
//            /**
//             * 2、正在后台运行：doInBackground(Params...),该回调方法由后台线程在onPreExecute()方法执行结束后立即调用。
//             * 通常在这里执行耗时的后台计算，计算的结果必须由该方法返回，并被传递到onPostExecute()中。
//             * 在该方法内也可使用publishProgress(Progress...)来发布一个或多个进度单位(units of progress)，
//             * 这些值将会在onProgressUpdate(Progress...)中被发布到UI线程。
//             * @param p
//             * @return
//             */
//            @Override
//            protected String doInBackground(Void... p)
//            {
//                String errMsg = "";
//                try
//                {
//                    Connection connection = null;
//                    connection = new BluetoothConnection(mac);
//                    connection.open();
//                    ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
//
//                    // 1
//                    String path = BasePath + PrintZPLTemplatePath;
//                    createFile(printer, path, String.format(templateZPL, String.valueOf(left), String.valueOf(top)));
//                    File f = new File(path);
//                    printer.sendFileContents(f.getAbsolutePath());
//
//                    connection.close();
//
//                }
//                catch (Exception ex)
//                {
//                    errMsg = ex.getMessage();
//                    if (errMsg.equals("Could not connect to device: Bluetooth is off"))
//                    {
//                        errMsg = "无法连接蓝牙打印机：PDA未开启蓝牙。";
//                    }
//                    else if (errMsg.equals("Could not connect to device: read failed, socket might closed or timeout, read ret: -1"))
//                    {
//                        errMsg = "无法连接蓝牙打印机：检查蓝牙打印机电源。";
//                    }
//
//                    errMsg = errMsg + "\r\n确认重试？";
//                }
//
//                return errMsg;
//            }
//
//            /**
//             * 3. 进度更新：onProgressUpdate(Progress...),该方法由UI线程在publishProgress(Progress...)方法调用完后被调用，一般用于动态地显示一个进度条。
//             * @param values
//             */
//            @Override
//            protected void onProgressUpdate(Void... values)
//            {
//                super.onProgressUpdate(values);
//            }
//
//            /**
//             * 4. 完成后台任务：onPostExecute(Result),当后台计算结束后调用。后台计算的结果会被作为参数传递给该方法。
//             * @param result
//             */
//            @SuppressWarnings("unused")
//            @Override
//            protected void onPostExecute(final String result)
//            {
//                handler.execute(result);
//            }
//
//            /**
//             * 5、取消任务：onCancelled ()，在调用AsyncTask的cancel()方法时调用
//             * @param t
//             */
//            @Override
//            protected void onCancelled(String t)
//            {
//                super.onCancelled(t);
//            }
//
//        };
//
//        task.execute();
//    }
//
    private void createFile(ZebraPrinter printer, String filePath, String content) throws IOException
    {
        FileOutputStream os = new FileOutputStream(filePath, false);
        os.write(content.getBytes());
        os.flush();
        os.close();
    }
    
}
