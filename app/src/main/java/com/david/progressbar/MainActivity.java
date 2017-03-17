package com.david.progressbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.david.progressbar.view.HorizontalProgress;
import com.david.progressbar.view.RoundProgress;

public class MainActivity extends AppCompatActivity {

    HorizontalProgress horizontalProgress;
    RoundProgress roundProgress;


    private static final int MSG_UPDATE = 0x001;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            int progress = horizontalProgress.getProgress();
            horizontalProgress.setProgress(++progress);
            roundProgress.setProgress(++progress);
            if (progress >= 100) {
                handler.removeMessages(MSG_UPDATE);
            }
            handler.sendEmptyMessageDelayed(MSG_UPDATE, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        horizontalProgress = (HorizontalProgress) findViewById(R.id.progress1);
        roundProgress = (RoundProgress) findViewById(R.id.progress2);
        handler.sendEmptyMessageDelayed(MSG_UPDATE, 500);
    }
}
