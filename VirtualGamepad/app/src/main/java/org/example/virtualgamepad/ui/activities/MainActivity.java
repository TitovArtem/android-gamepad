package org.example.virtualgamepad.ui.activities;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import org.example.virtualgamepad.R;
import org.example.virtualgamepad.data.managers.DataManager;
import org.example.virtualgamepad.utils.ButtonCodes;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    @BindView(R.id.dpad_up_btn) Button mDpadUpButton;
    @BindView(R.id.dpad_down_btn) Button mDpadDownButton;
    @BindView(R.id.dpad_right_btn) Button mDpadRightButton;
    @BindView(R.id.dpad_left_btn) Button mDpadLeftButton;
    
    @BindView(R.id.act_up_btn) Button mActionUpButton;
    @BindView(R.id.act_down_btn) Button mActionDownButton;
    @BindView(R.id.act_right_btn) Button mActionRightButton;
    @BindView(R.id.act_left_btn) Button mActionLeftButton;
    
    @BindView(R.id.l1_btn) Button mL1Button;
    @BindView(R.id.l2_btn) Button mL2Button;
    @BindView(R.id.r1_btn) Button mR1Button;
    @BindView(R.id.r2_btn) Button mR2Button;

    @BindView(R.id.start_btn) Button mStartButton;
    @BindView(R.id.select_btn) Button mSelectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the ActionBar
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mDpadUpButton.setOnTouchListener(this);
        mDpadDownButton.setOnTouchListener(this);
        mDpadRightButton.setOnTouchListener(this);
        mDpadLeftButton.setOnTouchListener(this);
        mActionUpButton.setOnTouchListener(this);
        mActionDownButton.setOnTouchListener(this);
        mActionRightButton.setOnTouchListener(this);
        mActionLeftButton.setOnTouchListener(this);
        mL1Button.setOnTouchListener(this);
        mL2Button.setOnTouchListener(this);
        mR1Button.setOnTouchListener(this);
        mR2Button.setOnTouchListener(this);
        mSelectButton.setOnTouchListener(this);
        mStartButton.setOnTouchListener(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        int action = motionEvent.getAction();
        if (action != MotionEvent.ACTION_DOWN && action != MotionEvent.ACTION_UP) {
            return false;
        }
        action = action == 0 ? 1 : 0;   // Reverse codes

        final int buttonCode = getButtonCodeFromViewId(view.getId());
        if (buttonCode >= 0) {
            sendCommand(buttonCode + " " + action);
        }
        return false;
    }

    private int getButtonCodeFromViewId(int viewId) {
        switch (viewId) {
            case R.id.dpad_up_btn:
                return ButtonCodes.DPAD_UP_BTN;
            case R.id.dpad_down_btn:
                return ButtonCodes.DPAD_DOWN_BTN;
            case R.id.dpad_right_btn:
                return ButtonCodes.DPAD_RIGHT_BTN;
            case R.id.dpad_left_btn:
                return ButtonCodes.DPAD_LEFT_BTN;
            case R.id.act_up_btn:
                return ButtonCodes.ACT_UP_BTN;
            case R.id.act_down_btn:
                return ButtonCodes.ACT_DOWN_BTN;
            case R.id.act_left_btn:
                return ButtonCodes.ACT_LEFT_BTN;
            case R.id.act_right_btn:
                return ButtonCodes.ACT_RIGHT_BTN;
            case R.id.r1_btn:
                return ButtonCodes.R1_BTN;
            case R.id.r2_btn:
                return ButtonCodes.R2_BTN;
            case R.id.l1_btn:
                return ButtonCodes.L1_BTN;
            case R.id.l2_btn:
                return ButtonCodes.L2_BTN;
            case R.id.select_btn:
                return ButtonCodes.SELECT_BTN;
            case R.id.start_btn:
                return ButtonCodes.START_BTN;
        }
        return -1;
    }

    public void sendCommand(final String cmd) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataManager.getInstance().getTcpClient().sendMessage(cmd);
            }
        }).start();
    }
}
