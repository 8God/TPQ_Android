package com.zcmedical.tangpangquan.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zcmedical.common.utils.NotifyUtil;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    public static final String ALARM_ACTION = "com.zcmedical.tangpangquan.push.alarm_action";
    public static final boolean DEFAULT_STATUS = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "AlarmReceiver onReceive");
        if (ALARM_ACTION.equals(action)) {
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");
            int taskid = intent.getIntExtra("TaskID", 0);
            Log.d(TAG, "AlarmReceiver task id:" + taskid);
            NotifyUtil.addAppNotiFy(context, title, content, taskid);
        }
        return;
    }

}
