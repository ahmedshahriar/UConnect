package com.sakib.uconnect.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;


import com.sakib.uconnect.auth.LoginActivity;

import java.util.HashMap;


/**
 * The type Session manager.
 */
public class SessionManager {

    /**
     * The constant KEY_ID.
     */
// User id (make variable public to access from outside)
    public static final String KEY_ID = "user_id";
    /**
     * The constant KEY_MOBILE.
     */
// mobile number (make variable public to access from outside)
    public static final String KEY_MOBILE = "mobile_number";


    /**
     * User details
     */

    //Name
    public static final String NAME = "name";



    // SharedPref file name
    private static final String PREF_NAME = "UConnectPref";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    // All Shared Preferences Keys
    private static final String IS_LOGOUT = "IsLoggedOut";
    // For first time starting the app
    private static final String IS_FIRST = "IsFirst";

    /**
     * The Pref.
     */
// Shared Preferences
    SharedPreferences pref;
    /**
     * The Editor.
     */
// Editor for Shared preferences
    SharedPreferences.Editor editor;
    /**
     * The Context.
     */
// Context
    Context _context;
    /**
     * The Private mode.
     */
// Shared pref mode
    int PRIVATE_MODE = 0;


    /**
     * Instantiates a new Session manager.
     *
     * @param context the context
     */
// Constructor
    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     *
     * @param id     the id
     * @param mobile the mobile
     * @param name    the name
     */
    public void createLoginSession(String id, String mobile, String name) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing login value as TRUE
        editor.putBoolean(IS_LOGOUT, false);

        // Storing id in pref
        editor.putString(KEY_ID, id);

        // Storing mobile in pref
        editor.putString(KEY_MOBILE, mobile);

        // Storing pin in pref
        editor.putString(NAME, name);

        // commit changes
        editor.commit();
    }

    /**
     * Create first time on board session
     */
    public void createFirstSession() {
        // Storing login value as TRUE
        editor.putBoolean(IS_FIRST, true);

        // commit changes
        editor.commit();
    }


    /**
     * Update User details
     *
     * @param name the first name
     */
    public void updateUserDetails(String name) {

        // Storing first name in pref
        editor.putString(NAME, name);


        // commit changes
        editor.commit();

    }




    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            Log.d("Logged In status", "checkLogin: no");
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }


    /**
     * Get stored session data
     *
     * @return the user details
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        // user id
        user.put(KEY_ID, pref.getString(KEY_ID, null));

        // user mobile number
        user.put(KEY_MOBILE, pref.getString(KEY_MOBILE, null));

        // user name
        user.put(NAME, pref.getString(NAME, null));


        // return user
        return user;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();


        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);


    }


    /**
     * Quick check for login
     *
     * @return the boolean
     */
// Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }


    /**
     * Quick check for first time set up
     *
     * @return the boolean
     */
// Get check for first time setup
    public boolean isFirst() {
        return pref.getBoolean(IS_FIRST, false);
    }


}
