package org.example.virtualgamepad.data.managers;

import org.example.virtualgamepad.utils.TcpClient;

/**
 * Singleton for accessing to data and connection bindings.
 */
public class DataManager {
    private static DataManager sInstance;

    private TcpClient mTcpClient;

    public DataManager(TcpClient client) {
        if (client == null) {
            throw new NullPointerException("The given client is null.");
        }
        mTcpClient = client;
    }

    public DataManager() {}

    public static synchronized DataManager getInstance() {
        if (sInstance == null) {
            sInstance = new DataManager();
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
