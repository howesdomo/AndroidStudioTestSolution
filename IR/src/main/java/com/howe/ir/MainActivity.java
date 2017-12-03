package com.howe.ir;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.enpot.utils.DateTime;
import com.howe.ir.utils.MessageBoxUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    
    @Bind(R.id.edtFrequency)
    EditText edtFrequency;
    @Bind(R.id.btnTVPower)
    Button btnTVPower;
    @Bind(R.id.btnTVSoundMinus)
    Button btnTVSoundMinus;
    @Bind(R.id.btnTVSoundPlus)
    Button btnTVSoundPlus;
    @Bind(R.id.btnTVChangeSources)
    Button btnTVChangeSources;
    @Bind(R.id.btnTVSilence)
    Button btnTVSilence;
    @Bind(R.id.btnSTBPower)
    Button btnSTBPower;
    @Bind(R.id.btnSTBSoundMinus)
    Button btnSTBSoundMinus;
    @Bind(R.id.btnSTBSoundPlus)
    Button btnSTBSoundPlus;
    @Bind(R.id.btnSTBOK)
    Button btnSTBOK;
    @Bind(R.id.btnSTBReturn)
    Button btnSTBReturn;
    @Bind(R.id.btnSTBBackspace)
    Button btnSTBBackspace;
    @Bind(R.id.btnSTBLeft)
    Button btnSTBLeft;
    @Bind(R.id.btnSTBUp)
    Button btnSTBUp;
    @Bind(R.id.btnSTBRight)
    Button btnSTBRight;
    @Bind(R.id.btnSTBDown)
    Button btnSTBDown;
    @Bind(R.id.btnSTBHomePage)
    Button btnSTBHomePage;
    @Bind(R.id.btnSTBMenu)
    Button btnSTBMenu;
    @Bind(R.id.btnSTBMouse)
    Button btnSTBMouse;
    
    
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
        
        //region 加载震动设置
        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        //endregion
        
        initEvents();
        initData();
    }
    
    private Integer mFREQUENCY = 38000;
    private Integer mHDR_MARK = 9000;
    private Integer mHDR_SPACE = 4500;
    private Integer mBIT_MARK = 600;
    private Integer mONE_SPACE = 1600;
    private Integer mZERO_SPACE = 600;
    
    private void initData()
    {
        edtFrequency.setText(mFREQUENCY.toString());
    }
    
    private void bindData()
    {
        try
        {
            mFREQUENCY = Integer.valueOf(edtFrequency.getText().toString());
        }
        catch (Exception ex)
        {
            MessageBoxUtil.ShowExceptionDialog(MainActivity.this, ex);
        }
    }
    
    private void initEvents()
    {
        
    }
        
    private int[] getPatterArrayByViewId(View btn)
    {
        int[] r = null;
        
        switch (btn.getId())
        {
            //region TV Button
            
            case R.id.btnTVPower:
                r = new int[]{
                        2400, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600, 25750,
                        2400, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600, 25750,
                        2400, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600, 25750,
                        2400, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600};
                break;
            
            case R.id.btnTVSoundMinus:
                r = new int[]{
                        2400, 600, 1200, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600, 25750,
                        2400, 600, 1200, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600, 25750,
                        2400, 600, 1200, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600};
                break;
            
            case R.id.btnTVSoundPlus:
                r = new int[]{
                        2400, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600, 25750,
                        2400, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600, 25750,
                        2400, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600, 25750,
                        2400, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600,};
                break;
            case R.id.btnTVChangeSources:
                r = new int[]{
                        2400, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600, 25750,
                        2400, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600, 25750,
                        2400, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600, 25750,
                        2400, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600, 25750,
                        2400, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600,};
                break;
            case R.id.btnTVSilence:
                r = new int[]{
                        2400, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600, 25750,
                        2400, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600, 25750,
                        2400, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600, 25750,
                        2400, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600, 25750,
                        2400, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 1200, 600, 600, 600, 600, 600, 600, 600, 600,};
                break;
            
            //endregion TV Button
            
            //region STB Button
            case R.id.btnSTBPower:
                r = new int[]{
                        9000, 4500,
                        600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 1600, 600, 600,
                        600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600,
                        600, 1600, 600, 600, 600, 1600, 600, 1600, 600, 600, 600, 600, 600, 1600, 600, 600,
                        600, 600, 600, 1600, 600, 600, 600, 600, 600, 1600, 600, 1600, 600, 600, 600, 1600,
                        600, 38991, 9000, 2227, 600,};
                break;
            case R.id.btnSTBSoundMinus:
                r = new int[]{
                        9000, 4500,
                        600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 1600, 600, 600,
                        600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600,
                        600, 600, 600, 600, 600, 1600, 600, 1600, 600, 1600, 600, 600, 600, 600, 600, 600,
                        600, 1600, 600, 1600, 600, 600, 600, 600, 600, 600, 600, 1600, 600, 1600, 600, 1600,
                        600, 38929, 9000, 2232, 600};
                break;
            case R.id.btnSTBSoundPlus:
                r = new int[]{
                        9000, 4500,
                        600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 1600, 600, 600,
                        600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600,
                        600, 1600, 600, 600, 600, 1600, 600, 600, 600, 1600, 600, 600, 600, 600, 600, 600,
                        600, 600, 600, 1600, 600, 600, 600, 1600, 600, 600, 600, 1600, 600, 1600, 600, 1600,
                        600, 38917, 9000, 2233, 600};
                break;
            case R.id.btnSTBOK:
                r = new int[]{
                        9000, 4500,
                        600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 1600, 600, 600,
                        600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600,
                        600, 1600, 600, 600, 600, 1600, 600, 1600, 600, 600, 600, 600, 600, 600, 600, 600,
                        600, 600, 600, 1600, 600, 600, 600, 600, 600, 1600, 600, 1600, 600, 1600, 600, 1600,
                        600, 38923, 9000, 2233, 600};
                break;
            case R.id.btnSTBReturn:
                r = new int[]{
                        9000, 4500,
                        600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 1600, 600, 600,
                        600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600,
                        600, 600, 600, 1600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 1600, 600, 600,
                        600, 1600, 600, 600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 600, 600, 1600,
                        600, 38947, 9000, 2235, 600};
                break;
            case R.id.btnSTBBackspace:
                r = new int[]{
                        9000, 4500,
                        600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 1600, 600, 600,
                        600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600,
                        600, 600, 600, 1600, 600, 600, 600, 600, 600, 1600, 600, 600, 600, 600, 600, 600,
                        600, 1600, 600, 600, 600, 1600, 600, 1600, 600, 600, 600, 1600, 600, 1600, 600, 1600,
                        600, 38953, 9000, 2233, 600};
                break;
            case R.id.btnSTBLeft:
                r = new int[]{
                        9000, 4500,
                        600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 1600, 600, 600,
                        600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600,
                        600, 600, 600, 600, 600, 600, 600, 600, 600, 1600, 600, 600, 600, 600, 600, 600,
                        600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 600, 600, 1600, 600, 1600, 600, 1600,
                        600, 38922, 9000, 2230, 600};
                break;
            case R.id.btnSTBUp:
                r = new int[]{
                        9000, 4500,
                        600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 1600, 600, 600,
                        600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600,
                        600, 1600, 600, 1600, 600, 600, 600, 1600, 600, 600, 600, 600, 600, 600, 600, 600,
                        600, 600, 600, 600, 600, 1600, 600, 600, 600, 1600, 600, 1600, 600, 1600, 600, 1600,
                        600, 38946, 9000, 2233, 600};
                break;
            case R.id.btnSTBRight:
                r = new int[]{
                        9000, 4500,
                        600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 1600, 600, 600,
                        600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600,
                        600, 1600, 600, 600, 600, 600, 600, 600, 600, 1600, 600, 600, 600, 600, 600, 600,
                        600, 600, 600, 1600, 600, 1600, 600, 1600, 600, 600, 600, 1600, 600, 1600, 600, 1600,
                        600, 38959, 9000, 2229, 600};
                break;
            case R.id.btnSTBDown:
                r = new int[]{
                        9000, 4500,
                        600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 1600, 600, 600,
                        600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600,
                        600, 600, 600, 1600, 600, 1600, 600, 1600, 600, 600, 600, 600, 600, 600, 600, 600,
                        600, 1600, 600, 600, 600, 600, 600, 600, 600, 1600, 600, 1600, 600, 1600, 600, 1600,
                        600, 38968, 9000, 2233, 600};
                break;
            case R.id.btnSTBHomePage:
                r = new int[]{
                        9000, 4500,
                        600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 1600, 600, 600,
                        600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600,
                        600, 600, 600, 1600, 600, 600, 600, 1600, 600, 1600, 600, 600, 600, 600, 600, 600,
                        600, 1600, 600, 600, 600, 1600, 600, 600, 600, 600, 600, 1600, 600, 1600, 600, 1600,
                        600, 38959, 9000, 2234, 600};
                break;
            case R.id.btnSTBMenu:
                r = new int[]{
                        9000, 4500,
                        600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 1600, 600, 600,
                        600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600,
                        600, 1600, 600, 600, 600, 1600, 600, 600, 600, 600, 600, 600, 600, 1600, 600, 600,
                        600, 600, 600, 1600, 600, 600, 600, 1600, 600, 1600, 600, 1600, 600, 600, 600, 1600,
                        600, 38956, 9000, 2232, 600,};
                break;
            case R.id.btnSTBMouse:
                r = new int[]{
                        9000, 4500,
                        600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 600, 1600, 600, 600,
                        600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600, 600, 1600,
                        600, 600, 600, 600, 600, 1600, 600, 600, 600, 1600, 600, 600, 600, 1600, 600, 600,
                        600, 1600, 600, 1600, 600, 600, 600, 1600, 600, 600, 600, 1600, 600, 600, 600, 1600,
                        600, 38953, 9000, 2231, 600,};
                break;
            
            //endregion STB Button
        }
        
        return r;
    }
    
    
    DateTime mLastestTouchTime;
    Integer mBtnId;
    
    @OnTouch({R.id.btnTVPower, R.id.btnTVSoundMinus, R.id.btnTVSoundPlus, R.id.btnTVChangeSources, R.id.btnTVSilence, R.id.btnSTBPower, R.id.btnSTBSoundMinus, R.id.btnSTBSoundPlus,
            R.id.btnSTBOK, R.id.btnSTBReturn, R.id.btnSTBBackspace, R.id.btnSTBLeft, R.id.btnSTBUp, R.id.btnSTBRight, R.id.btnSTBDown, R.id.btnSTBHomePage, R.id.btnSTBMenu, R.id.btnSTBMouse})
    public boolean onViewTouched(View v, MotionEvent e)
    {
        if (mCIR == null || mCIR.hasIrEmitter() == false)
        {
            MessageBoxUtil.ShowExceptionDialog(MainActivity.this, "本设备未找到可用的红外发射器。");
            return true;
        }
    
        Button btn = (Button) v;
                
        if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_MOVE)
        {
            if(mBtnId == null)
            {
                mBtnId = new Integer(btn.getId());
                PlaySuccessVibrator();
            }
            
            if(mBtnId.equals(btn.getId()) == false)
            {
                return true;
            }
            
            DateTime now = DateTime.now();
            if (mLastestTouchTime != null)
            {
                DateTime a = new DateTime(mLastestTouchTime.getTimeInMillis());
                a.add(DateTime.MILLISECOND_FIELD, 300);
                if (a.after(now))
                {
                    return true;
                }
            }
            
            mFREQUENCY = Integer.valueOf(edtFrequency.getText().toString());
            int[] pattern = this.getPatterArrayByViewId(v);
            mCIR.transmit(mFREQUENCY, pattern);
            mLastestTouchTime = now;
            
        }
        else if (e.getAction() == MotionEvent.ACTION_UP)
        {
            if(mBtnId.equals(btn.getId()) == false)
            {
                return true;
            }
            
            mBtnId = null;
            mLastestTouchTime = null;
        }
        return true;
    }

    
    private Vibrator vibrator;
    
    /**
     * 震动
     */
    private long[] vibrator_pattern_success = {100, 400}; // 停止 开启 停止 开启
    private long[] vibrator_pattern_error = {100, 400, 100, 400, 100, 400, 100, 400};
    
    public void PlaySuccessVibrator()
    {
        vibrator.vibrate(vibrator_pattern_success, -1);
    }
    
    public void PlayErrorVibrator()
    {
        vibrator.vibrate(vibrator_pattern_error, -1);
    }
    
}
