package t.a.m.com.doch1.management;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.renderscript.Long2;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.johnkil.print.PrintView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unnamed.b.atv.model.TreeNode;

import t.a.m.com.doch1.Models.Group;
import t.a.m.com.doch1.Models.User;
import t.a.m.com.doch1.R;

/**
 * Created by Morad on 12/5/2016.
 */
public class GroupHolder extends TreeNode.BaseNodeViewHolder<Group> implements View.OnClickListener {

    private TextView mNameView;
    private PrintView mArrowView;
    private Group mGroup;
    private TreeNode mNode;
    private OnAddButtonClicked mListener;

    public GroupHolder(Context context, OnAddButtonClicked listener) {
        super(context);
        mListener = listener;
    }

    @Override
    public View createNodeView(TreeNode node, Group value) {

        mGroup = value;
        mNode = node;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.group_in_list_layout, null, false);

        mArrowView = (PrintView) view.findViewById(R.id.arrow_icon);

        mNameView = (TextView) view.findViewById(R.id.name);
        mNameView.setText(value.getName());

        if (node.getLevel() == 1) {
            view.findViewById(R.id.delete).setVisibility(View.GONE);
        }

        view.findViewById(R.id.add).setOnClickListener(this);

        view.findViewById(R.id.delete).setOnClickListener(this);

        return view;
    }

    @Override
    public void toggle(boolean active) {
        mArrowView.setIconText(context.getResources().getString(active ? R.string.ic_keyboard_arrow_down : R.string.ic_keyboard_arrow_right));

        if(active) {

            if (mNode.getChildren().size() == 0) {
                Long groupId = mGroup.getId();
                final TreeNode groups = new TreeNode("Loading groups ...");
                final TreeNode users = new TreeNode("Loading users ...");
                if (mGroup.getUsers() != null && mGroup.getUsers().size() > 0) {
                    mNode.addChild(users);

                    FirebaseDatabase.getInstance().getReference("users")
                            .orderByChild(User.GROUP_ID_PROPERTY)
                            .equalTo(groupId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    getTreeView().removeNode(users);
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            User g = ds.getValue(User.class);
                                            TreeNode child = new TreeNode(g).setViewHolder(new UserHolder(context));
                                            getTreeView().addNode(mNode, child);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    getTreeView().removeNode(users);
                                }
                            });

                }

                mNode.addChild(groups);
                FirebaseDatabase.getInstance().getReference("groups")
                        .orderByChild(Group.PARENT_ID_PROPERTY)
                        .equalTo(groupId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                getTreeView().removeNode(groups);
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        Group g = ds.getValue(Group.class);
                                        TreeNode child = new TreeNode(g).setViewHolder(new GroupHolder(context, mListener));
                                        getTreeView().addNode(mNode, child);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                getTreeView().removeNode(groups);
                            }
                        });
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.delete : deleteGroup(); break;
            case R.id.add : onAddClicked(); break;
        }
    }

    private void onAddClicked() {

        if(mListener != null){
            mListener.onAddButtonClicked(mNode, mGroup);
        }
    }

    private void deleteGroup() {

        new AlertDialog.Builder(context)
                .setMessage("Are you sure you want to delete " + mGroup.getName())
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference(Group.GROUPS_REFERENCE_KEY)
                                .child(mGroup.getId().toString())
                                .removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        getTreeView().removeNode(mNode);
                                    }
                                });

                        if(mGroup.getUsers() != null && mGroup.getUsers().size() > 0){
                            for (Long id : mGroup.getUsers()) {
                                FirebaseDatabase.getInstance().getReference(User.USERS_REFERENCE_KEY).child(id.toString());
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public interface OnAddButtonClicked{
        void onAddButtonClicked(TreeNode parent, Group group);
    }
}
