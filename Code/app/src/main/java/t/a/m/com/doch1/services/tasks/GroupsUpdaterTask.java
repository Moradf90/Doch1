package t.a.m.com.doch1.services.tasks;

import com.activeandroid.query.Select;
import com.activeandroid.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import t.a.m.com.doch1.Models.Group;
import t.a.m.com.doch1.Models.User;

/**
 * Created by Morad on 12/21/2016.
 */
public class GroupsUpdaterTask implements ValueEventListener {

    private static GroupsUpdaterTask mTask;

    private Vector<Long> mGroups; //thread safe

    private GroupsUpdaterTask(){
        mGroups = new Vector<>();
    }

    public static void run(){
        if(mTask == null){
            mTask = new GroupsUpdaterTask();
            mTask.execute();
        }
    }

    private void execute() {
        User current = new Select().from(User.class)
                .where(User.EMAIL_PROPERTY + "= ?", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .executeSingle();

        if(current != null && current.getGroups() != null && current.getGroups().size() > 0) {
            for(Long groupId : current.getGroups()) {
                mGroups.add(groupId);
                addListenerToGroup(groupId);
            }
        }
    }

    private void addListenerToGroup(Long groupId){
        FirebaseDatabase.getInstance()
                .getReference(Group.GROUPS_REFERENCE_KEY)
                .child(groupId.toString())
                .addValueEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot ds) {
        if(ds.exists()){
            Group group = ds.getValue(Group.class);
            Log.d("Group-Updater", "Group added : " + group.getName());
            group.save();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public static void refresh(User user) {
        if (mTask != null && user != null && user.getGroups() != null && user.getGroups().size() > 0){

            for(Long groupId : user.getGroups()){
                if(!mTask.mGroups.contains(groupId)){
                    mTask.mGroups.add(groupId);
                    mTask.addListenerToGroup(groupId);
                }
            }

            // there are removed groups
            if(mTask.mGroups.size() > user.getGroups().size()){
                for(Long groupId : mTask.mGroups) {
                    if(!user.getGroups().contains(groupId)){
                        mTask.mGroups.remove(groupId);
                        mTask.removeListenerToGroup(groupId);
                    }
                }
            }
        }
    }

    private void removeListenerToGroup(Long groupId) {
        FirebaseDatabase.getInstance()
                .getReference(Group.GROUPS_REFERENCE_KEY)
                .child(groupId.toString())
                .removeEventListener(this);
    }
}
