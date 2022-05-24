package com.example.new_qr;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    private static final String PREF_NAME = "SESSION";
    private static final String SAHADATNAMA = "SAHADATNAMA";
    private static final String GUWA_HAT = "GUWA_HAT";
    private static final String GURAT_HAT = "GURAT_HAT";

    public Session(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void createSahadatnama(String number){
        editor = sharedPreferences.edit();
        editor.putString(SAHADATNAMA, number);
        editor.commit();
    }

    public void createGuwaHat(String guwa){
        editor = sharedPreferences.edit();
        editor.putString(GUWA_HAT, guwa);
        editor.commit();
    }

    public void creatGuratHat(String gurat){
        editor = sharedPreferences.edit();
        editor.putString(GURAT_HAT, gurat);
        editor.commit();
    }

    public String getSahadatnama(){
        return sharedPreferences.getString(SAHADATNAMA, "");
    }
    public String getGuwaHat(){
        return sharedPreferences.getString(GUWA_HAT, "");
    }
    public String getGuratHat(){
        return sharedPreferences.getString(GURAT_HAT, "");
    }
}
