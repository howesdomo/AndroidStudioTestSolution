package howe.com.howestimer;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity
{
    @Bind(R.id.llControl)
    LinearLayout llControl;
    @Bind(R.id.txtTimeA)
    EditText txtTimeA;
    @Bind(R.id.txtTimeB)
    EditText txtTimeB;
    @Bind(R.id.switchPlaySound)
    Switch switchPlaySound;
    @Bind(R.id.switchVibrate)
    Switch switchVibrate;
    @Bind(R.id.switchScreenAlwaysOn)
    Switch switchScreenAlwaysOn;
    @Bind(R.id.btnStart)
    Button btnStart;
    
    @Bind(R.id.btnModeProtectEyes)
    Button btnModeProtectEyes;
    @Bind(R.id.btnModeKeepFit)
    Button btnModeKeepFit;
    
    @Bind(R.id.llMain)
    LinearLayout llMain;
    @Bind(R.id.lblMain_Count)
    TextView lblMainCount;
    @Bind(R.id.lblMain_Content)
    TextView lblMainContent;
    @Bind(R.id.btnBack)
    Button btnBack;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initStaticInfo();
        initEvent();
    }
    
    //region 播放声音
    
    private static HashMap<Integer, Integer> defaultSoundMap;
    private static SoundPool soundPool;
    
    public static void PlayBeepSound()
    {
        try
        {
            soundPool.play(defaultSoundMap.get(1), 1, 1, 0, 0, 1);
        }
        catch (Exception ex)
        {
            
        }
    }
    
    public static void PlayErrorSound()
    {
        try
        {
            soundPool.play(defaultSoundMap.get(2), 1, 1, 0, 0, 1);
        }
        catch (Exception ex)
        {
            
        }
    }
    
    //endregion
    
    //region 震动
    
    private static Vibrator vibrator;
    
    /**
     * 震动
     */
    private static long[] vibrator_pattern_success = {100, 400}; // 停止 开启 停止 开启
    private static long[] vibrator_pattern_error = {100, 400, 100, 400, 100, 400, 100, 400};
    
    public static void PlaySuccessVibrator()
    {
        vibrator.vibrate(vibrator_pattern_success, -1);
    }
    
    public static void PlayErrorVibrator()
    {
        vibrator.vibrate(vibrator_pattern_error, -1);
    }
    
    //endregion
    
    private void initStaticInfo()
    {
        //region 加载本程序所需使用的所有音效文件
        
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        defaultSoundMap = new HashMap<Integer, Integer>();
        defaultSoundMap.put(1, soundPool.load(this, R.raw.beep, 1));
        defaultSoundMap.put(2, soundPool.load(this, R.raw.error, 1));
        
        //endregion
        
        //region 加载震动设置
        
        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        
        //endregion
    }
    
    private void initEvent()
    {
        RxView.clicks(this.btnStart)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>()
                {
                    @Override
                    public void call(Void aVoid)
                    {
                        mWhileBoolean = true;
                        mCount = 0;
                        llControl.setVisibility(View.GONE);
                        
                        mTimeA_SleepTime = Integer.valueOf(txtTimeA.getText().toString()) * 1000;
                        mTimeB_SleepTime = Integer.valueOf(txtTimeB.getText().toString()) * 1000;
                        
                        mIsPlaySound = switchPlaySound.isChecked();
                        mIsVibrate = switchVibrate.isChecked();
                        
                        method();
                    }
                });
        
        RxView.clicks(this.btnBack)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>()
                {
                    @Override
                    public void call(Void aVoid)
                    {
                        mWhileBoolean = false;
                        llControl.setVisibility(View.VISIBLE);
                    }
                });
        
        RxView.clicks(this.btnModeProtectEyes)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>()
                {
                    @Override
                    public void call(Void aVoid)
                    {
                        txtTimeA.setText("25");
                        txtTimeB.setText("35");
                        switchVibrate.setChecked(true);
                    }
                });
        
        RxView.clicks(this.btnModeKeepFit)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>()
                {
                    @Override
                    public void call(Void aVoid)
                    {
                        txtTimeA.setText("3");
                        txtTimeB.setText("7");
                        switchVibrate.setChecked(true);
                    }
                });
    }
    
    // 循环次数统计
    private Integer mCount = 0;
    // 跳出循环
    private Boolean mWhileBoolean = true;
    
    private Integer mTimeA_SleepTime = 3000;
    private Integer mTimeB_SleepTime = 3000;
    
    private Boolean mIsPlaySound = false;
    private Boolean mIsVibrate = false;
    
    private void method()
    {
        Thread nT = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    while (mWhileBoolean)
                    {
                        runOnUiThread(Runnable_A);
                        Thread.sleep(mTimeA_SleepTime);
                        runOnUiThread(Runnable_B);
                        Thread.sleep(mTimeB_SleepTime);
                    }
                }
                catch (Exception ex)
                {
                    
                }
            }
        });
        
        nT.start();
    }
    
    private Runnable Runnable_A = new Runnable()
    {
        @Override
        public void run()
        {
            mCount = mCount + 1;
            lblMainCount.setText(mCount.toString());
            lblMainContent.setText("TimeA");
            llMain.setBackgroundColor(Color.argb(255, 136, 186, 83));
            if (mWhileBoolean == true && mIsPlaySound == true)
            {
                PlayBeepSound();
            }
            
            if (mWhileBoolean == true && mIsVibrate == true)
            {
                PlaySuccessVibrator();
            }
        }
    };
    
    private Runnable Runnable_B = new Runnable()
    {
        @Override
        public void run()
        {
            lblMainContent.setText("TimeB");
            llMain.setBackgroundColor(Color.argb(255, 245, 205, 25));
            if (mWhileBoolean == true && mIsPlaySound == true)
            {
                PlayBeepSound();
            }
            
            if (mWhileBoolean == true && mIsVibrate == true)
            {
                PlaySuccessVibrator();
            }
        }
    };
    
}
