package cn.com.enpot.cncomenpotdemo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import cn.com.enpot.cncomenpotdemo.R;
import cn.com.enpot.cncomenpotdemo.common.StaticInfo;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity
{
    public final String TAG = "MainActivity";
    
    LinearLayout llSub;
    
    LinearLayout lvMainMenu;
    LinearLayout lvSubMenu;
    
    Button btnSubBack;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        StaticInfo.InitStaticInfo(MainActivity.this, R.raw.beep, R.raw.error);
        
        initView();
        initEvent();
        initData();
    }
    
    
    private void initView()
    {
        this.lvMainMenu = (LinearLayout) findViewById(R.id.lvMainMenu);
        
        this.llSub = (LinearLayout) findViewById(R.id.llSub);
        this.lvSubMenu = (LinearLayout) findViewById(R.id.lvSubMenu);
        
        this.btnSubBack = (Button) findViewById(R.id.btnSubBack);
    }
    
    
    private void initEvent()
    {
        RxView.clicks(btnSubBack)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>()
                {
                    @Override
                    public void call(Void aVoid)
                    {
                        lvSubMenu.removeAllViews();
                        llSub.setVisibility(View.GONE);
                    }
                });
    }
    
    private void initData()
    {
        addTextView();
    }
    
    
    private void addTextView()
    {
        Button btnTextView = new Button(this);
        btnTextView.setText(String.format("TextView"));
        
        RxView.clicks(btnTextView)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>()
                {
                    @Override
                    public void call(Void aVoid)
                    {
                        subMenu_TextView();
                        llSub.setVisibility(View.VISIBLE);
                    }
                });
        
        lvMainMenu.addView(btnTextView);
        
        
        Button btnRxJava = new Button(this);
        btnRxJava.setText(String.format("RxJava"));
    
        RxView.clicks(btnRxJava)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>()
                {
                    @Override
                    public void call(Void aVoid)
                    {
                        subMenu_RxJava();
                        llSub.setVisibility(View.VISIBLE);
                    }
                });
    
        lvMainMenu.addView(btnRxJava);
    }
    
    private void subMenu_TextView()
    {
        Button btn = new Button(this);
        btn.setText("TextView - 走马灯原生");
        RxView.clicks(btn)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>()
                {
                    @Override
                    public void call(Void aVoid)
                    {
                        Intent i = new Intent(MainActivity.this, MarqueeTest_V1_Activity.class);
                        startActivity(i);
                    }
                });
        
        lvSubMenu.addView(btn);
    }
    
    private void subMenu_RxJava()
    {
        Button btn = new Button(this);
        btn.setText("RxJava - TTTTT");
        RxView.clicks(btn)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>()
                {
                    @Override
                    public void call(Void aVoid)
                    {
                        Intent i = new Intent(MainActivity.this, RxJava_V1_Activity.class);
                        startActivity(i);
                    }
                });
    
        Button btnRxView_V1_MutiButton_ClickBuffer = new Button(this);
        btnRxView_V1_MutiButton_ClickBuffer.setText("RxView - 多个按钮统一防抖实例");
        RxView.clicks(btnRxView_V1_MutiButton_ClickBuffer)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>()
                {
                    @Override
                    public void call(Void aVoid)
                    {
                        Intent i = new Intent(MainActivity.this, RxView_V1_MutiButton_ClickBuffer.class);
                        startActivity(i);
                    }
                });
    
        lvSubMenu.addView(btn);
        lvSubMenu.addView(btnRxView_V1_MutiButton_ClickBuffer);
    }
}
