package cn.com.enpot.cncomenpotdemo.component;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Howe on 2017/7/26.
 */

/**
 * 跑马灯效果的TextView, 使用方式：
 * 启动/关闭：{@link #setMarqueeEnable(boolean)}
 * xml文件中记得设置：android:focusable="true", android:singleLine="true"
 * <p>
 * Created by dasu on 2017/3/21.
 * http://www.jianshu.com/u/bb52a2918096
 */
public class MarqueeTextView extends TextView
{
    private boolean isMarqueeEnable = false;
    
    public MarqueeTextView(Context context)
    {
        super(context);
    }
    
    public MarqueeTextView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }
    
    public MarqueeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }
    
    public void setMarqueeEnable(boolean enable)
    {
        if (isMarqueeEnable != enable)
        {
            isMarqueeEnable = enable;
            if (enable)
            {
                setEllipsize(TextUtils.TruncateAt.MARQUEE);
            }
            else
            {
                setEllipsize(TextUtils.TruncateAt.END);
            }
            // onWindowFocusChanged(enable);
        }
    }
    
    public boolean isMarqueeEnable()
    {
        return isMarqueeEnable;
    }
    
    @Override
    public boolean isFocused() // 丢失焦点后仍然执行走马灯效果关键代码
    {
        return isMarqueeEnable;
    }
    
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect)
    {
        super.onFocusChanged(isMarqueeEnable, direction, previouslyFocusedRect);
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus)
    {
        super.onWindowFocusChanged(isMarqueeEnable);
    }
    
    
}
