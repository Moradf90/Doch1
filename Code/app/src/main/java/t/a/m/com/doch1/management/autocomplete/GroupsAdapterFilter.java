package t.a.m.com.doch1.management.autocomplete;

import android.widget.Filter;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import t.a.m.com.doch1.Models.Group;
import t.a.m.com.doch1.common.connection.UserUtil;

/**
 * Created by Morad on 12/10/2016.
 */
public abstract class GroupsAdapterFilter extends Filter implements ChildEventListener {

    private List<Group> mAllGroups;
    private List<Group> mFilteredGroups;

    public GroupsAdapterFilter(){
        mAllGroups = new ArrayList<>();
        mFilteredGroups = new ArrayList<>();

        // listen to changes in the users
        FirebaseDatabase.getInstance().getReference(Group.GROUPS_REFERENCE_KEY).addChildEventListener(this);
    }

    public void destroy(){
        // remove the listener
        FirebaseDatabase.getInstance().getReference(Group.GROUPS_REFERENCE_KEY).removeEventListener(this);
    }

    // filtering
    @Override
    protected FilterResults performFiltering(CharSequence search) {

        String constr = search != null ? search.toString().toLowerCase() : "~!@#$";

        mFilteredGroups.clear();

        List<String> gIds = UserUtil.getCurrentUserData().getGroups();

        for(Group group : mAllGroups) {
            if(gIds != null && gIds.indexOf(group.getId()) != -1)
            if (group.getName().toLowerCase().indexOf(constr) != -1) {
                mFilteredGroups.add(group);
            }
        }

        FilterResults result = new FilterResults();
        result.values = mFilteredGroups;
        result.count = mFilteredGroups.size();

        return result;
    }

    @Override
    protected abstract void publishResults(CharSequence search, FilterResults result);

    @Override
    public CharSequence convertResultToString(Object resultValue) {
        return ((Group) resultValue).getName();
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if(dataSnapshot.exists()){
            mAllGroups.add(dataSnapshot.getValue(Group.class));
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
