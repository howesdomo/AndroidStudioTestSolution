package cn.com.enpot.scanprint;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * Created by Howe on 2017/8/21.
 */

public class SetActivity extends Activity
{
    @Bind(R.id.edtMAC)
    EditText edtMAC;
    @Bind(R.id.edtAlignLeft)
    EditText edtAlignLeft;
    @Bind(R.id.edtAlignTop)
    EditText edtAlignTop;
    @Bind(R.id.btnSaveBTPrinter)
    Button btnSaveBTPrinter;
    @Bind(R.id.txtTemplate)
    EditText txtTemplate;
    @Bind(R.id.btnSaveTemplate)
    Button btnSaveTemplate;
    @Bind(R.id.btnResetTemplate)
    Button btnResetTemplate;
    @Bind(R.id.llDev)
    LinearLayout llDev;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        ButterKnife.bind(this);
        
        Bundle bundle = this.getIntent().getExtras();
        try
        {
            String p = bundle.getString("Password");
            if (StringUtils.equalsIgnoreCase(p, "enpot"))
            {
                llDev.setVisibility(View.VISIBLE);
            }
        }
        catch (Exception ex)
        {
            
        }
        
        initEvent();
        initData();
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
    
    private void initEvent()
    {
        setDecodeListener();
        
        RxView.clicks(btnSaveBTPrinter)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>()
                {
                    @Override
                    public void call(Void aVoid)
                    {
                        Integer left = 0;
                        try
                        {
                            left = Integer.valueOf(edtAlignLeft.getText().toString());
                        }
                        catch (Exception ex)
                        {
                            edtAlignLeft.setError("请输入正确的水平偏移。");
                            return;
                        }
                        
                        Integer top = 0;
                        try
                        {
                            top = Integer.valueOf(edtAlignTop.getText().toString());
                        }
                        catch (Exception ex)
                        {
                            edtAlignTop.setError("请输入正确的垂直偏移。");
                            return;
                        }
                        
                        StaticInfo.SaveBTPrinterConfig(SetActivity.this, edtMAC.getText().toString(), left, top);
                        Toast.makeText(SetActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    }
                });
        
        RxView.clicks(btnSaveTemplate)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>()
                {
                    @Override
                    public void call(Void aVoid)
                    {
                        StaticInfo.SaveZPLTemplate(SetActivity.this, txtTemplate.getText().toString().trim());
                        Toast.makeText(SetActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    }
                });
    
        RxView.clicks(btnResetTemplate)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>()
                {
                    @Override
                    public void call(Void aVoid)
                    {
                        StaticInfo.SaveZPLTemplate(SetActivity.this, StaticInfo.Template_Default);
                        Toast.makeText(SetActivity.this, "重置成功", Toast.LENGTH_SHORT).show();
                        txtTemplate.setText(StaticInfo.PrintZPLTemplate);
                    }
                });
    }
    
    private void initData()
    {
        this.edtMAC.setText(StaticInfo.BluetoothMACAddress);
        this.edtAlignTop.setText(StaticInfo.PrinterSetting_AlignTop.toString());
        this.edtAlignLeft.setText(StaticInfo.PrinterSetting_AlignLeft.toString());
        
        this.txtTemplate.setText(StaticInfo.PrintZPLTemplate);
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
                    
                    final String data = intent.getStringExtra("value");
                    
                    if (edtMAC.isFocused())
                    {
                        edtMAC.setText(data.trim());
                        mIsScanning = false;
                        return;
                    }
                    
                    mIsScanning = false;
                }
            }
        };
    }
    
}
