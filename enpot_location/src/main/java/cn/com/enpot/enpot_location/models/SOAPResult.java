package cn.com.enpot.enpot_location.models;

import com.enpot.web.WebServiceException;

import java.io.Serializable;

/**
 * Created by Howe on 2017/3/31.
 */

public class SOAPResult extends Throwable implements Serializable
{
    /**
     * 版本号
     */
    private static final long serialVersionUID = -8327599049239612587L;

    public SOAPResult()
    {

    }

    public SOAPResult(WebServiceException ex)
    {
        this.IsComplete = false;
        this.ExceptionInfo = "e:" + ex.getMessage() + "/r/n" + ex.getStackTrace();
        this.IsSuccess = false;
        this.BusinessExceptionInfo = "";
    }

    public Boolean IsComplete;

    public String ExceptionInfo;

    //region 业务逻辑

    /// <summary>
    /// 业务逻辑运行成功
    /// </summary>
    public Boolean IsSuccess;

    /// <summary>
    /// 业务逻辑报错信息
    /// </summary>
    public String BusinessExceptionInfo;


    public String ReturnObjectJson;

    //endregion
}
