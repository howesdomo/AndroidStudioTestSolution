package cn.com.enpot.cncomenpotdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.com.enpot.cncomenpotdemo.R;
import cn.com.enpot.cncomenpotdemo.component.MarqueeTextView;

/**
 * Created by Howe on 2017/7/25.
 */

public class MarqueeTest_V1_Activity extends Activity
{
    public final String TAG = "MarqueeTest_V1_Activity";
    
    TextView lblA;
    MarqueeTextView lblB;
    EditText txtB;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marqueetest_v1);
        initView();
    }
    
    private void initView()
    {
        lblA = (TextView) findViewById(R.id.lblA);
        lblB = (MarqueeTextView) findViewById(R.id.lblB);
        lblB.setMarqueeEnable(true);
        
//        lblA = (TextView)findViewById(R.id.txtB);
    }
}
