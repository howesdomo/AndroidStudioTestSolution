package com.howe.ir;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.enpot.utils.EnpotLog;
import com.howe.ir.utils.MessageBoxUtil;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    
    @Bind(R.id.btnOpen)
    Button btnOpen;
    @Bind(R.id.btnClose)
    Button btnClose;
    @Bind(R.id.edtFrequency)
    EditText edtFrequency;
    @Bind(R.id.edtHDRMark)
    EditText edtHDRMark;
    @Bind(R.id.edtHDRSpace)
    EditText edtHDRSpace;
    @Bind(R.id.edtBitMark)
    EditText edtBitMark;
    @Bind(R.id.edtOneSpace)
    EditText edtOneSpace;
    @Bind(R.id.edtZeroSpace)
    EditText edtZeroSpace;
    
    /**
     * (核心)红外遥控
     */
    private ConsumerIrManager mCIR;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        
        // 获取系统的红外遥控服务
        mCIR = (ConsumerIrManager) getSystemService(Context.CONSUMER_IR_SERVICE);
        initEvents();
        initData();
    }
    
    private Integer mFREQUENCY = 38400;
    private Integer mHDR_MARK = 9000;
    private Integer mHDR_SPACE = 4500;
    private Integer mBIT_MARK = 600;
    private Integer mONE_SPACE = 1600;
    private Integer mZERO_SPACE = 600;
    
    private void initData()
    {
        edtFrequency.setText(mFREQUENCY.toString());
        edtHDRMark.setText(mHDR_MARK.toString());
        edtHDRSpace.setText(mHDR_SPACE.toString());
        edtBitMark.setText(mBIT_MARK.toString());
        edtOneSpace.setText(mONE_SPACE.toString());
        edtZeroSpace.setText(mZERO_SPACE.toString());
    }
    
    private void bindData()
    {
        try
        {
            mFREQUENCY = Integer.valueOf(edtFrequency.getText().toString());
            mHDR_MARK = Integer.valueOf(edtHDRMark.getText().toString());
            mHDR_SPACE = Integer.valueOf(edtHDRSpace.getText().toString());
            mBIT_MARK = Integer.valueOf(edtBitMark.getText().toString());
            mONE_SPACE = Integer.valueOf(edtOneSpace.getText().toString());
            mZERO_SPACE = Integer.valueOf(edtZeroSpace.getText().toString());
        }
        catch (Exception ex)
        {
            MessageBoxUtil.ShowExceptionDialog(MainActivity.this, ex);
        }
    }
    
    private void initEvents()
    {
        //region 开机
        RxView.clicks(btnOpen)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>()
                {
                    @Override
                    public void call(Void aVoid)
                    {
                        if (mCIR == null || mCIR.hasIrEmitter() == false)
                        {
                            MessageBoxUtil.ShowExceptionDialog(MainActivity.this, "本设备未找到可用的红外发射器。");
                            return;
                        }
                        sendOpen();
                    }
                });
        //endregion
        
        RxView.clicks(btnClose)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>()
                {
                    @Override
                    public void call(Void aVoid)
                    {
                        if (mCIR == null || mCIR.hasIrEmitter() == false)
                        {
                            MessageBoxUtil.ShowExceptionDialog(MainActivity.this, "本设备未找到可用的红外发射器。");
                            return;
                        }
                        getSomething();
                    }
                });
        
        RxTextView.textChangeEvents(edtZeroSpace)
                .debounce(300, TimeUnit.MILLISECONDS) //debounce:每次文本更改后有300毫秒的缓冲时间，默认在computation调度器
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<TextViewTextChangeEvent>()
                {
                    @Override
                    public void call(TextViewTextChangeEvent textViewTextChangeEvent)
                    {
                        String key = textViewTextChangeEvent.text().toString().trim();
                        if (StringUtils.isNotBlank(key))
                        {
                            edtBitMark.setText(key);
                        }
                        else
                        {
                            edtBitMark.setText("");
                        }
                    }
                });
    }
    
    private void sendOpenDemo()
    {
        // 一种交替的载波序列模式，通过毫秒测量
//        int[] pattern = {
//                9000, 4500, // 起始码S电平宽度
//                600, 600, 600, 600, 600, 1600, 600, 600, 600, 600,
//                600, 600, 600, 600, 600, 600, 600, 1600, 600, 1600,
//                600, 600, 600, 1600, 600, 1600, 600, 1600, 600, 1600,
//                600, 1600, 600, 600, 600, // 35位数据码
//                600, 20000, // 连接码C电平宽度
//                600, 600, 600, 600, 1600, 600, 600, 600, 600, 600,
//                600, 600, 600, 600, 1600, 600, 1600, 600, 1600, 600,
//                600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600,
//                39344, 9000 // 32位数据码
//            };
        
        int[] pattern =
                {
                        1901, 4453, 625, 1614, 625, 1588, 625, 1614, 625,
                        442, 625, 442, 625, 468, 625, 442, 625, 494, 572, 1614,
                        625, 1588, 625, 1614, 625, 494, 572, 442, 651, 442, 625,
                        442, 625, 442, 625, 1614, 625, 1588, 651, 1588, 625, 442,
                        625, 494, 598, 442, 625, 442, 625, 520, 572, 442, 625, 442,
                        625, 442, 651, 1588, 625, 1614, 625, 1588, 625, 1614, 625,
                        1588, 625, 48958
                };

//        int FREQUENCY = 38028;  // T = 26.296 us
//        int HDR_MARK = 342;
//        int HDR_SPACE = 171;
//        int BIT_MARK = 21;
//        int ONE_SPACE = 60;
//        int ZERO_SPACE = 21;
        
        int FREQUENCY = 38400;  // T = 26.296 us
        int HDR_MARK = 9000;
        int HDR_SPACE = 4500;
        int BIT_MARK = 600;
        int ONE_SPACE = 1600;
        int ZERO_SPACE = 600;

//
        IrCommandBuilder builder = IrCommandBuilder.irCommandBuilder(38400); // Static factory method
        IrCommand builderCommand = builder
                .pair(HDR_MARK, HDR_SPACE)  // Lead-in sequence
                
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
//***
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
//***
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
//***
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
//***
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
//***
                
                .mark(BIT_MARK)             // lead-out sequence
                .build();
        // mCIR.transmit(builderCommand.frequency, builderCommand.pattern);
//

//        IrCommand necCommand = IrCommand.NEC.buildNEC(32, 0x080040BF);
//        pattern = necCommand.pattern;
        
        pattern = builderCommand.pattern;
        // 在38.4KHz条件下进行模式转换
        mCIR.transmit(38400, pattern);
    }
    
    
    private void sendOpen() // 组织的是 0x08, 0x00, 0x40 // 接收到的是 FC 00 FF
    {
        bindData();
        
        IrCommandBuilder builder = IrCommandBuilder.irCommandBuilder(mFREQUENCY); // Static factory method
        IrCommand builderCommand = builder
                .pair(mHDR_MARK, mHDR_SPACE)  // Lead-in sequence
                
                .pair(mBIT_MARK, mZERO_SPACE) // 0
                .pair(mBIT_MARK, mZERO_SPACE) // 0
                .pair(mBIT_MARK, mZERO_SPACE) // 0
                .pair(mBIT_MARK, mZERO_SPACE) // 0
                .pair(mBIT_MARK, mONE_SPACE)  // 1
                .pair(mBIT_MARK, mZERO_SPACE) // 0
                .pair(mBIT_MARK, mZERO_SPACE) // 0
                .pair(mBIT_MARK, mZERO_SPACE) // 0
//***
                .pair(mBIT_MARK, mZERO_SPACE) // 0
                .pair(mBIT_MARK, mZERO_SPACE) // 0
                .pair(mBIT_MARK, mZERO_SPACE) // 0
                .pair(mBIT_MARK, mZERO_SPACE) // 0
                .pair(mBIT_MARK, mZERO_SPACE) // 0
                .pair(mBIT_MARK, mZERO_SPACE) // 0
                .pair(mBIT_MARK, mZERO_SPACE) // 0
                .pair(mBIT_MARK, mZERO_SPACE) // 0
//***
                .pair(mBIT_MARK, mONE_SPACE)  // 1
                .pair(mBIT_MARK, mONE_SPACE)  // 1
                .pair(mBIT_MARK, mONE_SPACE)  // 1
                .pair(mBIT_MARK, mONE_SPACE)  // 1
                .pair(mBIT_MARK, mONE_SPACE)  // 1
                .pair(mBIT_MARK, mONE_SPACE)  // 1
                .pair(mBIT_MARK, mONE_SPACE)  // 1
                .pair(mBIT_MARK, mONE_SPACE)  // 1
//***
                .pair(mBIT_MARK, mZERO_SPACE) // 0
                .pair(mBIT_MARK, mONE_SPACE)  // 1
                .pair(mBIT_MARK, mZERO_SPACE) // 0
                .pair(mBIT_MARK, mZERO_SPACE) // 0
                .pair(mBIT_MARK, mZERO_SPACE) // 0
                .pair(mBIT_MARK, mZERO_SPACE) // 0
                .pair(mBIT_MARK, mZERO_SPACE) // 0
                .pair(mBIT_MARK, mZERO_SPACE) // 0
//***
                .pair(mBIT_MARK, mONE_SPACE)  // 1
                .pair(mBIT_MARK, mZERO_SPACE) // 0
                .pair(mBIT_MARK, mONE_SPACE)  // 1
                .pair(mBIT_MARK, mONE_SPACE)  // 1
                .pair(mBIT_MARK, mONE_SPACE)  // 1
                .pair(mBIT_MARK, mONE_SPACE)  // 1
                .pair(mBIT_MARK, mONE_SPACE)  // 1
                .pair(mBIT_MARK, mONE_SPACE)  // 1
//***
                
                .mark(mBIT_MARK)             // lead-out sequence
                .build();
        
        int[] pattern = null;
        pattern = builderCommand.pattern;
        mCIR.transmit(builderCommand.frequency, builderCommand.pattern);
    }
    
    // 勿动
    private void sendOpenV2() // 组织的是 0x08, 0x00, 0x40 // 接收到的是 FC 00 FF
    {
        int FREQUENCY = 38400;  // T = 26.296 us
        int HDR_MARK = 9000;
        int HDR_SPACE = 4500;
        int BIT_MARK = 600;
        int ONE_SPACE = 1600;
        int ZERO_SPACE = 600;
        
        IrCommandBuilder builder = IrCommandBuilder.irCommandBuilder(FREQUENCY); // Static factory method
        IrCommand builderCommand = builder
                .pair(HDR_MARK, HDR_SPACE)  // Lead-in sequence
                
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
//***
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
//***
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
//***
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ZERO_SPACE) // 0
//***
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ZERO_SPACE) // 0
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
                .pair(BIT_MARK, ONE_SPACE)  // 1
//***
                
                .mark(BIT_MARK)             // lead-out sequence
                .build();
        int[] pattern = null;
        pattern = builderCommand.pattern;
        mCIR.transmit(38400, pattern);
    }
    
    private void getSomething()
    {
        if (!mCIR.hasIrEmitter())
        {
            MessageBoxUtil.ShowExceptionDialog(MainActivity.this, "本设备未找到可用的红外发射器。");
            return;
        }
        
        
        StringBuilder sb = new StringBuilder();
        // 获得可用的载波频率范围
        ConsumerIrManager.CarrierFrequencyRange[] freqs = mCIR.getCarrierFrequencies();
        sb.append("IR Carrier Frequencies:\n");// 红外载波频率
        // 边里获取频率段
        for (ConsumerIrManager.CarrierFrequencyRange range : freqs)
        {
            sb.append(String.format("    %d - %d\n",
                    range.getMinFrequency(), range.getMaxFrequency()
            ));
        }
        EnpotLog.i(sb.toString());
        
        // mFreqsText.setText(b.toString());// 显示结果
        // MessageBoxUtil.ShowInfoDialog(MainActivity.this, sb.toString());
    }
}
