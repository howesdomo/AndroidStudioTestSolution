package cn.com.enpot.enpot_location.models;

import com.baidu.location.BDLocation;
import com.enpot.web.CallbackHandler;
import com.enpot.web.WebService;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.enpot.enpot_location.commom.StaticInfo;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Howe on 2017/8/6.
 */

public class APP_WebService
{
    public void Upload(final String imei, final BDLocation bdLocation)
    {
        String methodName = "LocationMonitorLogs";
        
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("methodName", methodName);
        
        List<String> jsonArgsList = new ArrayList<String>();
        jsonArgsList.add(imei);
        jsonArgsList.add(StaticInfo.GsonForWebService.toJson(bdLocation));
        
        map.put("jsonArgs", jsonArgsList);
        
        WebService web = new WebService(StaticInfo.GetLastestEndPoint());
        web.RequestJson
                (
                        String.class,
                        "ExecuteWebServiceMethodV3",
                        map,
                        new CallbackHandler<String>()
                        {
                            @Override
                            public void execute(String data)
                            {
                                if (data != null && StringUtils.isNotBlank(data))
                                {
                                    SOAPResult soapResult = StaticInfo.GsonForWebService.fromJson(data, SOAPResult.class);
                                }
                            }
                        }
                );
        
        
    }
    
    
}
