package com.thunderspy.spy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WorkerService extends Service {
    public WorkerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
