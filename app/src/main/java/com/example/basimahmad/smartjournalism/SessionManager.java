package com.example.basimahmad.smartjournalism;

/**
 * Created by Basim Ahmad on 11/6/2017.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "SmartJournalismLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    private static final String KEY_USERID = "userid";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setUserID(int id) {

        editor.putInt(KEY_USERID, id);
        // commit changes
        editor.commit();

        Log.d(TAG, "User id set: !"+id+" pref: +"+pref.getInt(KEY_USERID, 0));
    }

    public int getUserID() {
        Log.d(TAG, "User id returned: "+pref.getInt(KEY_USERID, 0));
        return pref.getInt(KEY_USERID, 0);
    }

    public void removeUserID() {
        Log.d(TAG, "User login session modified!");
        editor.remove(KEY_USERID);
        editor.commit();
    }


    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
}