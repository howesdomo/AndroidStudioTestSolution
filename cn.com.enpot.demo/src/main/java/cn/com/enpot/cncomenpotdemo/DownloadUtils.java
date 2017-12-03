package cn.com.enpot.cncomenpotdemo;


import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Howe on 2017/7/30.
 */

public class DownloadUtils
{
    public static final String TAG = DownloadUtils.class.getSimpleName();
    
    OkHttpClient mClient;

    public DownloadUtils()
    {
        mClient = new OkHttpClient();
    }

    public Observable<byte[]> Download(String url)
    {
        return Observable.create(new Observable.OnSubscribe<byte[]>()
        {
            @Override
            public void call(Subscriber<? super byte[]> subscriber)
            {
                if (subscriber.isUnsubscribed() == true)
                {
                    Log.e(TAG, "call: isUnsubscribed is false");
                }
                else
                {
                    Request request = new Request.Builder().url(url).build();
                    mClient.newCall(request).enqueue(new Callback()
                    {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException
                        {
                            if (response == null || response.isSuccessful() == false)
                            {
                                subscriber.onError(new Exception("response has error"));
                                return;
                            }

                            byte[] r = response.body().bytes();
                            if (r == null)
                            {
                                subscriber.onError(new Exception("byte[] is null"));
                                return;
                            }

                            subscriber.onNext(r);
                            subscriber.onCompleted();
                        }

                        @Override
                        public void onFailure(Call call, IOException e)
                        {
                            subscriber.onError(e);
                        }
                    });
                }
            }
        });
    }
    
    
}
