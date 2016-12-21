package t.a.m.com.doch1.services;

import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import t.a.m.com.doch1.services.tasks.GroupsUpdaterTask;
import t.a.m.com.doch1.services.tasks.UsersUpdaterTask;

public class UpdaterService extends WakefulIntentService {

    public UpdaterService() {
        super("Updater");
    }

    @Override
    protected void doWakefulWork(Intent intent) {

        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
        if(current != null){
            UsersUpdaterTask.run();
            GroupsUpdaterTask.run();
        }
    }
}
