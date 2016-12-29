package t.a.m.com.doch1.services.tasks;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.activeandroid.query.Select;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import t.a.m.com.doch1.Models.Group;
import t.a.m.com.doch1.Models.User;
import t.a.m.com.doch1.services.UpdaterService;

/**
 * Created by Morad on 12/21/2016.
 */
public class GroupsUpdaterTask implements ValueEventListener {

    public static final String GROUP_UPDATED_ACTION = "group_updated_action";

    // user groups
    private Vector<Long> mGroups; //thread safe
    // map of group to sub groups
    private ConcurrentHashMap<Long, Vector<Long>> mGroupsMap;

    private Context mContext;
    private boolean isExecuted;

    public GroupsUpdaterTask(Context context){
        mContext = context;
        mGroups = new Vector<>();
        mGroupsMap = new ConcurrentHashMap<>();
        isExecuted = false;
    }

    public synchronized void run() {
        if(!isExecuted) {
            isExecuted = true;
            User current = new Select().from(User.class)
                    .where(User.EMAIL_PROPERTY + "= ?", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                    .executeSingle();

            if (current != null && current.getGroups() != null && current.getGroups().size() > 0) {
                for (Long groupId : current.getGroups()) {
                    mGroups.add(groupId);
                    addListenerToGroup(groupId);
                }
            }
        }
    }

    private void addSubGroups(final Long groupId) {
        FirebaseDatabase.getInstance()
                .getReference(Group.GROUPS_REFERENCE_KEY)
                .orderByChild(Group.PARENT_ID_PROPERTY).
                equalTo(groupId)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot subGroups) {
                                if (subGroups.exists()) {
                                    for (DataSnapshot postSnapshot: subGroups.getChildren()) {
                                        Group subGroup = postSnapshot.getValue(Group.class);
                                        subGroup.save();

                                        mGroupsMap.get(subGroup.getParentId())
                                                .add(subGroup.getId());

                                        addListenerToGroup(subGroup.getId());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        }
                );
    }

    private void addListenerToGroup(Long groupId){

        FirebaseDatabase.getInstance()
                .getReference(Group.GROUPS_REFERENCE_KEY)
                .child(groupId.toString())
                .addValueEventListener(this);

        mGroupsMap.put(groupId, new Vector<Long>());

        // listen to user statuses updates
        ((UpdaterService)mContext).getUsersStatusUpdaterTask().addGroupListener(groupId);

        // add sub group to update task
        addSubGroups(groupId);

    }

    @Override
    public void onDataChange(DataSnapshot ds) {
        if(ds.exists()){
            Group group = ds.getValue(Group.class);
            Log.d("Group-Updater", "Group added : " + group.getName());
            group.save();
            mContext.sendBroadcast(new Intent(GROUP_UPDATED_ACTION));

            if(group.getStatusesId() != null){
                ((UpdaterService)mContext).getStatusesUpdaterTask().addStatusesGroupListener(group.getStatusesId());
            }

        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public void refresh(User user) {
        if (user != null && user.getGroups() != null && user.getGroups().size() > 0){

            for(Long groupId : user.getGroups()){
                if(!mGroups.contains(groupId)){
                    mGroups.add(groupId);
                    addListenerToGroup(groupId);
                }
            }

            // there are removed groups
            if(mGroups.size() > user.getGroups().size()){
                for(Long groupId : mGroups) {
                    if(!user.getGroups().contains(groupId)){
                        mGroups.remove(groupId);
                        removeListenerToGroup(groupId);
                    }
                }
            }

            mContext.sendBroadcast(new Intent(GROUP_UPDATED_ACTION));
        }
    }

    private void removeListenerToGroup(Long groupId) {
        FirebaseDatabase.getInstance()
                .getReference(Group.GROUPS_REFERENCE_KEY)
                .child(groupId.toString())
                .removeEventListener(this);

        ((UpdaterService)mContext).getUsersStatusUpdaterTask().removeGroupListener(groupId);

        Group group = Group.load(Group.class, groupId);
        if(group != null){
            // delete from db
            group.delete();

            if(group.getStatusesId() != null) {
                ((UpdaterService)mContext).getStatusesUpdaterTask()
                        .removeStatusesGroupListener(group.getStatusesId());
            }
        }

        if(mGroupsMap.containsKey(groupId)) {
            Vector<Long> subGroups = mGroupsMap.remove(groupId);
            if (subGroups.size() > 0) {
                for (Long subGroup : subGroups) {
                    removeListenerToGroup(subGroup);
                }
            }
        }
    }
}
