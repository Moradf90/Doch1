package t.a.m.com.doch1.services.tasks;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.activeandroid.query.Select;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import t.a.m.com.doch1.Models.Group;
import t.a.m.com.doch1.Models.ListOfLongs;
import t.a.m.com.doch1.Models.User;
import t.a.m.com.doch1.services.UpdaterService;

/**
 * Created by Morad on 12/21/2016.
 */
public class GroupsUpdaterTask implements ValueEventListener, ChildEventListener {

    public static final String GROUP_UPDATED_ACTION = "group_updated_action";
    public static final String UPDATED_GROUP_EXTRA = "updated_group";
    public static final String UPDATED_GROUP_ID_EXTRA = "updated_group_id";
    public static final String NEW_GROUP_EXTRA = "new_group";
    public static final String DELETED_GROUPS_EXTRA = "deleted_groups";

    // user groups
    private Vector<Long> mGroups; //thread safe
    // map of group to sub groups
    private ConcurrentHashMap<Long, Vector<Long>> mGroupsMap;

    private Context mContext;
    private boolean isExecuted;
    private ListOfLongs mDeletedGroups;

    public GroupsUpdaterTask(Context context){
        mContext = context;
        mGroups = new Vector<>();
        mGroupsMap = new ConcurrentHashMap<>();
        isExecuted = false;
        mDeletedGroups = new ListOfLongs();
    }

    public synchronized void run() {
        if(!isExecuted) {
            isExecuted = true;

            // for remove
            FirebaseDatabase.getInstance().getReference(Group.GROUPS_REFERENCE_KEY)
                    .addChildEventListener(this);

            User current = User.current(mContext);

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
            Group currentG = Group.load(Group.class, group.getId());
            if(!group.equals(currentG)) {
                Log.d("Group-Updater", "Group added : " + group.getName());
                group.save();

                Intent intent = new Intent(GROUP_UPDATED_ACTION);
                intent.putExtra(UPDATED_GROUP_EXTRA, group);
                intent.putExtra(UPDATED_GROUP_ID_EXTRA, group.getId());
                mContext.sendBroadcast(intent);

                if (group.getStatusesId() != null) {
                    ((UpdaterService) mContext).getStatusesUpdaterTask().addStatusesGroupListener(group.getStatusesId());
                }
            }
        }
    }

    @Override
    public void onChildAdded(DataSnapshot ds, String s) {
    }

    @Override
    public void onChildChanged(DataSnapshot ds, String s) {
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Group group = dataSnapshot.getValue(Group.class);

        if(mGroupsMap.containsKey(group.getId())) {
            Log.d("Group-Updater", "Group deleted : " + group.getName());

            mDeletedGroups.clear();
            removeListenerToGroup(group.getId());

            Group.deleteGroups(mDeletedGroups);

            if(group.getParentId() != null && mGroupsMap.containsKey(group.getParentId())){
                mGroupsMap.get(group.getParentId()).remove(group.getId());
            }

            Intent intent = new Intent(GROUP_UPDATED_ACTION);
            intent.putExtra(DELETED_GROUPS_EXTRA, mDeletedGroups);
            mContext.sendBroadcast(intent);
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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

            mDeletedGroups.clear();

            // there are removed groups
            if(mGroups.size() > user.getGroups().size()){
                for(Long groupId : mGroups) {
                    if(!user.getGroups().contains(groupId)){
                        mGroups.remove(groupId);
                        //mDeletedGroups.add(groupId);
                        removeListenerToGroup(groupId);
                    }
                }
            }

            if(mDeletedGroups.size() > 0) {

                Group.deleteGroups(mDeletedGroups);

                Intent intent = new Intent(GROUP_UPDATED_ACTION);
                intent.putExtra(DELETED_GROUPS_EXTRA, mDeletedGroups);
                mContext.sendBroadcast(intent);
            }
        }
    }

    private void removeListenerToGroup(Long groupId) {
        FirebaseDatabase.getInstance()
                .getReference(Group.GROUPS_REFERENCE_KEY)
                .child(groupId.toString())
                .removeEventListener((ValueEventListener) this);

        mDeletedGroups.add(groupId);

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
