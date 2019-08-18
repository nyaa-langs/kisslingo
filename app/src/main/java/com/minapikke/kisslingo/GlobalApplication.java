package com.minapikke.kisslingo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

//The purpose of this class is to give access to the application context outside of Activity classes
public class GlobalApplication extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    public static void LoadActivity(Class pTargetActivity){
        appContext.startActivity(new Intent(appContext, pTargetActivity));
    }

    public static  Context getAppContext(){
        return appContext;
    }
}
