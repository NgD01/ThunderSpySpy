package com.thunderspy.spy.utils.etp;

import java.util.HashMap;

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
        }
    }

}























