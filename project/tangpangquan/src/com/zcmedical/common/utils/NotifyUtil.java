package com.zcmedical.common.utils;

import hirondelle.date4j.DateTime;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.SparseArray;

import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.push.AlarmReceiver;

public class NotifyUtil {
    /** 强制提醒时间间隔 */
    public static final Long COERCE_TIME = 5 * 60 * 1000L;
    public static final String tag = NotifyUtil.class.getName();

    static AlarmManager alarmMgr;
    static PendingIntent pendingIntent;
    static SparseArray<PendingIntent> pIntentMap = new SparseArray<PendingIntent>();
    public static SharedPreferences spfUtil;

    public static void addAppNotiFy(Context context, String tilte, String content, int taskid) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.logo_udr).setContentTitle(content).setContentText(tilte);
        spfUtil = context.getSharedPreferences(TpqApplication.class.getName(), 0);
        mBuilder.setAutoCancel(true);
        //mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setVibrate(new long[] { 0, 2000, 0, 0 });
        mBuilder.setLights(Color.parseColor("#f35381"), 5000, 5000);
        mBuilder.setSound(getDefaultRingtoneUri(context));
        Intent intent = startOrReturnApp(context);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0 == taskid ? (int) (System.currentTimeMillis() % 1000) : taskid, mBuilder.build());
    }

    public static Uri getDefaultRingtoneUri(Context context) {
        return RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);
    }

    public static Intent startOrReturnApp(Context context) {
        Intent activityIntent = new Intent(Intent.ACTION_MAIN);
        activityIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        activityIntent.setComponent(new ComponentName(context.getPackageName(), "com.zcmedical.tangpangquan.activity.WelcomeActivity"));
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        return activityIntent;
    }

    public static void cancelAlarm(int taskid) {
        AlarmManager alarmManager = (AlarmManager) TpqApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
        PendingIntent p = pIntentMap.get(taskid);
        if (null != p) {
            alarmManager.cancel(p);
        }
    }

    private static PendingIntent getPendingIntent(Context context, int taskid, String title, String content) {
        Intent intent = new Intent(AlarmReceiver.ALARM_ACTION);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("TaskID", taskid);
        return PendingIntent.getBroadcast(context, taskid, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static void setAlarm(Context context, int taskid, String title, String content, long timeInMillis, boolean isCancel) {
        if (isCancel) {
            cancelAlarm(taskid);
        } else {
            PendingIntent pIntent = getPendingIntent(context, taskid, title, content);
            pIntentMap.put(taskid, pIntent);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, DateUtils.DAY_IN_MILLIS, pIntent);
        }
    }

    public static void setWeightAlarm(Context context, long timeInMillis, boolean isCancel) {
        setAlarm(context, CommonConstant.REMIND_WEIGHT, "为了健康的身体。", "该记体重啦~", timeInMillis, isCancel);
    }

    public static void setBloodSugarRemind(Context context, int taskid, long timeInMillis, boolean isCancel) {
        setAlarm(context, taskid, "为了健康的身体。", "该记血糖啦~", timeInMillis, isCancel);
    }

    public static void setEverydayAlarm(Context context, long timeInMillis, boolean isCancel) {
        setAlarm(context, CommonConstant.REMIND_EVERYDAY, "为了健康的身体。", "记录一下您的状态吧~", timeInMillis, isCancel);
    }

    public static void setCommunityAlarm(Context context, String title, String content, long timeInMillis, boolean isCancel) {
        setAlarm(context, CommonConstant.REMIND_COMMUNITY, TextUtils.isEmpty(title) ? "社区有新消息" : title, TextUtils.isEmpty(content) ? "请及时查看~" : title, timeInMillis, isCancel);
    }

    public static long getNextAlarmTime(String time) {
        Calendar c = Calendar.getInstance();
        long now = c.getTimeInMillis();
        DateTime d = new DateTime(time);
        c.set(Calendar.HOUR_OF_DAY, d.getHour());
        c.set(Calendar.MINUTE, d.getMinute());
        return now > c.getTimeInMillis() ? c.getTimeInMillis() + DateUtils.DAY_IN_MILLIS : c.getTimeInMillis();
    }

    public static long getNextDay(String time) {
        Calendar c = Calendar.getInstance();
        DateTime d = new DateTime(time);
        c.set(Calendar.HOUR_OF_DAY, d.getHour());
        c.set(Calendar.MINUTE, d.getMinute());
        return c.getTimeInMillis() + DateUtils.DAY_IN_MILLIS;
    }

    public static void setTaskAlarm(Context context, int taskid, boolean isCancel) {
        if (null == context) {
            return;
        }
        spfUtil = context.getSharedPreferences(TpqApplication.class.getName(), 0);
        String time = null;
        switch (taskid) {
        case CommonConstant.REMIND_BLOOD_SUGAR_1:
            time = spfUtil.getString("Remind_Bs_Time_1", "07:00");
            setBloodSugarRemind(context, taskid, getNextAlarmTime(time), isCancel);
            break;
        case CommonConstant.REMIND_BLOOD_SUGAR_2:
            time = spfUtil.getString("Remind_Bs_Time_2", "09:00");
            setBloodSugarRemind(context, taskid, getNextAlarmTime(time), isCancel);
            break;
        case CommonConstant.REMIND_BLOOD_SUGAR_3:
            time = spfUtil.getString("Remind_Bs_Time_3", "12:00");
            setBloodSugarRemind(context, taskid, getNextAlarmTime(time), isCancel);
            break;
        case CommonConstant.REMIND_BLOOD_SUGAR_4:
            time = spfUtil.getString("Remind_Bs_Time_4", "14:00");
            setBloodSugarRemind(context, taskid, getNextAlarmTime(time), isCancel);
            break;
        case CommonConstant.REMIND_BLOOD_SUGAR_5:
            time = spfUtil.getString("Remind_Bs_Time_5", "18:00");
            setBloodSugarRemind(context, taskid, getNextAlarmTime(time), isCancel);
            break;
        case CommonConstant.REMIND_BLOOD_SUGAR_6:
            time = spfUtil.getString("Remind_Bs_Time_6", "20:00");
            setBloodSugarRemind(context, taskid, getNextAlarmTime(time), isCancel);
            break;
        case CommonConstant.REMIND_BLOOD_SUGAR_7:
            time = spfUtil.getString("Remind_Bs_Time_7", "23:16");
            setBloodSugarRemind(context, taskid, getNextAlarmTime(time), isCancel);
            break;
        case CommonConstant.REMIND_WEIGHT:
            time = spfUtil.getString("Remind_Weight_Time", "08:00");
            setWeightAlarm(context, getNextAlarmTime(time), isCancel);
            break;
        case CommonConstant.REMIND_BLOOD_SUGAR_8:
            time = spfUtil.getString("Remind_Bs_Time_8", "23:56");
            setBloodSugarRemind(context, taskid, getNextAlarmTime(time), isCancel);
            break;
        case CommonConstant.REMIND_EVERYDAY:
            time = spfUtil.getString("Remind_Everyday_Time", "09:00");
            setEverydayAlarm(context, getNextAlarmTime(time), isCancel);
            break;
        default:
            break;
        }
    }

}
