package com.enpot.web;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.enpot.utils.EnpotLog;

import cn.com.enpot.enpot_location.commom.StaticInfo;


public class WebServiceException extends Exception
{

    private String _message;
    private String _requestXML;
    private String _methodName;
    private String _code;
    private Exception _exception;

    public WebServiceException(Exception exception, String methodName, String requestXML, String responseXML)
    {

        super("WebServiceException");

        this._requestXML = requestXML;
        this._methodName = methodName;
        this._exception = exception;

        try
        {
            Fault fault = (Fault) Converter.XMLToObject(Fault.class, responseXML);
            if (fault != null)
            {
                this._message = fault.faultstring == null ? "" : fault.faultstring;
            }
            else if (this._exception != null)
            {
                this._message = this._exception.getMessage();
                if(StringUtils.isBlank(this._message) && this._exception.getClass().getName().equals("java.net.SocketTimeoutException"))
                {
                    this._message = "WebService 连接超时; 连接地址 : " + StaticInfo.GetLastestEndPoint();
                }
            }
        }
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchFieldException | ClassNotFoundException | XmlPullParserException
                | IOException e)
        {
            this._exception = e;
            this._message = responseXML;
            this._code = "WebServiceException";
        }
        catch (Exception e)
        {
            this._exception = e;
            this._message = e.getMessage();
            this._code = "WebServiceException2";
        }

    }


    public WebServiceException(Exception exception, String methodName, String requestXML, String code, String message)
    {
        super("WebServiceException");
        Log.i("bbb", "cccc");
        this._requestXML = requestXML;
        this._methodName = methodName;
        this._exception = exception;
        this._message = message;
        this._code = code;
    }


    @Override
    public String getMessage()
    {
        return this._message;
    }

    public String getRequestXML()
    {
        return this._requestXML;
    }

    public String getMethodName()
    {
        return this._methodName;
    }

    public Exception getException()
    {
        return this._exception;
    }
}
