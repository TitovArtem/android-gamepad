package org.example.virtualgamepad.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

/**
 * Base activity. It contains basic components to show errors and messages.
 */
public class BaseActivity extends AppCompatActivity {

    static final String TAG = "BaseActivity";  // TODO: TAG_PREFIX

    /** Shows toast as {@link #showToast(String)} with message and logs exception message. */
    public void showError(String message, Exception error) {
        showToast(message);
        Log.e(TAG, String.valueOf(error));
    }

    /** Shows toast with long length ({@link android.widget.Toast#LENGTH_LONG}). */
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
