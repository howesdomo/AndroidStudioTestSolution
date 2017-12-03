package com.enpot.utils;

/**
 * Created by Howe on 2017/4/2.
 */

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.howe.ir.R;

public class MessageBox extends Dialog
{
    public static final String MessageBoxStyle_Simple = "SIMPLE";
    public static final String MessageBoxStyle_Confirm = "CONFIRM";


    int dialogResult;
    Handler mHandler;

    public MessageBox(Activity context)
    {
        super(context);
        dialogResult = 0;
        setOwnerActivity(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        onCreate(MessageBox.MessageBoxStyle_Simple);
    }

    public MessageBox(Activity context, String style)
    {
        super(context);
        dialogResult = 0;
        setOwnerActivity(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        onCreate(style);
    }

    public void onCreate(String style)
    {
        // setContentView(R.layout.messagebox);
        switch (style)
        {
            case "SIMPLE":
                setContentView(R.layout.messagebox_singlebutton);
                break;
            case "CONFIRM":
                setContentView(R.layout.messagebox);
                break;
            default:
                setContentView(R.layout.messagebox_singlebutton);
                break;
        }

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View paramView)
            {
                endDialog(0);
            }
        });
        findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View paramView)
            {
                endDialog(1);
            }
        });
    }

    public int getDialogResult()
    {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult)
    {
        this.dialogResult = dialogResult;
    }

    public void endDialog(int result)
    {
        dismiss();
        setDialogResult(result);
        Message m = mHandler.obtainMessage();
        mHandler.sendMessage(m);
    }

    public int showDialog(String Msg, String Title)
    {
        TextView TvErrorInfo = (TextView) findViewById(R.id.textViewInfo);
        TvErrorInfo.setText(Msg);
        TvErrorInfo.setMovementMethod(ScrollingMovementMethod.getInstance());

        TextView TvTitle = (TextView) findViewById(R.id.textViewTitle);
        TvTitle.setText(Title);

        mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message mesg)
            {
                throw new RuntimeException();
            }
        };
        this.setCancelable(false); // 设置点击屏幕Dialog不消失
        super.show();
        try
        {
            Looper.getMainLooper();
            Looper.loop();
        }
        catch (RuntimeException e2)
        {
        }
        return dialogResult;
    }
}