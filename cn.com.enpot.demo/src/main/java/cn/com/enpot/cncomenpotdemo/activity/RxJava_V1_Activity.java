package cn.com.enpot.cncomenpotdemo.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.widget.Button;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import cn.com.enpot.cncomenpotdemo.DownloadUtils;
import cn.com.enpot.cncomenpotdemo.R;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Howe on 2017/7/30.
 */

public class RxJava_V1_Activity extends Activity
{
    public final String TAG = "RxJava_V1_Activity";
    
    Button btnDownload;
    AppCompatImageView imgABC;
    ProgressDialog mProgressDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava_v1);
        
        initView();
        
        mDownloadUtils = new DownloadUtils();
    
        mProgressDialog = new ProgressDialog(RxJava_V1_Activity.this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); //设置进度条风格，风格为圆形，旋转的
        // 设置ProgressDialog 标题
        mProgressDialog.setTitle("圆形进度条");
        // 设置ProgressDialog 提示信息
        mProgressDialog.setMessage("正在下载中……");
        // 设置ProgressDialog 标题图标
        // mProgressDialog.setIcon(R.drawable.ic_launcher);
        // 设置ProgressDialog 进度条进度
        mProgressDialog.setProgress(100);
        // 设置ProgressDialog 的进度条是否不明确
        mProgressDialog.setIndeterminate(false);
        // 设置ProgressDialog 是否可以按退回按键取消
        mProgressDialog.setCancelable(true);
        // 设置ProgressDialog 的一个Button
        mProgressDialog.setButton("取消", new android.content.DialogInterface.OnClickListener() {
            public void onClick(android.content.DialogInterface dialog, int i)
            {
                dialog.cancel();
            }
        });
    
        initEvent();
    }
    
    private void initView()
    {
        
        btnDownload = (Button) findViewById(R.id.btnDownload);
        imgABC = (AppCompatImageView) findViewById(R.id.imgABC);
        
    }
    
    DownloadUtils mDownloadUtils;
    public String IMAGE_URL = "http://wx4.sinaimg.cn/mw690/68318509gy1fiuq2eidn9g20b804nx6r.gif";
    // "http://wx4.sinaimg.cn/mw690/68318509gy1fiuq2eidn9g20b804nx6r.gif";
    // "https://oimageb7.ydstatic.com/image?id=8068200313027958926&product=adpublish";
    
    private void initEvent()
    {
        RxView.clicks(btnDownload)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>()
                {
                    @Override
                    public void call(Void aVoid)
                    {
                        mDownloadUtils
                                .Download(IMAGE_URL)
                                .subscribeOn(Schedulers.io())
                                .doOnSubscribe(new Action0()
                                {
                                    @Override
                                    public void call()
                                    {
                                        mProgressDialog.setProgress(0);
                                        mProgressDialog.show();
                                    }
                                })
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<byte[]>()
                                {
                                    @Override
                                    public void onNext(byte[] bytes)
                                    {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        imgABC.setImageBitmap(bitmap);
                                    }

                                    @Override
                                    public void onCompleted()
                                    {
                                        Log.i(TAG, "onCompleted: Download : OK ; TransformBitmap : OK");
                                    }

                                    @Override
                                    public void onError(Throwable e)
                                    {
                                        Log.e(TAG, e.getMessage());
                                    }
                                });
                        
                    }
                });
    }
}
