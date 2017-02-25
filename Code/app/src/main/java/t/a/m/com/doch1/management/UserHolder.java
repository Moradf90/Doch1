package t.a.m.com.doch1.management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.unnamed.b.atv.model.TreeNode;

import t.a.m.com.doch1.Models.User;
import t.a.m.com.doch1.R;
import t.a.m.com.doch1.views.RoundedImageView;

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

        if(value.getImage() != null) {
            RoundedImageView image = (RoundedImageView) view.findViewById(R.id.image);
            Glide.with(context).load(value.getImage())
                    .error(R.drawable.profile_pic).into(image);
        }

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
//        new AlertDialog.Builder(context)
//                .setMessage("Are you sure you want to delete " + mUser.getName())
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        FirebaseDatabase.getInstance().getReference(User.USERS_REFERENCE_KEY)
//                                .child(mUser.getId().toString())
//                                .removeValue(new DatabaseReference.CompletionListener() {
//                                    @Override
//                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                        getTreeView().removeNode(mNode);
//                                    }
//                                });
//
//                        if(mNode.getParent() != null)
//                        {
//                            if(mNode.getParent().getValue() instanceof Group){
//                                Group group = (Group) mNode.getParent().getValue();
//                                if(group.getUsers().indexOf(mUser.getId()) != -1)
//                                {
//                                    group.getUsers().remove(mUser.getId());
//                                    FirebaseDatabase.getInstance().getReference(Group.GROUPS_REFERENCE_KEY)
//                                            .child(mUser.getGroupId()).child(Group.USERS_PROPERTY)
//                                            .setValue(group.getUsers());
//                                }
//                            }
//                        }
//                    }
//                })
//                .setNegativeButton("Cancel", null)
//                .create()
//                .show();
    }
}
