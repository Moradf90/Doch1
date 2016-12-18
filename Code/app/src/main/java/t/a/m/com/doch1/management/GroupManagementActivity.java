package t.a.m.com.doch1.management;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import t.a.m.com.doch1.Models.Group;
import t.a.m.com.doch1.Models.User;
import t.a.m.com.doch1.R;
import t.a.m.com.doch1.common.utils.ImagePicker;
import t.a.m.com.doch1.common.validators.NotEmptyValidator;
import t.a.m.com.doch1.management.autocomplete.AutoCompleteGroupsAdapter;
import t.a.m.com.doch1.management.autocomplete.AutoCompleteUsersAdapter;
import t.a.m.com.doch1.views.CircleImageView;

public class GroupManagementActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_PICK_IMAGE = 23564;
    private static final int PICK_IMAGE = 2423;

    private CircleImageView mGroupImageView;
    private ImageButton mGroupImageEditButton;
    private LinearLayout mGroupNameDisplayView;
    private TextView mGroupNameViewMode;
    private ImageButton mGroupNameEditButton;
    private TextInputLayout mGroupNameEditorLayout;
    private NotEmptyValidator mGroupNameValidator;
    private Switch mSubGroupSwitch;
    private RelativeLayout mSubGroupLayout;
    private CircleImageView mSubGroupImageView;
    private TextView mSubGroupName;
    private TextInputLayout mSubGroupSearchLayout;
    private AutoCompleteTextView mSearchGroupTextView;
    private AutoCompleteGroupsAdapter mAutoCompleteGroupsAdapter;
    private AutoCompleteTextView mSearchUserTextView;
    private AutoCompleteUsersAdapter mAutoCompleteUsersAdapter;
    private RecyclerView mMembersRecycler;
    private UsersAdapter mMembersAdapter;

    private Uri mPicUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_management);

        mGroupImageEditButton = (ImageButton) findViewById(R.id.edit_pic);
        mGroupImageEditButton.setOnClickListener(this);
        mGroupImageView = (CircleImageView) findViewById(R.id.picture);

        mGroupNameDisplayView = (LinearLayout) findViewById(R.id.display_name_view);
        mGroupNameViewMode = (TextView) findViewById(R.id.group_name);
        mGroupNameEditButton = (ImageButton) findViewById(R.id.group_name_edit);
        mGroupNameEditButton.setOnClickListener(this);
        mGroupNameEditorLayout = (TextInputLayout) findViewById(R.id.name_layout);
        mGroupNameValidator = new NotEmptyValidator(mGroupNameEditorLayout);

        mSubGroupSwitch = (Switch) findViewById(R.id.sub_group_switch);
        mSubGroupSwitch.setOnClickListener(this);
        mSubGroupLayout = (RelativeLayout) findViewById(R.id.selected_sub_group_view);
        mSubGroupImageView = (CircleImageView) findViewById(R.id.sub_group_image);
        mSubGroupName = (TextView) findViewById(R.id.sub_group_name);

        mSubGroupSearchLayout = (TextInputLayout) findViewById(R.id.sub_group_search);
        mSearchGroupTextView = (AutoCompleteTextView) findViewById(R.id.search_group);
        mSearchGroupTextView.setThreshold(2);
        mAutoCompleteGroupsAdapter = new AutoCompleteGroupsAdapter(this);
        mSearchGroupTextView.setAdapter(mAutoCompleteGroupsAdapter);
        mSearchGroupTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSearchGroupTextView.setText("");
                Group group = mAutoCompleteGroupsAdapter.getItem(i);
                mSubGroupLayout.setVisibility(View.VISIBLE);
                mSubGroupName.setText(group.getName());

                mSubGroupImageView.setImageDrawable(getDrawable(R.drawable.profile_group_pic));
                if(group.getImage() != null){
                    Picasso.with(GroupManagementActivity.this).load(group.getImage())
                            .placeholder(R.drawable.profile_group_pic)
                            .into(mSubGroupImageView);
                }
                // hide the keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchUserTextView.getWindowToken(), 0);
            }
        });
        mSearchGroupTextView.setDropDownWidth(500);

        mSearchUserTextView = (AutoCompleteTextView) findViewById(R.id.search_text_view);
        mSearchUserTextView.setThreshold(2);
        mAutoCompleteUsersAdapter = new AutoCompleteUsersAdapter(this);
        mSearchUserTextView.setAdapter(mAutoCompleteUsersAdapter);
        mSearchUserTextView.setOnItemClickListener(this);
        mSearchUserTextView.setDropDownWidth(500);

        mMembersRecycler = (RecyclerView) findViewById(R.id.members);
        mMembersAdapter = new UsersAdapter(this);
        mMembersRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMembersRecycler.setAdapter(mMembersAdapter);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final User mig = mMembersAdapter.getItem(position);
                    AlertDialog dialog = new AlertDialog.Builder(GroupManagementActivity.this)
                            .setTitle("Removing " + mig.getName())
                            .setMessage(String.format("Are you sure you want to remove %s", mig.getName()))
                            .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mMembersAdapter.remove(position);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mMembersAdapter.notifyDataSetChanged();
                                }
                            })
                            .create();

                    dialog.show();
                }

        });

        touchHelper.attachToRecyclerView(mMembersRecycler);

        Intent intent = getIntent();
        //String groupId = "21827933-d057-4ada-a51e-816cd46a586d" ;
        String groupId = intent.getStringExtra("groupId");

        if(groupId == null) // add new group
        {
            initNewGroup();
        }
        else {
            initExistingGroup(groupId);
        }
    }

    private void initExistingGroup(String groupId) {
        FirebaseDatabase.getInstance().getReference(Group.GROUPS_REFERENCE_KEY)
                .child(groupId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Group group = dataSnapshot.getValue(Group.class);
                            initUi(group);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(GroupManagementActivity.this, "Invalid group id", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void initUi(Group group) {
        if(group.getImage() != null){
            Picasso.with(this).load(group.getImage())
                    .placeholder(R.drawable.profile_group_pic)
                    .into(mGroupImageView);
        }
        mGroupNameDisplayView.setVisibility(View.VISIBLE);
        mGroupNameEditorLayout.setVisibility(View.GONE);
        mGroupNameViewMode.setText(group.getName());

        mSubGroupSwitch.setVisibility(View.GONE);
        mSubGroupSearchLayout.setVisibility(View.GONE);
        mSubGroupLayout.setVisibility(View.GONE);
        if(group.getParentId() != null && !group.getParentId().equals("-1")){
            FirebaseDatabase.getInstance().getReference(Group.GROUPS_REFERENCE_KEY)
                    .child(group.getParentId().toString())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                Group parent = dataSnapshot.getValue(Group.class);
                                mSubGroupLayout.setVisibility(View.VISIBLE);
                                mSubGroupName.setText(parent.getName());
                                if(parent.getImage()!= null){
                                    Picasso.with(GroupManagementActivity.this)
                                            .load(parent.getImage())
                                            .placeholder(R.drawable.profile_group_pic)
                                            .into(mSubGroupImageView);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

    }

    private void initNewGroup() {
        mGroupNameDisplayView.setVisibility(View.GONE);
        mGroupNameEditorLayout.setVisibility(View.VISIBLE);
        mGroupImageEditButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAutoCompleteUsersAdapter.destroy();
    }


    // user clicked on auto complete text view
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        User user = mAutoCompleteUsersAdapter.getItem(i);
        mMembersAdapter.add(user);
        mSearchUserTextView.setText("");
        // hide the keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchUserTextView.getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sub_group_switch: {
                if(mSubGroupSwitch.isChecked()){
                    mSubGroupSearchLayout.setVisibility(View.VISIBLE);
                }
                else {
                    mSubGroupLayout.setVisibility(View.GONE);
                    mSubGroupSearchLayout.setVisibility(View.GONE);
                }
                break;
            }
            case R.id.group_name_edit :{

            }
            case R.id.edit_pic:
                onPictureEditButtonClick();
                break;
        }
    }

    private void onPictureEditButtonClick() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_PICK_IMAGE);

        } else {
            Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
            startActivityForResult(chooseImageIntent, PICK_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_PICK_IMAGE) {
            pickImage();
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {

            if (data == null) {
                return;
            }
            mPicUri = data.getData();
            // In case it's from camera
            if (mPicUri == null) {
                Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                mPicUri = getImageUri(this, bitmap);
            }
            mGroupImageView.setImageURI(mPicUri);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "doch1UserImage", null);
        return Uri.parse(path);
    }
}
