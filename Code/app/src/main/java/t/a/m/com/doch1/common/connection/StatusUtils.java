package t.a.m.com.doch1.common.connection;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.haha.perflib.Main;

import java.util.ArrayList;
import java.util.List;

import t.a.m.com.doch1.Models.MainStatus;
import t.a.m.com.doch1.Models.User;

/**
 * Created by Morad on 12/3/2016.
 */
public class StatusUtils {

    public static List<MainStatus> getAllMainStatuses() {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final List<MainStatus> lst = new ArrayList<>();

        database.getReference(MainStatus.STATUSES_REFERENCE_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    lst.add(postSnapshot.getValue(MainStatus.class));
                }
                //notifyDataSetChanged

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return lst;
    }

    public static List<String> getSubStatusesByMain(String mainStatusName) {

        // todo: save otherwise
        final List<String> subStatusesToReturn = new ArrayList<>();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference(MainStatus.STATUSES_REFERENCE_KEY)
                .orderByChild(MainStatus.MAIN_STATUS_NAME)
                .equalTo(mainStatusName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                subStatusesToReturn.addAll(dataSnapshot.getValue(MainStatus.class).getSubStatuses());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return subStatusesToReturn;
    }
}
