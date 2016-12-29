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
        // if the service not started or when the user logout, updates the tasks updaters
        Intent service = new Intent(context, UpdaterService.class);
        context.startService(service);
    }
}
