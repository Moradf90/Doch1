package t.a.m.com.doch1.services.tasks;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import t.a.m.com.doch1.Models.User;
import t.a.m.com.doch1.services.UpdaterService;

/**
 * Created by Morad on 12/17/2016.
 */
public class UsersUpdaterTask implements ChildEventListener {

    public static final String USER_UPDATED_ACTION = "user_updated_action";
    public static final String UPDATED_USER_EXTRA = "user_updated";
    public static final String NEW_USER_EXTRA = "new_user";
    public static final String DELETED_USER_EXTRA = "deleted_user";

    private Context mContext;
    private boolean isExecuted;

    public UsersUpdaterTask(Context context){
        mContext = context;
        isExecuted = false;
    }

    public synchronized void run() {
        if(!isExecuted) {
            isExecuted = true;
            FirebaseDatabase.getInstance().getReference(User.USERS_REFERENCE_KEY)
                    .addChildEventListener(this);
        }
    }

    private void stop(){
        FirebaseDatabase.getInstance().getReference(User.USERS_REFERENCE_KEY)
                .removeEventListener(this);
    }

    @Override
    public void onChildAdded(DataSnapshot snapshot, String s) {
        User user = snapshot.getValue(User.class);
        User currentUser = User.load(User.class, user.getId());
        if(!user.equals(currentUser)){
            Log.d("User-Updater", "User added : " + user.getName());
            user.save();

            Intent intent = new Intent(USER_UPDATED_ACTION);
            intent.putExtra(NEW_USER_EXTRA, user);

            mContext.sendBroadcast(intent);

            if(user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                    && mContext instanceof UpdaterService) {
                ((UpdaterService)mContext).refresh(user);
            }
        }
    }

    @Override
    public void onChildChanged(DataSnapshot snapshot, String s) {

        User user = snapshot.getValue(User.class);
        Log.d("User-Updater", "User changed : " + user.getName());
        user.save();
        Intent intent = new Intent(USER_UPDATED_ACTION);
        intent.putExtra(UPDATED_USER_EXTRA, user);
        mContext.sendBroadcast(intent);

        if(user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                && mContext instanceof UpdaterService) {
            ((UpdaterService)mContext).refresh(user);
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

        User user = dataSnapshot.getValue(User.class);
        Log.d("User-Updater", "User deleted : " + user.getName());
        user.delete();
        Intent intent = new Intent(USER_UPDATED_ACTION);
        intent.putExtra(DELETED_USER_EXTRA, user);
        mContext.sendBroadcast(intent);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
