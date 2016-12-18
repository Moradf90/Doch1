package t.a.m.com.doch1.services.tasks;

import android.os.AsyncTask;

import com.activeandroid.util.Log;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import t.a.m.com.doch1.Models.User;

/**
 * Created by Morad on 12/17/2016.
 */
public class UsersUpdaterTask extends AsyncTask<Void, Void, Void> implements ChildEventListener {

    private static UsersUpdaterTask mTask;

    public static void run(){
        if(mTask == null) {
            mTask = new UsersUpdaterTask();
            mTask.execute();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {

        FirebaseDatabase.getInstance().getReference(User.USERS_REFERENCE_KEY)
                .addChildEventListener(this);

//        while (true){
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        FirebaseDatabase.getInstance().getReference(User.USERS_REFERENCE_KEY)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot ds) {
//                        if(ds.exists()){
//                            for (DataSnapshot snapshot : ds.getChildren()){
//                                User user = snapshot.getValue(User.class);
//                                user.save();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError ds) {
//
//                    }
//                });
        return null;
    }

    @Override
    public void onChildAdded(DataSnapshot snapshot, String s) {
        User user = snapshot.getValue(User.class);
        Log.d("User-Updater", "User added : " + user.getName());
        user.save();
    }

    @Override
    public void onChildChanged(DataSnapshot snapshot, String s) {

        User user = snapshot.getValue(User.class);
        Log.d("User-Updater", "User changed : " + user.getName());
        user.save();
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
