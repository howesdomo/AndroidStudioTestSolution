package cn.com.enpot.enpot_location;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.enpot.commons.EnpotApplication;
import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;

public class MainActivity extends AppCompatActivity
{
    
    @Bind(R.id.btnStartService)
    Button btnStartService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initEvent();
    }
    
    private void initEvent()
    {
        this.btnStartService.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LocationMonitorService service = ((EnpotApplication) getApplication()).locationMonitorService;
                Intent i = new Intent();
                service.onStartCommand(i, Service.START_FLAG_REDELIVERY, 1);
            }
        });

//        RxView.clicks(this.btnStartService)
//                .throttleFirst(1, TimeUnit.SECONDS)
//                .subscribe(new Subscriber<Void>()
//                {
//                    @Override
//                    public void onCompleted()
//                    {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e)
//                    {
//
//                    }
//
//                    @Override
//                    public void onNext(Void aVoid)
//                    {
//
//                    }
//                });
        
    }
    
    
}
