package com.minapikke.kisslingo;

import android.app.Application;
import android.content.Context;

//The purpose of this class is to give access to the application context outside of Activity classes
public class GlobalApplication extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }
    public static  Context getAppContext(){
        return appContext;
    }
}
