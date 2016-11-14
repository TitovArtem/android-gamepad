package org.example.virtualgamepad.utils;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Client with TCP connection based on socket.
 */
public class TcpClient implements Runnable {

    private static final String TAG = "TCP Client";

    private PrintWriter mOut;
    private Socket mSocket;
    private final InetAddress mAddress;
    private final int mPort;
    private int mConnectionTimeout = 5000;
    public TcpClient(InetAddress address, int port) {
        if (address == null) {
            throw new NullPointerException("The given address is null.");
        }
        mAddress = address;
        mPort = port;

    }

    public TcpClient(InetAddress address, int port, int connectionTimeout) {
        this(address, port);

        if (connectionTimeout < 0) {
            throw new IllegalArgumentException("The given value of connection " +
                    "timeout must be non negative.");
        }
        mConnectionTimeout = connectionTimeout;
    }

    public int getConnectionTimeout() {
        return mConnectionTimeout;
    }

    public void setConnectionTimeout(int timeout) {
        if (timeout < 0) {
            throw new IllegalArgumentException("The given value of connection " +
                    "timeout must be non negative.");
        }
        mConnectionTimeout = timeout;
    }


    public InetAddress getAddress() {
        return mAddress;
    }

    public int getPort() {
        return mPort;
    }

    /** Stops connection. */
    public void stop() {
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException exc) {
                Log.e(TAG, exc.getMessage());
            }
        }
        if (mOut != null) {
            mOut.close();
        }
    }

    /** Sends message to server. */
    public void sendMessage(String message) {
        if (mOut != null && !mOut.checkError()) {
            mOut.println(message);
            mOut.flush();
        }
    }

    public boolean isConnected() {
        return mSocket.isConnected();
    }

    @Override
    public void run() {
        try {
            mSocket = new Socket();
            mSocket.connect(new InetSocketAddress(mAddress, mPort), mConnectionTimeout);
            mOut = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(mSocket.getOutputStream())));
        } catch (IOException exc) {
            Log.e(TAG, String.valueOf(exc));
        }
    }
}
