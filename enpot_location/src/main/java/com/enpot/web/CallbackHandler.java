package com.enpot.web;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class CallbackHandler<R> extends Handler {
	
	public CallbackHandler() {
	}

//	public CallbackHandler(Callback callback) {
//		super(callback);
//	}
//
//	public CallbackHandler(Looper looper) {
//		super(looper);
//	}
//
//	public CallbackHandler(Looper looper, Callback callback) {
//		super(looper, callback);
//	}

	public void execute(R result)
    {
		Log.i("CALLBACK", "sswgewgewg");
	}
	
}
