package com.enpot.web;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.enpot.utils.EnpotLog;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import cn.com.enpot.enpot_location.commom.StaticInfo;
import cn.com.enpot.enpot_location.models.SOAPResult;


/**
 * Converter
 * <p>
 * <p>XML2JavaBean & JavaBean2XML Converter</p>
 *
 * @version 1.1
 * @LastestAuthor Howe ( ksoap )
 * @LastestAuthor Howe(JSON)
 * @LastestAuthor Yaka(Requests 添加对List<Class> 支持)
 * @LastestAuthor Howe(添加对List<Integer> 等基础类型支持)
 */
public class WebService
{
    
    public static String NAME_SPACE = "http://tempuri.org/";
    public static String URL = "";
    public static int SOAP_VR = 110; //SoapEnvelope.VER11; // Edit By Howe 移除了对 Ksoap包的依赖
    public static int TIME_OUT = 5 * 1000;
    
    /**
     * 连接WebService超时时间
     */
    public static int CONNECT_TIME_OUT = 5 * 1000;
    
    /**
     * 等待读取WebService数据超时时间
     * SQLServer Conn 连接默认超时时间为 20秒
     * 故设置30秒 可以接收到连接超时错误
     */
    public static int READ_TIME_OUT = 30 * 1000;
    
    public WebService(String url)
    {
        this.URL = url;
    }
    
    public WebService(String url, int soap_vr)
    {
        this.URL = url;
        this.SOAP_VR = soap_vr;
    }
    
    public <T> void Request(final Class<T> c, final String methodName, final CallbackHandler<T> handler, final WebServiceExceptionHandler exception)
    {
        this.Request(c, methodName, null, handler, exception);
    }
    
    public <T> void Request(final Class<T> c, final String methodName, final Map<String, Object> params
            , final CallbackHandler<T> handler, final WebServiceExceptionHandler exception)
    {
        AsyncTask<Void, Void, T> task = new AsyncTask<Void, Void, T>()
        {
            @Override
            protected T doInBackground(Void... p)
            {
                
                String soap = "";
                String response = "";
                try
                {
                    soap = Converter.ObjectToXML(WebService.NAME_SPACE, methodName, params);
                    
                    boolean b = false;
                    if (b)
                    {
                        soap = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:e=\"http://tempuri.org/\"><soapenv:Header /><soapenv:Body><e:ReceiptOrder_ScanCarton><e:tmp><e:CartonNo>12345678</e:CartonNo></e:tmp></e:ReceiptOrder_ScanCarton></soapenv:Body></soapenv:Envelope>";
                    }
                    // TODO Howe 如何处理这些 Java ==> C# 的XML标签转换
//region Java ==> C# 的XML标签转换
                    soap = soap.replaceAll("<e:String>", "<e:string>");
                    soap = soap.replaceAll("</e:String>", "</e:string>");
                    soap = soap.replaceAll("<e:String />", "<e:string />");
                    
                    soap = soap.replaceAll("<e:Boolean>", "<e:boolean>");
                    soap = soap.replaceAll("</e:Boolean>", "</e:boolean>");
                    soap = soap.replaceAll("<e:Boolean />", "<e:boolean />");
                    
                    soap = soap.replaceAll("<e:Integer>", "<e:int>");
                    soap = soap.replaceAll("</e:Integer>", "</e:int>");
                    soap = soap.replaceAll("<e:Integer />", "<e:int />");
                    
                    
                    soap = soap.replaceAll("<e:Long>", "<e:long>");
                    soap = soap.replaceAll("</e:Long>", "</e:long>");
                    soap = soap.replaceAll("<e:Long />", "<e:long />");
                    
                    
                    soap = soap.replaceAll("<e:Double>", "<e:double>");
                    soap = soap.replaceAll("</e:Double>", "</e:double>");
                    soap = soap.replaceAll("<e:Double />", "<e:double />");
                    
                    
                    soap = soap.replaceAll("<e:BigDecimal>", "<e:decimal>");
                    soap = soap.replaceAll("</e:BigDecimal>", "</e:decimal>");
                    soap = soap.replaceAll("<e:BigDecimal />", "<e:decimal />");
                    
                    soap = soap.replaceAll("<e:Date>", "<e:datetime>");
                    soap = soap.replaceAll("</e:Date>", "</e:datetime>");
                    soap = soap.replaceAll("<e:Date />", "<e:datetime />");
                    
                    soap = soap.replaceAll("<e:DateTime>", "<e:datetime>");
                    soap = soap.replaceAll("</e:DateTime>", "</e:datetime>");
                    soap = soap.replaceAll("<e:DateTime />", "<e:datetime />");

//endregion
                    
                    response = WebService.this.request(soap);
                    
                    if (StringUtils.contains(response, "System.Web.Services.Protocols.SoapException"))
                    {
                        String error = "传入参数到WebService有问题"; // TODO (请加上断点)传入参数到WebService有问题
                        throw new WebServiceException(null, methodName, soap, response);
                    }
                    
                    if (WebService.this.CheckError(response))
                    {
                        throw new WebServiceException(null, methodName, soap, response);
                    }
                    
                    // TODO Howe 如何处理这些 C# ==> JAVA 的XML标签转换
//region  C# ==> JAVA 的XML标签转换
                    if (response != null && response.equals("") == false)
                    {
                        response = response.replaceAll("<string>", "<String>");
                        response = response.replaceAll("</string>", "</String>");
                        response = response.replaceAll("<string />", "<String />");
                        
                        response = response.replaceAll("<boolean>", "<Boolean>");
                        response = response.replaceAll("</boolean>", "</Boolean>");
                        response = response.replaceAll("<boolean />", "<Boolean />");
                        
                        response = response.replaceAll("<int>", "<Integer>");
                        response = response.replaceAll("</int>", "</Integer>");
                        response = response.replaceAll("<int />", "<Integer />");
                        
                        
                        response = response.replaceAll("<long>", "<Long>");
                        response = response.replaceAll("</long>", "</Long>");
                        response = response.replaceAll("<long />", "<Long />");
                        
                        
                        response = response.replaceAll("<double>", "<Double>");
                        response = response.replaceAll("</double>", "</Double>");
                        response = response.replaceAll("<double />", "<Double />");
                        
                        
                        response = response.replaceAll("<decimal>", "<BigDecimal>");
                        response = response.replaceAll("</decimal>", "</BigDecimal>");
                        response = response.replaceAll("<decimal />", "<BigDecimal />");
                        
                        
                        response = response.replaceAll("<dateTime>", "<DateTime>");
                        response = response.replaceAll("</dateTime>", "</DateTime>");
                        response = response.replaceAll("<dateTime />", "<DateTime />");

//                        response = response.replaceAll("<datetime>", "<Date>");
//                        response = response.replaceAll("</datetime>", "</Date>");
//                        response = response.replaceAll("<datetime />", "<Date />");
                    }

//endregion
                    
                    if (c.getCanonicalName().endsWith("List"))
                    {
                        try
                        {
                            
                            TypeToken<T> token = new TypeToken<T>()
                            {
                            };
                            java.lang.reflect.Type temp = token.getType();
                            java.lang.reflect.Type[] types = ((java.lang.reflect.ParameterizedType) temp).getActualTypeArguments();
                            Class t = (Class<?>) types[0];
                            return (T) Converter.XMLToObjects(t, response);
                        }
                        catch (Exception e)
                        {
                            return (T) Converter.XMLToObject(c, response);
                        }
                    }
                    else
                    {
                        return (T) Converter.XMLToObject(c, response);
                    }
                    
                }
                catch (IOException | XmlPullParserException | IllegalArgumentException | IllegalStateException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException | NoSuchFieldException e)
                {
                    EnpotLog.e("WebService Request Err", e);
                    // exception.execute(new WebServiceException(e, methodName, soap, response));
                    WebServiceException we = new WebServiceException(e, methodName, soap, response);
                    exception.execute(we);
                }
                catch (WebServiceException e)
                {
                    EnpotLog.e("WebService Request Err", e);
                    exception.execute(e);
                }
                catch (Exception e)
                {
                    EnpotLog.e("WebService Request Err", e);
                    // exception.execute(new WebServiceException(e, methodName, soap, response));
                    WebServiceException we = new WebServiceException(e, methodName, soap, response);
                    exception.execute(we);
                }

//                T t;
//                try
//                {
//                    t = c.newInstance();
//                    return t;
//                }
//                catch (InstantiationException e)
//                {
//                    EnpotLog.e("WebService Request Err", e);
//                }
//                catch (IllegalAccessException e)
//                {
//                    EnpotLog.e("WebService Request Err", e);
//                }
                return null;
            }
            
            
            @SuppressWarnings("unused")
            @Override
            protected void onPostExecute(final T result)
            {

//				Class<?> classType = result.getClass().getSuperclass();
//				Object d =null;
//				if(result == null)
//				{
//					 try {
//						d = Class.forName(classType.getName()).newInstance();
//						handler.execute((T)d);
//					} catch (ExceptionInfo e) {
//
//					}
//
//					 return;
//				}
//
                handler.execute(result);
            }
        };
        
        task.execute();
    }
    
    public <T> void Requests(final Class<T> c, final String methodName, final CallbackHandler<List<T>> handler
            , final WebServiceExceptionHandler exception)
    {
        this.Requests(c, methodName, null, handler, exception);
    }
    
    public <T> void Requests(final Class<T> c, final String methodName, final Map<String, Object> params
            , final CallbackHandler<List<T>> handler, final WebServiceExceptionHandler exception)
    {
        AsyncTask<Void, Void, List<T>> task = new AsyncTask<Void, Void, List<T>>()
        {
            @Override
            protected List<T> doInBackground(Void... p)
            {
                
                String soap = "";
                String response = "";
                try
                {
                    
                    soap = Converter.ObjectToXML(WebService.NAME_SPACE, methodName, params);
                    
                    // TODO Howe 如何处理这些 Java ==> C# 的XML标签转换
//region Java ==> C# 的XML标签转换
                    soap = soap.replaceAll("<e:String>", "<e:string>");
                    soap = soap.replaceAll("</e:String>", "</e:string>");
                    soap = soap.replaceAll("<e:String />", "<e:string />");
                    
                    soap = soap.replaceAll("<e:Boolean>", "<e:boolean>");
                    soap = soap.replaceAll("</e:Boolean>", "</e:boolean>");
                    soap = soap.replaceAll("<e:Boolean />", "<e:boolean />");
                    
                    soap = soap.replaceAll("<e:Integer>", "<e:int>");
                    soap = soap.replaceAll("</e:Integer>", "</e:int>");
                    soap = soap.replaceAll("<e:Integer />", "<e:int />");
                    
                    
                    soap = soap.replaceAll("<e:Long>", "<e:long>");
                    soap = soap.replaceAll("</e:Long>", "</e:long>");
                    soap = soap.replaceAll("<e:Long />", "<e:long />");
                    
                    
                    soap = soap.replaceAll("<e:Double>", "<e:double>");
                    soap = soap.replaceAll("</e:Double>", "</e:double>");
                    soap = soap.replaceAll("<e:Double />", "<e:double />");
                    
                    
                    soap = soap.replaceAll("<e:BigDecimal>", "<e:decimal>");
                    soap = soap.replaceAll("</e:BigDecimal>", "</e:decimal>");
                    soap = soap.replaceAll("<e:BigDecimal />", "<e:decimal />");
                    
                    soap = soap.replaceAll("<e:Date>", "<e:datetime>");
                    soap = soap.replaceAll("</e:Date>", "</e:datetime>");
                    soap = soap.replaceAll("<e:Date />", "<e:datetime />");
                    
                    soap = soap.replaceAll("<e:DateTime>", "<e:datetime>");
                    soap = soap.replaceAll("</e:DateTime>", "</e:datetime>");
                    soap = soap.replaceAll("<e:DateTime />", "<e:datetime />");

//endregion
                    
                    response = WebService.this.request(soap);
                    
                    
                    // TODO Howe 如何处理这些 C# ==> JAVA 的XML标签转换
//region  C# ==> JAVA 的XML标签转换
                    if (response != null && response.equals("") == false)
                    {
                        response = response.replaceAll("<string>", "<String>");
                        response = response.replaceAll("</string>", "</String>");
                        response = response.replaceAll("<string />", "<String />");
                        
                        response = response.replaceAll("<boolean>", "<Boolean>");
                        response = response.replaceAll("</boolean>", "</Boolean>");
                        response = response.replaceAll("<boolean />", "<Boolean />");
                        
                        response = response.replaceAll("<int>", "<Integer>");
                        response = response.replaceAll("</int>", "</Integer>");
                        response = response.replaceAll("<int />", "<Integer />");
                        
                        
                        response = response.replaceAll("<long>", "<Long>");
                        response = response.replaceAll("</long>", "</Long>");
                        response = response.replaceAll("<long />", "<Long />");
                        
                        
                        response = response.replaceAll("<double>", "<Double>");
                        response = response.replaceAll("</double>", "</Double>");
                        response = response.replaceAll("<double />", "<Double />");
                        
                        
                        response = response.replaceAll("<decimal>", "<BigDecimal>");
                        response = response.replaceAll("</decimal>", "</BigDecimal>");
                        response = response.replaceAll("<decimal />", "<BigDecimal />");
                        
                        
                        response = response.replaceAll("<dateTime>", "<DateTime>");
                        response = response.replaceAll("</dateTime>", "</DateTime>");
                        response = response.replaceAll("<dateTime />", "<DateTime />");

//                        response = response.replaceAll("<datetime>", "<Date>");
//                        response = response.replaceAll("</datetime>", "</Date>");
//                        response = response.replaceAll("<datetime />", "<Date />");
                    }

//endregion
                    
                    if (WebService.this.CheckError(response))
                    {
                        throw new WebServiceException(null, methodName, soap, response);
                    }
                    
                    return (List<T>) Converter.XMLToObjects(c, response);
                    
                }
                catch (IOException | XmlPullParserException | IllegalArgumentException | IllegalStateException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException | NoSuchFieldException e)
                {
                    Log.i("execute 1", response);
                    exception.execute(new WebServiceException(e, methodName, soap, response));
                }
                catch (WebServiceException e)
                {
                    Log.i("execute 2", e.toString());
                    exception.execute(e);
                }
                return null;
                
            }
            
            ;
            
            @Override
            protected void onPostExecute(final List<T> result)
            {
                
                if (result != null)
                {
                    handler.execute(result);
                }
            }
        };
        
        task.execute();
    }
    
    private boolean CheckError(String xml) throws XmlPullParserException, IOException
    {
        int max = 5;
        int count = 0;
        ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes());
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(stream, "UTF-8");
        
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                {
                    if (count >= max)
                    {
                        return false;
                    }
                    if (parser.getName().endsWith("Fault"))
                    {
                        return true;
                    }
                    count++;
                }
            }
            eventType = parser.next();
        }
        return false;
    }
    
    private String request(String soap) throws IOException, WebServiceException
    {
        String result = null;
        byte[] data = soap.getBytes();
        URL url = new URL(WebService.URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        // conn.setConnectTimeout(WebService.TIME_OUT); // Edit By Howe
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("Content-Length", String.valueOf(data.length));
        conn.setDoOutput(true);
        // conn.setConnectTimeout(10000);    // 设置请求时间10秒
        conn.setConnectTimeout(CONNECT_TIME_OUT);    // 设置请求时间5秒 // Edit By Howe
        conn.setReadTimeout(READ_TIME_OUT);       // 设置读取时间30秒 // Edit By Howe
        
        conn.setChunkedStreamingMode(0);  // 设置无重复请求
        OutputStream outStream = conn.getOutputStream();
        outStream.write(data);
        outStream.flush();
        outStream.close();

        int code = conn.getResponseCode();
        
        InputStream is = null;
        
        if (code == 200)
        {
            is = conn.getInputStream();
        }
        else
        {
            is = conn.getErrorStream();
        }
        
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = in.readLine()) != null)
        {
            buffer.append(line);
        }
        result = buffer.toString();
        
        return result;
    }
    
    
    //region Howe Json
    
    public <T> void RequestJson(final Class<T> c, final String methodName, final Map<String, Object> params
            , final CallbackHandler<T> handler)
    {
        AsyncTask<Void, Void, T> task = new AsyncTask<Void, Void, T>()
        {
            /**
             * 1、准备运行：onPreExecute(),该回调方法在任务被执行之后立即由UI线程调用。这个步骤通常用来建立任务，在UI上显示进度条。
             */
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
            }
            
            /**
             * 2、正在后台运行：doInBackground(Params...),该回调方法由后台线程在onPreExecute()方法执行结束后立即调用。
             * 通常在这里执行耗时的后台计算，计算的结果必须由该方法返回，并被传递到onPostExecute()中。
             * 在该方法内也可使用publishProgress(Progress...)来发布一个或多个进度单位(units of progress)，
             * 这些值将会在onProgressUpdate(Progress...)中被发布到UI线程。
             * @param p
             * @return
             */
            @Override
            protected T doInBackground(Void... p)
            {
                
                String soap = "";
                String response = "";
                String soapResultJsonTemplate = "{\"IsComplete\":false,\"ExceptionInfo\":\"%s\",\"IsSuccess\":false,\"BusinessExceptionInfo\":\"\"}";
                
                try
                {
                    
                    soap = Converter.ObjectToXML(WebService.NAME_SPACE, methodName, params);
                    //region Java ==> C# 的XML标签转换
                    soap = soap.replaceAll("<e:String>", "<e:string>");
                    soap = soap.replaceAll("</e:String>", "</e:string>");
                    soap = soap.replaceAll("<e:String />", "<e:string />");
                    
                    soap = soap.replaceAll("<e:Boolean>", "<e:boolean>");
                    soap = soap.replaceAll("</e:Boolean>", "</e:boolean>");
                    soap = soap.replaceAll("<e:Boolean />", "<e:boolean />");
                    
                    soap = soap.replaceAll("<e:Integer>", "<e:int>");
                    soap = soap.replaceAll("</e:Integer>", "</e:int>");
                    soap = soap.replaceAll("<e:Integer />", "<e:int />");
                    
                    
                    soap = soap.replaceAll("<e:Long>", "<e:long>");
                    soap = soap.replaceAll("</e:Long>", "</e:long>");
                    soap = soap.replaceAll("<e:Long />", "<e:long />");
                    
                    
                    soap = soap.replaceAll("<e:Double>", "<e:double>");
                    soap = soap.replaceAll("</e:Double>", "</e:double>");
                    soap = soap.replaceAll("<e:Double />", "<e:double />");
                    
                    
                    soap = soap.replaceAll("<e:BigDecimal>", "<e:decimal>");
                    soap = soap.replaceAll("</e:BigDecimal>", "</e:decimal>");
                    soap = soap.replaceAll("<e:BigDecimal />", "<e:decimal />");
                    
                    soap = soap.replaceAll("<e:Date>", "<e:datetime>");
                    soap = soap.replaceAll("</e:Date>", "</e:datetime>");
                    soap = soap.replaceAll("<e:Date />", "<e:datetime />");
                    
                    soap = soap.replaceAll("<e:DateTime>", "<e:datetime>");
                    soap = soap.replaceAll("</e:DateTime>", "</e:datetime>");
                    soap = soap.replaceAll("<e:DateTime />", "<e:datetime />");

//endregion
                    response = WebService.this.request(soap);
                    //region  C# ==> JAVA 的XML标签转换
                    if (response != null && response.equals("") == false)
                    {
                        response = response.replaceAll("<string>", "<String>");
                        response = response.replaceAll("</string>", "</String>");
                        response = response.replaceAll("<string />", "<String />");
                        
                        response = response.replaceAll("<boolean>", "<Boolean>");
                        response = response.replaceAll("</boolean>", "</Boolean>");
                        response = response.replaceAll("<boolean />", "<Boolean />");
                        
                        response = response.replaceAll("<int>", "<Integer>");
                        response = response.replaceAll("</int>", "</Integer>");
                        response = response.replaceAll("<int />", "<Integer />");
                        
                        
                        response = response.replaceAll("<long>", "<Long>");
                        response = response.replaceAll("</long>", "</Long>");
                        response = response.replaceAll("<long />", "<Long />");
                        
                        
                        response = response.replaceAll("<double>", "<Double>");
                        response = response.replaceAll("</double>", "</Double>");
                        response = response.replaceAll("<double />", "<Double />");
                        
                        
                        response = response.replaceAll("<decimal>", "<BigDecimal>");
                        response = response.replaceAll("</decimal>", "</BigDecimal>");
                        response = response.replaceAll("<decimal />", "<BigDecimal />");
                        
                        
                        response = response.replaceAll("<dateTime>", "<DateTime>");
                        response = response.replaceAll("</dateTime>", "</DateTime>");
                        response = response.replaceAll("<dateTime />", "<DateTime />");

//                        response = response.replaceAll("<datetime>", "<Date>");
//                        response = response.replaceAll("</datetime>", "</Date>");
//                        response = response.replaceAll("<datetime />", "<Date />");
                    }

//endregion
                    
                    if (StringUtils.contains(response, "System.Web.Services.Protocols.SoapException"))
                    {
                        String error = "传入参数到WebService有问题"; // TODO (请加上断点)传入参数到WebService有问题
                    }
                    
                    if (WebService.this.CheckError(response))
                    {
                        return (T) Converter.XMLToObject(c, response);
                    }
                    
                    if (c.getCanonicalName().endsWith("List"))
                    {
                        try
                        {
                            
                            TypeToken<T> token = new TypeToken<T>()
                            {
                            };
                            java.lang.reflect.Type temp = token.getType();
                            java.lang.reflect.Type[] types = ((java.lang.reflect.ParameterizedType) temp).getActualTypeArguments();
                            Class t = (Class<?>) types[0];
                            return (T) Converter.XMLToObjects(t, response);
                        }
                        catch (Exception e)
                        {
                            return (T) Converter.XMLToObject(c, response);
                        }
                    }
                    else
                    {
                        return (T) Converter.XMLToObject(c, response);
                    }
                    
                }
                // *****************
                // 1. 首先要定位是否超时问题 由于 SocketTimeoutException 是 IOException的孙子, 不提前会被 IOException catch掉
                // java.io.IOException
                // -- java.io.InterruptedIOException
                // -- -- java.net.SocketTimeoutException
                catch (java.net.SocketTimeoutException timeOutEx)
                {
//                    SOAPResult soapResult = new SOAPResult();
//                    soapResult.IsComplete = false;
//                    soapResult.ExceptionInfo = String.format("WebService 连接超时; 连接地址 : %s", WebService.this.URL);
//                    soapResult.IsSuccess = false;
                    
                    String soapException = String.format("WebService连接超时。\r\n连接地址：%s", WebService.this.URL);
                    String errEx = String.format(soapResultJsonTemplate, soapException);
                    
                    return (T) errEx;
                }
                // *****************
                
                catch (IOException | XmlPullParserException | IllegalArgumentException | IllegalStateException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException | NoSuchFieldException e)
                {
                    String soapException = e.getMessage() + "/r/n" + e.getStackTrace();
                    String errEx = String.format(soapResultJsonTemplate, soapException);
                    return (T) errEx;
                }
                catch (WebServiceException e)
                {
                    String soapException = e.getMessage() + "/r/n" + e.getStackTrace();
                    String errEx = String.format(soapResultJsonTemplate, soapException);
                    return (T) errEx;
                }
                catch (Exception e)
                {
                    String soapException = e.getMessage() + "/r/n" + e.getStackTrace();
                    String errEx = String.format(soapResultJsonTemplate, soapException);
                    return (T) errEx;
                }
            }
            
            /**
             * 3. 进度更新：onProgressUpdate(Progress...),该方法由UI线程在publishProgress(Progress...)方法调用完后被调用，一般用于动态地显示一个进度条。
             * @param values
             */
            @Override
            protected void onProgressUpdate(Void... values)
            {
                super.onProgressUpdate(values);
            }
            
            /**
             * 4. 完成后台任务：onPostExecute(Result),当后台计算结束后调用。后台计算的结果会被作为参数传递给该方法。
             * @param result
             */
            @SuppressWarnings("unused")
            @Override
            protected void onPostExecute(final T result)
            {
                handler.execute(result);
            }
            
            /**
             * 5、取消任务：onCancelled ()，在调用AsyncTask的cancel()方法时调用
             * @param t
             */
            @Override
            protected void onCancelled(T t)
            {
                super.onCancelled(t);
            }
            
        };
        
        task.execute();
    }
    
    public <T> void RequestJsonBakV1(final Class<T> c, final String methodName, final Map<String, Object> params
            , final CallbackHandler<T> handler, final WebServiceExceptionHandler errCallBack)
    {
        AsyncTask<Void, Void, T> task = new AsyncTask<Void, Void, T>()
        {
            /**
             * 1、准备运行：onPreExecute(),该回调方法在任务被执行之后立即由UI线程调用。这个步骤通常用来建立任务，在UI上显示进度条。
             */
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
            }
            
            /**
             * 2、正在后台运行：doInBackground(Params...),该回调方法由后台线程在onPreExecute()方法执行结束后立即调用。
             * 通常在这里执行耗时的后台计算，计算的结果必须由该方法返回，并被传递到onPostExecute()中。
             * 在该方法内也可使用publishProgress(Progress...)来发布一个或多个进度单位(units of progress)，
             * 这些值将会在onProgressUpdate(Progress...)中被发布到UI线程。
             * @param p
             * @return
             */
            @Override
            protected T doInBackground(Void... p)
            {
                
                String soap = "";
                String response = "";
                try
                {
                    
                    soap = Converter.ObjectToXML(WebService.NAME_SPACE, methodName, params);
                    //region Java ==> C# 的XML标签转换
                    soap = soap.replaceAll("<e:String>", "<e:string>");
                    soap = soap.replaceAll("</e:String>", "</e:string>");
                    soap = soap.replaceAll("<e:String />", "<e:string />");
                    
                    soap = soap.replaceAll("<e:Boolean>", "<e:boolean>");
                    soap = soap.replaceAll("</e:Boolean>", "</e:boolean>");
                    soap = soap.replaceAll("<e:Boolean />", "<e:boolean />");
                    
                    soap = soap.replaceAll("<e:Integer>", "<e:int>");
                    soap = soap.replaceAll("</e:Integer>", "</e:int>");
                    soap = soap.replaceAll("<e:Integer />", "<e:int />");
                    
                    
                    soap = soap.replaceAll("<e:Long>", "<e:long>");
                    soap = soap.replaceAll("</e:Long>", "</e:long>");
                    soap = soap.replaceAll("<e:Long />", "<e:long />");
                    
                    
                    soap = soap.replaceAll("<e:Double>", "<e:double>");
                    soap = soap.replaceAll("</e:Double>", "</e:double>");
                    soap = soap.replaceAll("<e:Double />", "<e:double />");
                    
                    
                    soap = soap.replaceAll("<e:BigDecimal>", "<e:decimal>");
                    soap = soap.replaceAll("</e:BigDecimal>", "</e:decimal>");
                    soap = soap.replaceAll("<e:BigDecimal />", "<e:decimal />");
                    
                    soap = soap.replaceAll("<e:Date>", "<e:datetime>");
                    soap = soap.replaceAll("</e:Date>", "</e:datetime>");
                    soap = soap.replaceAll("<e:Date />", "<e:datetime />");
                    
                    soap = soap.replaceAll("<e:DateTime>", "<e:datetime>");
                    soap = soap.replaceAll("</e:DateTime>", "</e:datetime>");
                    soap = soap.replaceAll("<e:DateTime />", "<e:datetime />");

//endregion
                    response = WebService.this.request(soap);
                    //region  C# ==> JAVA 的XML标签转换
                    if (response != null && response.equals("") == false)
                    {
                        response = response.replaceAll("<string>", "<String>");
                        response = response.replaceAll("</string>", "</String>");
                        response = response.replaceAll("<string />", "<String />");
                        
                        response = response.replaceAll("<boolean>", "<Boolean>");
                        response = response.replaceAll("</boolean>", "</Boolean>");
                        response = response.replaceAll("<boolean />", "<Boolean />");
                        
                        response = response.replaceAll("<int>", "<Integer>");
                        response = response.replaceAll("</int>", "</Integer>");
                        response = response.replaceAll("<int />", "<Integer />");
                        
                        
                        response = response.replaceAll("<long>", "<Long>");
                        response = response.replaceAll("</long>", "</Long>");
                        response = response.replaceAll("<long />", "<Long />");
                        
                        
                        response = response.replaceAll("<double>", "<Double>");
                        response = response.replaceAll("</double>", "</Double>");
                        response = response.replaceAll("<double />", "<Double />");
                        
                        
                        response = response.replaceAll("<decimal>", "<BigDecimal>");
                        response = response.replaceAll("</decimal>", "</BigDecimal>");
                        response = response.replaceAll("<decimal />", "<BigDecimal />");
                        
                        
                        response = response.replaceAll("<dateTime>", "<DateTime>");
                        response = response.replaceAll("</dateTime>", "</DateTime>");
                        response = response.replaceAll("<dateTime />", "<DateTime />");

//                        response = response.replaceAll("<datetime>", "<Date>");
//                        response = response.replaceAll("</datetime>", "</Date>");
//                        response = response.replaceAll("<datetime />", "<Date />");
                    }

//endregion
                    
                    if (StringUtils.contains(response, "System.Web.Services.Protocols.SoapException"))
                    {
                        String error = "传入参数到WebService有问题"; // TODO (请加上断点)传入参数到WebService有问题
                    }
                    
                    if (WebService.this.CheckError(response))
                    {
                        return (T) Converter.XMLToObject(c, response);
                    }
                    
                    if (c.getCanonicalName().endsWith("List"))
                    {
                        try
                        {
                            
                            TypeToken<T> token = new TypeToken<T>()
                            {
                            };
                            java.lang.reflect.Type temp = token.getType();
                            java.lang.reflect.Type[] types = ((java.lang.reflect.ParameterizedType) temp).getActualTypeArguments();
                            Class t = (Class<?>) types[0];
                            return (T) Converter.XMLToObjects(t, response);
                        }
                        catch (Exception e)
                        {
                            return (T) Converter.XMLToObject(c, response);
                        }
                    }
                    else
                    {
                        return (T) Converter.XMLToObject(c, response);
                    }
                    
                }
                // *****************
                // 1. 首先要定位是否超时问题 由于 SocketTimeoutException 是 IOException的孙子, 不提前会被 IOException catch掉
                // java.io.IOException
                // -- java.io.InterruptedIOException
                // -- -- java.net.SocketTimeoutException
                catch (java.net.SocketTimeoutException timeOutEx)
                {
                    errCallBack.execute(new WebServiceException(timeOutEx, methodName, soap, response));
                }
                // *****************
                
                catch (IOException | XmlPullParserException | IllegalArgumentException | IllegalStateException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException | NoSuchFieldException e)
                {
                    errCallBack.execute(new WebServiceException(e, methodName, soap, response));
                }
                catch (WebServiceException e)
                {
                    errCallBack.execute(e);
                }
                catch (Exception e)
                {
                    errCallBack.execute(new WebServiceException(e, methodName, soap, response));
                }
                
                return null;
            }
            
            /**
             * 3. 进度更新：onProgressUpdate(Progress...),该方法由UI线程在publishProgress(Progress...)方法调用完后被调用，一般用于动态地显示一个进度条。
             * @param values
             */
            @Override
            protected void onProgressUpdate(Void... values)
            {
                super.onProgressUpdate(values);
            }
            
            /**
             * 4. 完成后台任务：onPostExecute(Result),当后台计算结束后调用。后台计算的结果会被作为参数传递给该方法。
             * @param result
             */
            @SuppressWarnings("unused")
            @Override
            protected void onPostExecute(final T result)
            {

//				Class<?> classType = result.getClass().getSuperclass();
//				Object d =null;
//				if(result == null)
//				{
//					 try {
//						d = Class.forName(classType.getName()).newInstance();
//						handler.execute((T)d);
//					} catch (ExceptionInfo e) {
//
//					}
//
//					 return;
//				}
//
                handler.execute(result);
            }
            
            /**
             * 5、取消任务：onCancelled ()，在调用AsyncTask的cancel()方法时调用
             * @param t
             */
            @Override
            protected void onCancelled(T t)
            {
                super.onCancelled(t);
            }
            
        };
        
        task.execute();
    }
    
    //endregion Howe Json
    
    //region Howe Ksoap2
    
//    public void RequestKSoap(String methodName, List<String> args, final CallbackHandler<String> handler, String... endPointUrl)
//{
//    String NAMESPACE = "http://tempuri.org/"; // TODO 放在 StaticInfo中
//    String METHODNAME = "ExecuteWebServiceMethodV3"; // TODO 放在 StaticInfo 中
//
//    SoapObject soapObject = new SoapObject(NAMESPACE, METHODNAME);
//
//    PropertyInfo arg1 = new PropertyInfo();
//    arg1.setName("methodName");
//    arg1.setValue(methodName);
//    arg1.setType(String.class);
//    soapObject.addProperty(arg1);
//
//    PropertyInfo arg2 = new PropertyInfo();
//    arg2.setName("jsonArgs");
//    arg2.setValue(new KvmStringList(NAMESPACE, args));
//    arg2.setType(KvmStringList.class);
//    soapObject.addProperty(arg2);
//
//    AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>()
//    {
//        /**
//         * 1、准备运行：onPreExecute(),该回调方法在任务被执行之后立即由UI线程调用。这个步骤通常用来建立任务，在UI上显示进度条。
//         */
//        @Override
//        protected void onPreExecute()
//        {
//            super.onPreExecute();
//        }
//
//        /**
//         * 2、正在后台运行：doInBackground(Params...),该回调方法由后台线程在onPreExecute()方法执行结束后立即调用。
//         * 通常在这里执行耗时的后台计算，计算的结果必须由该方法返回，并被传递到onPostExecute()中。
//         * 在该方法内也可使用publishProgress(Progress...)来发布一个或多个进度单位(units of progress)，
//         * 这些值将会在onProgressUpdate(Progress...)中被发布到UI线程。
//         * @param p
//         * @return
//         */
//        @Override
//        protected String doInBackground(Void... p)
//        {
//            HttpTransportSE httpTransportSE = null;
//            if (endPointUrl.length > 0)
//            {
//                httpTransportSE = new HttpTransportSE(endPointUrl[0], 30000); // TODO 设置超时
//            }
//            else
//            {
//                httpTransportSE = new HttpTransportSE(StaticInfo.UpdateEndPoint(), 30000); // TODO 设置超时
//            }
//
//            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER10); // TODO 放在 StaticInfo中
//
//            soapEnvelope.setOutputSoapObject(soapObject);
//            soapEnvelope.dotNet = true;
//            httpTransportSE.debug = true;
//
//            SoapObject resultSoapObject = null;
//            try
//            {
//                httpTransportSE.call(NAMESPACE + METHODNAME, soapEnvelope);
//                if (soapEnvelope.getResponse() != null)
//                {
//                    resultSoapObject = (SoapObject) soapEnvelope.bodyIn;
//                    return resultSoapObject.getProperty(0).toString();
//                }
//                else
//                {
//
//                }
//            }
//            catch (SocketTimeoutException ste)
//            {
//                String response = null;
//                if (resultSoapObject != null)
//                {
//                    response = resultSoapObject.toString();
//                }
//                SOAPResult err = new SOAPResult();
//                err.IsComplete = false;
//                err.ExceptionInfo = "网络连接超时。";
//                err.BusinessExceptionInfo = response;
//
//                return StaticInfo.GsonForWebService.toJson(err);
//            }
//            catch (Exception e)
//            {
//                String response = null;
//                if (resultSoapObject != null)
//                {
//                    response = resultSoapObject.toString();
//                }
//                SOAPResult err = new SOAPResult();
//                err.IsComplete = false;
//                err.ExceptionInfo = e.getMessage() + "\r\n" + e.getStackTrace();
//                err.BusinessExceptionInfo = response;
//
//                return StaticInfo.GsonForWebService.toJson(err);
//            }
//
//            return null;
//        }
//
//        /**
//         * 3. 进度更新：onProgressUpdate(Progress...),该方法由UI线程在publishProgress(Progress...)方法调用完后被调用，一般用于动态地显示一个进度条。
//         * @param values
//         */
//        @Override
//        protected void onProgressUpdate(Void... values)
//        {
//            super.onProgressUpdate(values);
//        }
//
//        /**
//         * 4. 完成后台任务：onPostExecute(Result),当后台计算结束后调用。后台计算的结果会被作为参数传递给该方法。
//         * @param result
//         */
//        @SuppressWarnings("unused")
//        @Override
//        // protected void onPostExecute(final SoapObject result)
//        protected void onPostExecute(final String result)
//        {
//            if (result == null)
//            {
//                handler.execute("");
//            }
//            else
//            {
//                handler.execute(result);
//            }
//        }
//
//        /**
//         * 5、取消任务：onCancelled ()，在调用AsyncTask的cancel()方法时调用
//         * @param t
//         */
//        @Override
//        protected void onCancelled(String t)
//        {
//            super.onCancelled(t);
//        }
//
//    };
//
//    task.execute();
//
//
//}
    
    //endregion
}
