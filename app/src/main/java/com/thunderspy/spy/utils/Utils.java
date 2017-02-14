package com.thunderspy.spy.utils;


import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Created by ariyan on 2/11/17.
 */

public final class Utils {

    public static void log(Object format, Object... args) {
        try {
            format = (format == null) ? ("null") : (format);
            Log.d(Constants.LOGCAT_TAG, String.format(format.toString(), args));
        } catch (Exception exp) {
            Utils.log(exp.getMessage());
        }
    }

    public static X509Certificate getServerActualCertificate() {
        X509Certificate certificate = null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            InputStream is = Utils.getServerActualCertificateInputStream();
            certificate = (X509Certificate)certificateFactory.generateCertificate(is);
            try {
                is.close();
            } catch (Exception exp) {
                Utils.log("Error in closing cert file stream: %s", exp.getMessage());
            }
        } catch (Exception exp) {
            Utils.log("Invalid certificate is read: %s", exp.getMessage());
            certificate = null;
        }
        return certificate;
    }

    public static InputStream getServerActualCertificateInputStream() {
        final StringReader sr = new StringReader(Constants.SERVER_ACTUAL_CERTIFICATE_IN_STR);
        return new InputStream() {
            @Override
            public int read() throws IOException {
                return sr.read();
            }

            @Override
            public void close() throws IOException {
                super.close();
                sr.close();
            }
        };
    }


}

















