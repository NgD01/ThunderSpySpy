package com.thunderspy.spy;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.thunderspy.spy.utils.SocketsHolder;
import com.thunderspy.spy.utils.ThreadPoolManager;
import com.thunderspy.spy.utils.Utils;
import com.thunderspy.spy.utils.etp.ETPThread;

import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class WorkerService extends Service {
    private final int START_MODE = START_STICKY;

    int x = 0;
    public WorkerService() {}

    @Override
    public void onCreate() {
        ThreadPoolManager.setupThreadPool();
        ETPThread.setupEtpThread();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ThreadPoolManager.setupThreadPool();
        ETPThread.setupEtpThread();
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
