package com.thunderspy.spy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WorkerService extends Service {
    private final int START_MODE = START_STICKY;

    public WorkerService() {}

    @Override
    public void onCreate() {

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
