package t.a.m.com.doch1.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class UpdaterService extends Service {
    public UpdaterService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
