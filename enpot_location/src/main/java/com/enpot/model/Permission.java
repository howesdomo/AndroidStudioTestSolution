package com.enpot.model;

/**
 * Created by Howe on 2017/6/13.
 */

public class Permission
{
    public Permission()
    {
        
    }
    
    public Permission(int iconId)
    {
        IconId = iconId;
    }
    
    public Integer IconId;
    public String ID;
    public String Code;
    public String Name;
    public String ClassName;
    
    public Integer Seq;
    
    public Integer getSeq()
    {
        return Seq;
    }
    
    // 所属功能组别
    public String ModuleID;
    public String ModuleName;
}
