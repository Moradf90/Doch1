package t.a.m.com.doch1.management;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.Date;
import java.util.UUID;

import t.a.m.com.doch1.Models.Group;
import t.a.m.com.doch1.R;
import t.a.m.com.doch1.management.fragments.AddUserFragment;

public class ManagementFragment extends Fragment implements GroupHolder.OnAddButtonClicked, AddUserFragment.CurrentDataGetter {

    private AndroidTreeView mTreeView;
    private TreeNode mCurrentParentNode;
    private Group mCurrentGroup;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_management, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.managmen_fragment_title);

        //TODO  get the group id from the intent
        final String groupId = "1";

        final TreeNode thatroot = TreeNode.root();
        final TreeNode root = new TreeNode("Loading ...");
        thatroot.addChild(root);
        mTreeView = new AndroidTreeView(getActivity(), thatroot);

        FirebaseDatabase.getInstance().getReference("groups").child(groupId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            mTreeView.removeNode(root);
                            Group group1 = dataSnapshot.getValue(Group.class);
                            mTreeView.addNode(thatroot, new TreeNode(group1).setViewHolder(new GroupHolder(getActivity(), ManagementFragment.this)));
                            mTreeView.expandLevel(2);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        mTreeView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        ((FrameLayout)view.findViewById(R.id.container)).addView(mTreeView.getView());
    }

    @Override
    public void onAddButtonClicked(TreeNode parent,Group group) {
        mCurrentParentNode = parent;
        mCurrentGroup = group;

        new AlertDialog.Builder(getActivity())
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
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle("Enter Name of the group :");

        final EditText editText = new EditText(getActivity());
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        dialogBuilder.setView(editText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final Group newGroup = new Group();
                        newGroup.setName(editText.getText().toString());
                        newGroup.setParentId(mCurrentGroup.getId());
                        newGroup.setId(new Date().getTime());
                        FirebaseDatabase.getInstance().getReference("groups")
                                .child(newGroup.getId().toString())
                                .setValue(newGroup)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mTreeView.addNode(mCurrentParentNode, new TreeNode(newGroup).setViewHolder(new GroupHolder(getActivity(), ManagementFragment.this)));
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", null);

        dialogBuilder.create().show();
    }

    private void addUser() {

        FragmentManager manager = getFragmentManager();
        manager.beginTransaction()
                .add(R.id.add_user_frame, new AddUserFragment(), AddUserFragment.TAG)
                .addToBackStack(AddUserFragment.TAG)
                .commit();
    }

    @Override
    public AndroidTreeView getTreeView() {
        return mTreeView;
    }

    @Override
    public Group getCurrentGroup() {
        return mCurrentGroup;
    }

    @Override
    public TreeNode getCurrentParentTreeNode() {
        return mCurrentParentNode;
    }
}
