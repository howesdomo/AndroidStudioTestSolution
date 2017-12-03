package com.enpot.web;

import android.os.Handler;
import android.util.Log;

public class WebServiceExceptionHandler extends Handler {
	
	public WebServiceExceptionHandler() {
		super();
	}

	public void execute(WebServiceException e)
	{
		Log.i("WebServiceException", e.getMethodName() + ":" + e.getMessage());
	}
	
}