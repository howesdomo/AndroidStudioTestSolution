package cn.com.enpot.cncomenpotdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import java.util.List;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.enpot.cncomenpotdemo.R;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Howe on 2017/9/12.
 */

public class RxView_V1_MutiButton_ClickBuffer extends Activity
{
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;
    @Bind(R.id.button3)
    Button button3;
    @Bind(R.id.button4)
    Button button4;
    @Bind(R.id.button5)
    Button button5;
    @Bind(R.id.button6)
    Button button6;
    
    @Override
    protected void onResume()
    {
        super.onResume();
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ButterKnife.unbind(RxView_V1_MutiButton_ClickBuffer.this);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxview_v1_mutibutton_click);
        ButterKnife.bind(this);
    }
    
    private void initView()
    {
        ButterKnife.bind(RxView_V1_MutiButton_ClickBuffer.this);
    }
    
    private void initEvent()
    {
        
    }
    
    @OnClick({R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6})
    public void onClick(final View view)
    {
        // region 失败例子
        // 实际执行效果没有问题，但是会出现最开始我们说到的问题，就是第一次点击没反应。那怎么办呢？
        // 其实RxBinding 在内部去添加监听与ButterKnife 的监听“重复”了，所以我们可以自己实现RxBinding 效果，不在内部添加监听。如下：
//        RxView.clicks(view)
//                .throttleFirst(1, TimeUnit.SECONDS)
//                .subscribe(new Action1<Void>()
//                {
//                    @Override
//                    public void call(Void aVoid)
//                    {
//                        if (view instanceof Button)
//                        {
//                            Button btn = (Button) view;
//                            // Toast.makeText(RxView_V1_MutiButton_ClickBuffer.this, btn.getText().toString(), Toast.LENGTH_LONG).show();
//                            Log.i("Click", "call: " + btn.getText());
//                        }
//                    }
//                });
        //endregion
        
        // 经测试仍没有效果
        Observable
                .create(new Observable.OnSubscribe<View>()
                {
                    @Override
                    public void call(Subscriber<? super View> subscriber)
                    {
                        subscriber.onNext(view);
                    }
                })
                .buffer(1000, TimeUnit.MILLISECONDS) // 缓存 500 毫秒内点击的操作
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<View>>()
                {
                    @Override
                    public void call(List<View> list)
                    {
                        if (list == null || list.size() == 0)
                        {
                            return;
                        }
                        View responseView = list.get(list.size() - 1); // 只响应500毫秒内缓存的操作的最后一个
                        if (responseView instanceof Button)
                        {
                            Button responseButton = (Button) responseView;
                            Log.i("Click", "call: " + responseButton.getText() + "; listCount" + list.size());
                        }
                    }
                });
    }
}
