package cn.com.enpot.cncomenpotdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Howe on 2017/7/16.
 */

public class CarAdapter extends BaseAdapter
{
    Context mContext;
    List<Car> mList;
    
    public CarAdapter(Context c, List<Car> l)
    {
        mContext = c;
        mList = l;
    }
    
    @Override
    public int getCount()
    {
        return 0;
    }
    
    @Override
    public Object getItem(int position)
    {
        return null;
    }
    
    @Override
    public long getItemId(int position)
    {
        return 0;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.lvi_car, null);
            
            viewHolder.lblIndex = (TextView) convertView.findViewById(R.id.lblIndex);
            viewHolder.lblName = (TextView) convertView.findViewById(R.id.lblName);
            
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
        Car match = mList.get(position);
        viewHolder.lblIndex.setText(match.getIndex());
        viewHolder.lblName.setText(match.Name);
        
        return convertView;
    }
    
    final class ViewHolder
    {
        public TextView lblIndex;
        public TextView lblName;
    }
}
