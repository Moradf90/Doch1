package t.a.m.com.doch1.management.autocomplete;

import android.widget.BaseAdapter;
import android.widget.Filter;

import com.activeandroid.query.Select;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import t.a.m.com.doch1.Models.User;

/**
 * Created by Morad on 12/10/2016.
 */
public abstract class UsersAdapterFilter extends Filter implements ChildEventListener {

    private List<User> mAllUsers;
    private List<User> mFilteredUsers;

    public UsersAdapterFilter(){
        mAllUsers = new ArrayList<>();
        mFilteredUsers = new ArrayList<>();

        // listen to changes in the users
       // FirebaseDatabase.getInstance().getReference(User.USERS_REFERENCE_KEY).addChildEventListener(this);
    }

    public void destroy(){
        // remove the listener
        //FirebaseDatabase.getInstance().getReference(User.USERS_REFERENCE_KEY).removeEventListener(this);
    }

    // filtering
    @Override
    protected FilterResults performFiltering(CharSequence search) {

        String constr = search != null ? search.toString().toLowerCase() : "~!@#$";

        mFilteredUsers.clear();
//        for(User user : mAllUsers) {
//            if (user.getName().toLowerCase().indexOf(constr) != -1) {
//                mFilteredUsers.add(user);
//            }
//        }

        FilterResults result = new FilterResults();
        //result.values = mFilteredUsers;
        result.values = new Select().from(User.class).where(User.NAME_PROPERTY + " LIKE '%" + constr + "%'").execute();
        result.count = mFilteredUsers.size();

        return result;
    }

    @Override
    protected abstract void publishResults(CharSequence search, FilterResults result);

    @Override
    public CharSequence convertResultToString(Object resultValue) {
        return ((User) resultValue).getName();
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if(dataSnapshot.exists()){
            mAllUsers.add(dataSnapshot.getValue(User.class));
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
