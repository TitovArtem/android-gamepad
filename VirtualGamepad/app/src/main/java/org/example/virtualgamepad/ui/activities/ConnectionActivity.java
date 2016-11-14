package org.example.virtualgamepad.ui.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.example.virtualgamepad.R;
import org.example.virtualgamepad.data.managers.DataManager;
import org.example.virtualgamepad.utils.TcpClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Activity for connection to server.
 */
public class ConnectionActivity extends BaseActivity implements View.OnClickListener {

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
                connect();
        }
    }

    private void connect() {
        String port = mPortEt.getText().toString();
        String address = mAddressEt.getText().toString();
        new ConnectionTask().execute(address, port);
    }

    private class ConnectionTask extends AsyncTask<String, Void, Void> {
        private TcpClient mTcpClient;

        @Override
        protected void onPreExecute() {
            showToast(getString(R.string.conn_connecting_msg));
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mTcpClient != null && mTcpClient.isConnected()) {
                showToast(getString(R.string.conn_success_connection));
                DataManager.getInstance().setTcpClient(mTcpClient);
                Intent intent = new Intent(ConnectionActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                showToast(getString(R.string.conn_connect_err));
            }
            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                String host = strings[0];
                int port = Integer.parseInt(strings[1]);
                mTcpClient = new TcpClient(InetAddress.getByName(host), port);

                // Try to connect
                mTcpClient.run();
            } catch (UnknownHostException exc) {    // TODO: invalid call showError
                showError(getString(R.string.conn_invalid_host_err), exc);
            } catch (NumberFormatException exc) {   // TODO: invalid call showError
                showError(getString(R.string.conn_invalid_port_err), exc);
            }
            return null;
        }
    }
}
