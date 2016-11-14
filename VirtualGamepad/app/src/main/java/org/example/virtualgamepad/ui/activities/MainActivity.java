package org.example.virtualgamepad.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.example.virtualgamepad.R;
import org.example.virtualgamepad.data.managers.DataManager;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.up_button) Button mUpButton;
    @BindView(R.id.down_button) Button mDownButton;
    @BindView(R.id.right_button) Button mRightButton;
    @BindView(R.id.left_button) Button mLeftButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mUpButton.setOnClickListener(this);
        mDownButton.setOnClickListener(this);
        mRightButton.setOnClickListener(this);
        mLeftButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.up_button:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DataManager.getInstance().getTcpClient().sendMessage("UP_BTN");
                    }
                }).start();
                break;
        }
    }
}
