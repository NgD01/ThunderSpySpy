package com.thunderspy.spy.utils;

import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.SSLSocket;

/**
 * Created by ariyan on 2/14/17.
 */

public final class SocketsHolder {

    private static Set<Socket> sockets = Collections.synchronizedSet(new HashSet<Socket>());

    public static void addSocket(Socket socket) {
        try {
            sockets.add(socket);
        } catch (Exception exp) {
            Utils.log("Error in adding socket to SocketsHolder: %s", exp.getMessage());
        }
    }

    public static void removeSocket(Socket socket) {
        try {
            sockets.remove(socket);
        } catch (Exception exp) {
            Utils.log("Error in removing socket from SocketsHolder: %s", exp.getMessage());
        }
    }

    public static Set<Socket> getAllSockets() {
        return sockets;
    }

}
