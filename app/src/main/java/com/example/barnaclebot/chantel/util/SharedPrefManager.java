package com.example.barnaclebot.chantel.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    public static final String CHANTEL_APP = "ChantelApp";

    public static final String USERNAME = "Username";
    //public static final String PASSWORD = "Password";
    public static final String TOKEN = "Token";

    public static final String SP_SUDAH_LOGIN = "spSudahLogin";

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    public SharedPrefManager(Context context){
        sp = context.getSharedPreferences(CHANTEL_APP, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void saveSPString(String keySP, String value){
        spEditor.putString(keySP, value);
        spEditor.commit();
    }

    public void saveSPInt(String keySP, int value){
        spEditor.putInt(keySP, value);
        spEditor.commit();
    }

    public void saveSPBoolean(String keySP, boolean value){
        spEditor.putBoolean(keySP, value);
        spEditor.commit();
    }

    public String getToken(){
        return sp.getString(TOKEN, "");
    }

    public String getUsername(){
        return sp.getString(USERNAME, "");
    }

   /* public String getPassword(){
        return sp.getString(PASSWORD, "");
    }*/

    public Boolean getSPSudahLogin(){
        return sp.getBoolean(SP_SUDAH_LOGIN, false);
    }
}
