package cn.com.enpot.scanprint;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity
{
    
    @Bind(R.id.btnMAC)
    Button btnMAC;
    @Bind(R.id.txtContent)
    EditText txtContent;
    @Bind(R.id.txtQty)
    EditText txtQty;
    @Bind(R.id.btnPrint)
    Button btnPrint;
    @Bind(R.id.btnClear)
    Button btnClear;
    
    ProgressDialog mProgressDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        
        if (mProgressDialog == null)
        {
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);
        }
        
        this.bindUI();
        this.initEvent();
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(this.mReceiver, ScanUtil.FILTER);
    }
    
    @Override
    protected void onPause()
    {
        // 注销获取扫描结果的广播
        this.unregisterReceiver(this.mReceiver);
        super.onPause();
    }
    
    private void bindUI()
    {
        if (StringUtils.isBlank(StaticInfo.BluetoothMACAddress))
        {
            this.btnMAC.setText("请设置打印机MAC地址");
        }
        else
        {
            this.btnMAC.setText(String.format("打印机MAC地址 : %s", StaticInfo.BluetoothMACAddress));
        }
        
        if (StringUtils.isBlank(StaticInfo.BluetoothMACAddress))
        {
            
        }
    }
    
    private void initEvent()
    {
        setDecodeListener();
        
        RxView.clicks(btnMAC)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>()
                {
                    @Override
                    public void call(Void aVoid)
                    {
                        Intent it = new Intent(MainActivity.this, SetActivity.class);
                        Bundle b = new Bundle();
                        String arg = StringUtils.isBlank(txtContent.getText().toString().trim()) ? "0" : txtContent.getText().toString().trim();
                        b.putString("Password", arg);
                        it.putExtras(b);
                        MainActivity.this.startActivityForResult(it, 110);
                    }
                });
        
        RxView.clicks(btnPrint)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>()
                {
                    @Override
                    public void call(Void aVoid)
                    {
                        print();
                    }
                });
        
        RxView.clicks(btnClear)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>()
                {
                    @Override
                    public void call(Void aVoid)
                    {
                        txtQty.setText("");
                        txtContent.setText("");
                    }
                });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        bindUI();
    }
    
    private void print()
    {
        if (StringUtils.isBlank(StaticInfo.BluetoothMACAddress))
        {
            Toast.makeText(MainActivity.this, "请设置打印机MAC地址", Toast.LENGTH_SHORT).show();
            StaticInfo.PlayErrorSoundAndPlayErrorVibrator();
            mIsScanning = false;
            return;
        }
        
        if (StringUtils.isNumeric(this.txtQty.getText()) == false)
        {
            Toast.makeText(MainActivity.this, "请设置打印张数", Toast.LENGTH_SHORT).show();
            StaticInfo.PlayErrorSoundAndPlayErrorVibrator();
            mIsScanning = false;
            return;
        }
        
        this.ProgressDialogShow();
        new PrinterUtil_QLn420()
                .Print(
                        StaticInfo.PrinterSetting_AlignLeft.toString(), // 水平偏移
                        StaticInfo.PrinterSetting_AlignTop.toString(), // 垂直偏移
                        this.txtContent.getText().toString(), // QRCode
                        this.txtQty.getText().toString() // 打印张数
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>()
                {
                    @Override
                    public void onCompleted()
                    {
                        ProgressDialogDismiss();
                        
                    }
                    
                    @Override
                    public void onError(Throwable e)
                    {
                        ProgressDialogDismiss();
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        StaticInfo.PlayErrorSoundAndPlayErrorVibrator();
                        mIsScanning = false;
                    }
                    
                    @Override
                    public void onNext(Boolean aBoolean)
                    {
                        Toast.makeText(MainActivity.this, "打印完毕", Toast.LENGTH_SHORT).show();
                        mIsScanning = false;
                    }
                });
    }
    
    //     加载
    public void ProgressDialogShow()
    {
        this.ProgressDialogShow("", "正在加载中...");
    }
    
    // 修改
    public void ProgressDialogSetMessage(String message)
    {
        if (mProgressDialog != null
                && mProgressDialog.isShowing() == true)
        {
            mProgressDialog.setMessage(message);
        }
    }
    
    public void ProgressDialogShow(String title, String msg)
    {
        if (mProgressDialog == null)
        {
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);
        }
        
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }
    
    //     取消
    public void ProgressDialogDismiss()
    {
        if (mProgressDialog != null)
        {
            mProgressDialog.dismiss();
        }
    }
    
    //region 扫描打印机MAC地址
    
    public BroadcastReceiver mReceiver;
    
    public Boolean mIsScanning = false;
    
    /**
     * 获取扫描数据
     */
    private void setDecodeListener()
    {
        this.mReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (mIsScanning == false)
                {
                    mIsScanning = true;
                    StaticInfo.PlayBeepSound();
                    
                    final String data = intent.getStringExtra("value").toString().trim();
                    
                    txtContent.setText(data);
                    
                    print();
                }
            }
        };
    }
}
