package org.example.virtualgamepad.ui.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.example.virtualgamepad.R;
import org.example.virtualgamepad.data.managers.DataManager;
import org.example.virtualgamepad.utils.NetworkStatusChecker;
import org.example.virtualgamepad.utils.TcpClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Activity for connection to server.
 */
public class ConnectionActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ConnectionActivity";

    @BindView(R.id.conn_address_et) EditText mAddressEt;
    @BindView(R.id.conn_port_id) EditText mPortEt;
    @BindView(R.id.connect_btn) Button mConnectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        ButterKnife.bind(this);

        mConnectBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.connect_btn:
                if (NetworkStatusChecker.isAvailable(this) == false) {
                    showToast(getString(R.string.conn_network_is_unreachable));
                } else {
                    connect();
                }
                break;
        }
    }

    private void connect() {
        String port = mPortEt.getText().toString();
        String address = mAddressEt.getText().toString();
        new ConnectionTask().execute(address, port);
    }

    /* Class for async connection to server. */
    private class ConnectionTask extends AsyncTask<String, Void, Boolean> {
        private TcpClient mTcpClient;

        @Override
        protected void onPreExecute() {
            showToast(getString(R.string.conn_connecting_msg));
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (mTcpClient != null && mTcpClient.isConnected()) {
                DataManager.getInstance().setTcpClient(mTcpClient);
                Intent intent = new Intent(ConnectionActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (!result) {
                showToast(getString(R.string.conn_connect_err));
            }
            super.onPostExecute(result);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            boolean isInvalidInput = false;
            try {
                String host = strings[0];
                int port = Integer.parseInt(strings[1]);
                mTcpClient = new TcpClient(InetAddress.getByName(host), port, 10000);

                // Try to connect
                mTcpClient.run();
            } catch (UnknownHostException exc) {
                showError(getString(R.string.conn_invalid_host_err), exc);
                isInvalidInput = true;
            } catch (NumberFormatException exc) {
                showError(getString(R.string.conn_invalid_port_err), exc);
                isInvalidInput = true;
            }
            return isInvalidInput;
        }

        private void showError(final String message, Exception exc) {
            Log.e(TAG, String.valueOf(exc));
            new Handler(ConnectionActivity.this.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(ConnectionActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
