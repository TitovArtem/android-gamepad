package org.example.virtualgamepad.data.managers;

import org.example.virtualgamepad.utils.TcpClient;

/**
 * Singleton for connection bindings.
 */
public class ConnectionManager {
    private static ConnectionManager sInstance;

    private TcpClient mTcpClient;

    private ConnectionManager() {}

    public static synchronized ConnectionManager getInstance() {
        if (sInstance == null) {
            sInstance = new ConnectionManager();
        }
        return sInstance;
    }

    public TcpClient getTcpClient() {
        return mTcpClient;
    }

    public void setTcpClient(TcpClient client) {
        if (client == null) {
            throw new NullPointerException("The given client is null.");
        }
        mTcpClient = client;
    }
}
