package com.enpot.web;

import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;
import java.util.List;

/**
 * Created by Howe on 2017/6/8.
 */
public class KvmStringList extends BaseKvmList<String>
{
    
    public KvmStringList(String nameSpace, List<String> list)
    {
        super(nameSpace, list);
    }
    
    @Override
    public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2)
    {
        arg2.namespace = this.nameSpace;
        arg2.name = "string";
    }
}