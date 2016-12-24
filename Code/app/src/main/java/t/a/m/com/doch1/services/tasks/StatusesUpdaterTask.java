package t.a.m.com.doch1.services.tasks;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Vector;

import t.a.m.com.doch1.Models.StatusesInGroup;

/**
 * Created by Morad on 12/23/2016.
 */
public class StatusesUpdaterTask implements ValueEventListener {
    public static final String STATUSES_UPDATED_ACTION = "statuses_updated_action";

    private static StatusesUpdaterTask mTask;

    private Vector<Long> mStatusesIds; //thread safe

    private Context mContext;
    private StatusesUpdaterTask(Context context){
        mContext = context;
        mStatusesIds = new Vector<>();
    }

    public static StatusesUpdaterTask instance(Context context){
        if(mTask == null){
            mTask = new StatusesUpdaterTask(context);
        }
        return mTask;
    }

    public void addStatusesGroupListener(Long statusesId){
        if(!mStatusesIds.contains(statusesId)) {
            FirebaseDatabase.getInstance().getReference(StatusesInGroup.STATUSES_IN_GROUP_REFERENCE_KEY)
                    .child(statusesId.toString())
                    .addValueEventListener(this);
            mStatusesIds.add(statusesId);
        }
    }

    public void removeStatusesGroupListener(Long statusesId){
        if(mStatusesIds.contains(statusesId)) {
            FirebaseDatabase.getInstance().getReference(StatusesInGroup.STATUSES_IN_GROUP_REFERENCE_KEY)
                    .child(statusesId.toString())
                    .removeEventListener(this);
            mStatusesIds.remove(statusesId);
        }
    }

    @Override
    public void onDataChange(DataSnapshot ds) {
        if(ds.exists()){
            for (DataSnapshot dataSnapshot : ds.getChildren()){
                StatusesInGroup stg = dataSnapshot.getValue(StatusesInGroup.class);
                stg.save();
            }

            mContext.sendBroadcast(new Intent(STATUSES_UPDATED_ACTION));
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
