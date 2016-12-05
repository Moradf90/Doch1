package t.a.m.com.doch1.common.connection;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import t.a.m.com.doch1.Models.MainStatus;
import t.a.m.com.doch1.Models.User;

/**
 * Created by Morad on 12/3/2016.
 */
public class UserUtil {

    private static User mCurrentUserData;

    public static void init(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            database.getReference(User.USERS_REFERENCE_KEY)
                    .orderByChild(User.EMAIL_PROPERTY)
                    .equalTo(firebaseUser.getEmail())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                for (DataSnapshot user : dataSnapshot.getChildren()) {
                                    mCurrentUserData = user.getValue(User.class);
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }
}
