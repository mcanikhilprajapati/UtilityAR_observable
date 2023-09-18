package com.utility.app;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {


    public static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    private static SessionManager ourInstance;
    private static Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "uar";
    private static final String IS_LOGIN = "IsLoggedIn";


    public static final String JWT="jwt";
    public static final String COMMUNICATIONUSERID="communicationUserId";
    public static final String CHATTHREAD_ID="chatThreadId";
    public static final String GROUPCALL_ID="groupCallId";
    public static final String EMAIL="email";
    public static final String TOKEN ="token" ;
    public static final String SCREEN_MODE ="screenmode" ;

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static SessionManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new SessionManager(_context);
        }
        return ourInstance;
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void setValue(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }
    public void setBoolen(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }
    public void logout() {
        editor.remove(JWT);
        editor.remove(COMMUNICATIONUSERID);
        editor.remove(GROUPCALL_ID);
        editor.remove(CHATTHREAD_ID);
        editor.commit();
    }

}
