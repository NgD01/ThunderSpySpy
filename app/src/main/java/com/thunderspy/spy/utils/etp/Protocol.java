package com.thunderspy.spy.utils.etp;

import com.thunderspy.spy.utils.Constants;
import com.thunderspy.spy.utils.ThreadPoolManager;
import com.thunderspy.spy.utils.Utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import javax.net.ssl.SSLSocket;

/**
 * ETP stands for Event Transfer Protocol
 *
 * Created by ariyan on 2/14/17.
 */

public final class Protocol {

    public static void generateEvent(SSLSocket sslSocket) {
        InputStream is = null;
        OutputStream os = null;
        try {

            is = sslSocket.getInputStream();
            os = sslSocket.getOutputStream();

            ETPEventGenerator eventGenerator = new ETPEventGenerator(sslSocket);
            byte[] buffer = new byte[Constants.SOCKET_STREAM_BUFFER_SIZE];
            int bufferBytesRead;
            boolean dataProcessed;
            while ((bufferBytesRead = is.read(buffer)) != -1) {
                dataProcessed = eventGenerator.processData(buffer, 0, bufferBytesRead);
                if(!dataProcessed) {
                    throw new Exception("Invalid protocol handshaking");
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














