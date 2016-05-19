package com.example.thrymr.chatapplication;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;


/**
 * Created by thrymr on 10/9/15.
 */
public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    SharedPreferences tripStatusPref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    SharedPreferences.Editor tripStatusEditor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "LoginPref";

    private static final String TRIP_PREF_NAME = "TripPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";


    public static final String KEY_EMAIL = "email";


    public static final String CENTREUSER_ID = "centreUser_id";
    public static final String CENTRE_ID = "centre_id";

    public static final String MOBILE_LOGIN_AUTH_TOKEN = "mobile_login_auth_token";
    public static final String APP_USER_ID = "appUserId";

    public static final String TRIP_STATUS = "tripStatus";
    public static final String TRIP_ID = "tripId";

    public static final String CENTRE_USER = "centreUserName";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        tripStatusPref = _context.getSharedPreferences(TRIP_PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        tripStatusEditor = tripStatusPref.edit();

    }


    /**
     * Create login session
     */
    public void createLoginSession(final String success, final String email, final String appUserId, final String token) {
        editor.putString(IS_LOGIN, success);
        editor.putString(KEY_EMAIL, email);
        editor.putString(APP_USER_ID, appUserId);
        editor.putString(MOBILE_LOGIN_AUTH_TOKEN, token);
        editor.commit();
    }

    public void createTripStatus(String tripId, String tripStatus) {
        tripStatusEditor.putString(TRIP_STATUS, tripStatus);
        tripStatusEditor.putString(TRIP_ID, tripId);
        tripStatusEditor.commit();

    }


    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(MOBILE_LOGIN_AUTH_TOKEN, pref.getString(MOBILE_LOGIN_AUTH_TOKEN, null));
        user.put(APP_USER_ID, pref.getString(APP_USER_ID, null));
        return user;
    }


    /**
     * Clear session details
     */
    public void logoutUser() {
        editor.clear();
        editor.commit();


    }

    // Get Login State
    public String isLoggedIn() {
        return pref.getString(IS_LOGIN, "false");
    }


}
