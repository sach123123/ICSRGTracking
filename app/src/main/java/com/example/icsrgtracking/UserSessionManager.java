package com.example.icsrgtracking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;



import java.util.HashMap;


public class UserSessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREFER_NAME = "AndroidExamplePref";
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
    public static final String KEY_UserName = "userName";
    public static final String KEY__EMAIL = "userEmail";
    public static final String KEY_PROFILE = "userProfile";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_Fb_UID = "fb_uid";
    public static final String KEY_loginBy="login_by";
    public static final String KEY_quizType="quizTYpe";
    public static final String KEY_quizSubType="quizSubType";
    public static final String KEY_oppType="oppType";
    public static final String KEY_USER_LEVEL="1";
    public static final String KEY_OTHER_DUAL="";
    public static final String KEY_TROPHIES="";




    public UserSessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }







    public void createUserLoginSession(String userId, String username,String Fname,String emailId) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_UserName, username);
        editor.putString(KEY_FIRST_NAME,Fname);
        editor.putString(KEY__EMAIL,emailId);




        editor.commit();
    }

    public boolean signIn() {

        if (!this.isUserLoggedIn()) {
            Intent i = new Intent(_context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            return true;
        }
        return false;

    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
        user.put(KEY_UserName, pref.getString(KEY_UserName, null));
        user.put(KEY_FIRST_NAME, pref.getString(KEY_FIRST_NAME, null));
        user.put(KEY__EMAIL, pref.getString(KEY__EMAIL, null));




        return user;
    }



    public void logoutUser() {
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }
}
