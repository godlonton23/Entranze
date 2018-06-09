package com.godlontonconsulting.entranze.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;


public class PrefManager {
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Shared preferences file name
    private static final String PREF_NAME = "Entranze";
    private static final String KEY_MOBILE_NUMBER = "mobile_number";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_IS_LOGGED_IN_REG = "isIn";
    private static final String PUSH_NOTIFICATIONS = "isSet";
    private static final String KEY_NAME = "name";
    private static final String KEY_SURNAME = "surname";
    private static final String KEY_MOBILE = "msisdn";
    private static final String KEY_LOGIN_PIN = "pin";
    private static final String KEY_TOKEN = "accessToken";
    private static final String KEY_REFRESH_TOKEN = "refreshToken";
    private static final String KEY_FAV_LIST = "FavsList";
    private static final String KEY_TICKET_LIST = "TicketList";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setMobileNumber(String mobileNumber) {
        editor.putString(KEY_MOBILE_NUMBER, mobileNumber);
        editor.commit();
    }

    public void setLoginPin(String pin) {
        editor.putString(KEY_LOGIN_PIN, pin);
        editor.commit();
    }

    public String getLoginPin() {
        return pref.getString(KEY_LOGIN_PIN, null);
    }

    public void logOutReg() {
        editor.putBoolean(KEY_IS_LOGGED_IN_REG, false);
        editor.commit();
    }

    public boolean isLogReg() {
        return pref.getBoolean(KEY_IS_LOGGED_IN_REG, false);
    }

    public void logInReg() {
        editor.putBoolean(KEY_IS_LOGGED_IN_REG, true);
        editor.commit();
    }

    public String getMobileNumber() {
        return pref.getString(KEY_MOBILE_NUMBER, null);
    }

    public void createActivateUser(String name, String surname, String mobile) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_SURNAME, surname);
        editor.putString(KEY_MOBILE, mobile);
        editor.commit();
    }

    public String getToken() {
        return pref.getString(KEY_TOKEN, null);
    }

    public String getRefreshToken() {
        return pref.getString(KEY_REFRESH_TOKEN, null);
    }

    public String getMobile() {
        return pref.getString(KEY_MOBILE, null);
    }

    public void createToken(String accessToken,String refreshToken) {
        editor.putString(KEY_TOKEN,accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.commit();
    }

    public void logIn(){
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }

    public void turnPushOn(){
        editor.putBoolean(PUSH_NOTIFICATIONS, true);
        editor.commit();
    }

    public void turnPushOff(){
        editor.putBoolean(PUSH_NOTIFICATIONS, false);
        editor.commit();
    }

    public boolean getPush() {
        return pref.getBoolean(PUSH_NOTIFICATIONS, false);
    }

    public void createAccessToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public String getFavs() {
        return pref.getString(KEY_FAV_LIST, null);
    }

    public void createFavsList(String favList) {
        editor.putString(KEY_FAV_LIST, favList);
        editor.commit();
    }

    public String getTickets() {
        return pref.getString(KEY_TICKET_LIST, null);
    }

    public void createTicketList(String favList) {
        editor.putString(KEY_TICKET_LIST, favList);
        editor.commit();
    }

    public void createRefreshToken(String token) {
        editor.putString(KEY_REFRESH_TOKEN, token);
        editor.commit();
    }


    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> profile = new HashMap<>();
        profile.put("name", pref.getString(KEY_NAME, null));
        profile.put("surname", pref.getString(KEY_SURNAME, null));
        profile.put("msisdn", pref.getString(KEY_MOBILE, null));
        return profile;
    }
}
