package t.a.m.com.doch1.management;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unnamed.b.atv.model.TreeNode;

import t.a.m.com.doch1.Models.Group;
import t.a.m.com.doch1.Models.User;
import t.a.m.com.doch1.R;

/**
 * Created by Morad on 12/5/2016.
 */
public class UserHolder extends TreeNode.BaseNodeViewHolder<User> implements View.OnClickListener {

    private User mUser;

    public UserHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, User value) {

        mUser = value;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.user_in_list_layout, null, false);

        TextView text = (TextView) view.findViewById(R.id.name);
        text.setText(value.getName());

        view.findViewById(R.id.delete).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.delete : deleteUser(); break;
        }
    }

    private void deleteUser() {
        new AlertDialog.Builder(context)
                .setMessage("Are you sure you want to delete " + mUser.getName())
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference(User.USERS_REFERENCE_KEY)
                                .child(mUser.getId())
                                .removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        getTreeView().removeNode(mNode);
                                    }
                                });

                        if(mNode.getParent() != null)
                        {
                            if(mNode.getParent().getValue() instanceof Group){
                                Group group = (Group) mNode.getParent().getValue();
                                if(group.getUsers().indexOf(mUser.getId()) != -1)
                                {
                                    group.getUsers().remove(mUser.getId());
                                    FirebaseDatabase.getInstance().getReference(Group.GROUPS_REFERENCE_KEY)
                                            .child(mUser.getGroupId()).child(Group.USERS_PROPERTY)
                                            .setValue(group.getUsers());
                                }
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
