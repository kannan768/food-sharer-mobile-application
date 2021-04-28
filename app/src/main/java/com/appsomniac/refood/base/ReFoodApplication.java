package com.appsomniac.refood.base;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;
public class ReFoodApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
