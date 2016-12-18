package t.a.m.com.doch1.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Morad on 12/17/2016.
 */
public class OnAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        WakefulIntentService.sendWakefulWork(context, UpdaterService.class);
    }
}
