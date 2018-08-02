package com.example.admin.liftapp.Controller;
import android.app.Application;
import android.content.Context;

/**
 * Created by adima on 02/03/2018.
 */

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }


    public static Context getMyContext(){
        return context;
    }





}
