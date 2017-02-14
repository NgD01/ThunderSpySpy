package com.thunderspy.spy.utils.etp;


import java.util.HashMap;
import javax.net.ssl.SSLSocket;

/**
 * Created by ariyan on 2/14/17.
 */

public interface EventCallback {
    public void run(SSLSocket socket, HashMap<String, String> headers, byte[] data);
}
