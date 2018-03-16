package com.example.arjunpatidar.androidexample;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;

/**
 * Created by arjunpatidar on 16/03/18.
 */

public class PreferenceManager {
    private Context context;
    private SharedPreferences sharedPreferences;

    PreferenceManager(Context context){
        this.context=context;
        getSharedPreference();
    }


    private  void getSharedPreference(){

     sharedPreferences = context.getSharedPreferences(context.getString(R.string.my_preference),Context.MODE_PRIVATE);

    }

    public  void writePreference(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.my_preference_key),"INIK_OK");
        editor.commit();
    }

    public boolean checkPreference() {
        boolean status = false;
        if (sharedPreferences.getString(context.getString(R.string.my_preference_key), "null").equals("null")) {
            status = false;
        } else {
           status =true;
        }
        return status;
    }

    public void clearPreference(){

        sharedPreferences.edit().clear().commit();
    }
}
