package com.enpot.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Howe on 2017/6/13.
 */

public class User implements Serializable
{
    public User()
    {
        
    }
    
    public String ID;
    public String Account;
    public String Password;
    public String UserName;
    public String CompanyCode;
    public ArrayList<Permission> PermissionList;
}