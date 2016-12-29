package t.a.m.com.doch1.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import t.a.m.com.doch1.Models.User;
import t.a.m.com.doch1.services.tasks.GroupsUpdaterTask;
import t.a.m.com.doch1.services.tasks.StatusesUpdaterTask;
import t.a.m.com.doch1.services.tasks.UsersStatusUpdaterTask;
import t.a.m.com.doch1.services.tasks.UsersUpdaterTask;

public class UpdaterService extends Service {

    private GroupsUpdaterTask mGroupsUpdaterTask;
    private UsersUpdaterTask mUsersUpdaterTask;
    private UsersStatusUpdaterTask mUsersStatusUpdaterTask;
    private StatusesUpdaterTask mStatusesUpdaterTask;
    public UpdaterService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        mGroupsUpdaterTask = new GroupsUpdaterTask(this);
        mUsersUpdaterTask = new UsersUpdaterTask(this);
        mUsersStatusUpdaterTask = new UsersStatusUpdaterTask(this);
        mStatusesUpdaterTask = new StatusesUpdaterTask(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
        if(current != null){
            mUsersUpdaterTask.run();
            mGroupsUpdaterTask.run();
        }

        return START_STICKY;
    }

    public void refresh(User user) {
        if(user != null){
            mGroupsUpdaterTask.refresh(user);
        }
    }

    public StatusesUpdaterTask getStatusesUpdaterTask(){
        return mStatusesUpdaterTask;
    }

    public UsersStatusUpdaterTask getUsersStatusUpdaterTask(){
        return mUsersStatusUpdaterTask;
    }
}
