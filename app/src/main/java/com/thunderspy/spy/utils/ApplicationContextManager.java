package com.thunderspy.spy.utils;

import android.content.Context;

/**
 * Created by ariyan on 2/15/17.
 */

public final class ApplicationContextManager {

    private static Context applicationContext = null;

    public static void initApplicationContext(Context context) {
        if(applicationContext == null) {
            applicationContext = context;
        }
    }

    public static Context getApplicationContext() {
        return applicationContext;
    }
}
