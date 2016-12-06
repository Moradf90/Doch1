package t.a.m.com.doch1.management;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.UUID;

import t.a.m.com.doch1.Models.Group;
import t.a.m.com.doch1.Models.User;
import t.a.m.com.doch1.R;
import t.a.m.com.doch1.management.fragments.AddUserFragment;

public class ManagementActivity extends AppCompatActivity implements GroupHolder.OnAddButtonClicked, AddUserFragment.OnAddUserEvents {

    private AndroidTreeView mTreeView;
    private TreeNode mCurrentParentNode;
    private Group mCurrentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

        //TODO  get the group id from the intent
        final String groupId = "1";

        final TreeNode thatroot = TreeNode.root();
        final TreeNode root = new TreeNode("Loading ...");
        thatroot.addChild(root);
        mTreeView = new AndroidTreeView(this, thatroot);

        FirebaseDatabase.getInstance().getReference("groups").child(groupId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mTreeView.removeNode(root);
                    Group group1 = dataSnapshot.getValue(Group.class);
                    mTreeView.addNode(thatroot, new TreeNode(group1).setViewHolder(new GroupHolder(ManagementActivity.this)));
                    mTreeView.expandLevel(2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mTreeView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        ((FrameLayout)findViewById(R.id.container)).addView(mTreeView.getView());
    }

    @Override
    public void onAddButtonClicked(TreeNode parent,Group group) {
        mCurrentParentNode = parent;
        mCurrentGroup = group;

        new AlertDialog.Builder(this)
                .setTitle("Which Item you want to add under "+ group.getName()+":")
                .setSingleChoiceItems(new String[]{"User", "Group"}, -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i){
                                    case 0 : addUser(); break;
                                    case 1 : addGroup(); break;
                                }
                                dialogInterface.dismiss();
                            }
                        })
                .setNegativeButton("Cancel", null)
                .create()
                .show();

    }

    private void addGroup() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Enter Name of the group :");

        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        dialogBuilder.setView(editText)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final Group newGroup = new Group();
                newGroup.setName(editText.getText().toString());
                newGroup.setParentId(mCurrentGroup.getId());
                newGroup.setId(UUID.randomUUID().toString());
                FirebaseDatabase.getInstance().getReference("groups")
                        .child(newGroup.getId())
                        .setValue(newGroup)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mTreeView.addNode(mCurrentParentNode, new TreeNode(newGroup).setViewHolder(new GroupHolder(ManagementActivity.this)));
                            }
                        });
            }
        })
        .setNegativeButton("Cancel", null);

        dialogBuilder.create().show();
    }

    private void addUser() {

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .add(R.id.add_user_frame, new AddUserFragment(), AddUserFragment.TAG)
                .addToBackStack(AddUserFragment.TAG)
                .commit();
    }

    @Override
    public void onSaveClicked(final User user) {
        if(user != null){
            user.setGroupId(mCurrentGroup.getId());

            FirebaseDatabase.getInstance().getReference("users").child(user.getId()).setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            onCancelClicked(); // close the bottom sheet
                            mTreeView.addNode(mCurrentParentNode, new TreeNode(user).setViewHolder(new UserHolder(ManagementActivity.this)));
                        }
                    });

            mCurrentGroup.addUser(user.getId());
            FirebaseDatabase.getInstance().getReference("groups").child(mCurrentGroup.getId())
                    .child(Group.USERS_PROPERTY).setValue(mCurrentGroup.getUsers());
        }
    }

    @Override
    public void onCancelClicked() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(AddUserFragment.TAG);

        if(fragment != null){
            manager.beginTransaction().remove(fragment).commit();
        }
    }
}
