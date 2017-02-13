package com.thunderspy.spy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MonitorService extends Service {
    public MonitorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
