package t.a.m.com.doch1.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Created by Morad on 12/17/2016.
 */
public class StartServiceReceiver extends BroadcastReceiver {
    private static final int PERIOD = 1 * 60 * 1000;   // 1 minutes

    @Override
    public void onReceive(Context context, Intent intent) {

        AlarmManager mgr =(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, OnAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 60000,
                PERIOD,
                pi);

        // first time
        Intent service = new Intent(context, UpdaterService.class);
        context.startService(service);
    }
}
