package com.rafaelkarlo.workmode.mainscreen.service.alarm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class WorkModeAlarmUtils {

    public static final String WORK_START_ACTION = "com.rafaelkarlo.workmode.START_WORK";
    public static final String WORK_END_ACTION = "com.rafaelkarlo.workmode.END_WORK";
    public static final int ONE_DAY_IN_MILLIS = 86400000;

    public static Intent createIntentWithIdentifierAndTime(Context context, String actionIdentifier, long timeInMillis) {
        Intent intent = new Intent(actionIdentifier, null, context, WorkModeAlarmReceiver.class);
        intent.putExtra(actionIdentifier, actionIdentifier.hashCode());
        intent.putExtra(getActionIdentifierWithTimeKey(actionIdentifier), timeInMillis);
        return intent;
    }

    @NonNull
    public static String getActionIdentifierWithTimeKey(String actionIdentifier) {
        return actionIdentifier + ".TIME";
    }

    public static PendingIntent createPendingIntentWithIntent(Context context, Intent workStartIntent) {
        int intentUniqueIdentifier = workStartIntent.getIntExtra(workStartIntent.getAction(), 0);
        return PendingIntent.getBroadcast(context, intentUniqueIdentifier, workStartIntent, FLAG_UPDATE_CURRENT);
    }

}
