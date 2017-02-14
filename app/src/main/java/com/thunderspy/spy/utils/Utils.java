package com.thunderspy.spy.utils;


import android.content.Context;
import android.util.Log;

import java.io.InputStream;
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

    public static X509Certificate getServerActualCertificate(Context context) {
        X509Certificate certificate = null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            InputStream is = context.getAssets().open("cert/public-cert.pem");
            certificate = (X509Certificate)certificateFactory.generateCertificate(is);
            try {
                is.close();
            } catch (Exception exp) {
                Utils.log("Error in closing cert file stream: %s", exp.getMessage());
            }
        } catch (Exception exp) {
            certificate = null;
        }
        return certificate;
    }




}

















