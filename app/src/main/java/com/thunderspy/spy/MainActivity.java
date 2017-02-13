package com.thunderspy.spy;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {


                    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                    final X509Certificate certificate = (X509Certificate)certificateFactory.generateCertificate(getAssets().open("public-cert.pem"));


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

                    int r;
                    while ((r=is.read()) != -1) {
                        final int y = r;
                        new Handler(getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                tv.append((char)y + "");
                            }
                        });
                    }

                    while (true) {
                        os.write("lkn".getBytes());
                        Thread.sleep(8000);
                    }




                } catch (Exception exp) {
                    Log.d("APP", exp.getMessage());
                }
            }
        }).start();




    }
}
