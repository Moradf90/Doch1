package t.a.m.com.doch1.services.tasks;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import t.a.m.com.doch1.Models.UserInGroup;

/**
 * Created by Morad on 12/21/2016.
 */
public class UsersStatusUpdaterTask implements ValueEventListener {

    private static UsersStatusUpdaterTask mTask;

    public static UsersStatusUpdaterTask instance(){
        if(mTask == null){
            mTask = new UsersStatusUpdaterTask();
        }
        return mTask;
    }

    public void addGroupListener(Long groupId){
        FirebaseDatabase.getInstance().getReference(UserInGroup.USERS_IN_GROUP_REFERENCE_KEY)
                .child(groupId.toString())
                .addValueEventListener(this);
    }

    public void removeGroupListener(Long groupId){
        FirebaseDatabase.getInstance().getReference(UserInGroup.USERS_IN_GROUP_REFERENCE_KEY)
                .child(groupId.toString())
                .removeEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot ds) {
        if(ds.exists()){
            Long groupId = Long.parseLong(ds.getKey());
            for (DataSnapshot dataSnapshot : ds.getChildren()){
                Long userId = Long.parseLong(dataSnapshot.getKey());
                UserInGroup userInGroup = dataSnapshot.getValue(UserInGroup.class);
                userInGroup.setGroupId(groupId);
                userInGroup.setUserId(userId);
                userInGroup.save();
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
