package t.a.m.com.doch1.services.tasks;

import android.os.AsyncTask;

import com.activeandroid.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import t.a.m.com.doch1.Models.User;

/**
 * Created by Morad on 12/17/2016.
 */
public class UsersUpdaterTask implements ChildEventListener {

    private static UsersUpdaterTask mTask;

    public static void run(){
        if(mTask == null) {
            mTask = new UsersUpdaterTask();
            mTask.execute();
        }
    }

    private void execute() {
        FirebaseDatabase.getInstance().getReference(User.USERS_REFERENCE_KEY)
                .addChildEventListener(this);
    }

    public static void cancel(){
        if(mTask != null) {
            mTask.stop();
        }
    }

    private void stop(){
        FirebaseDatabase.getInstance().getReference(User.USERS_REFERENCE_KEY)
                .removeEventListener(this);
    }

    @Override
    public void onChildAdded(DataSnapshot snapshot, String s) {
        User user = snapshot.getValue(User.class);
        Log.d("User-Updater", "User added : " + user.getName());
        user.save();

        if(user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            GroupsUpdaterTask.refresh(user);
        }
    }

    @Override
    public void onChildChanged(DataSnapshot snapshot, String s) {

        User user = snapshot.getValue(User.class);
        Log.d("User-Updater", "User changed : " + user.getName());
        user.save();

        if(user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            GroupsUpdaterTask.refresh(user);
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

        User user = dataSnapshot.getValue(User.class);
        Log.d("User-Updater", "User deleted : " + user.getName());
        user.delete();
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
