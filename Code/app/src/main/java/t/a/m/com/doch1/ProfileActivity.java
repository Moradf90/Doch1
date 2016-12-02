package t.a.m.com.doch1;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import t.a.m.com.doch1.common.validators.NotEmptyValidator;
import t.a.m.com.doch1.views.RoundedImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, OnCompleteListener<Void> {

    private static final int PICK_IMAGE = 1233;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 36411; // must be 16 bit

    private FirebaseUser mCurrentUser;
    private DatabaseReference mUserReference;

    private ImageButton mPicEditButton;
    private RoundedImageView mProfilePictureView;
    private TextView mProfileDisplayName;
    private Button mNameEditButton;
    private TextInputLayout mEditNameInputLayout;
    private NotEmptyValidator mNotEmptyValidator;
    private View mDisplayNameView;

    private Uri mPicUri;
    private String mNewDisplayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle("Profile");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mCurrentUser == null){
            finish();
        }
        else {
            mProfilePictureView = (RoundedImageView) findViewById(R.id.picture);
            mPicEditButton = (ImageButton) findViewById(R.id.edit_pic);
            mProfileDisplayName = (TextView) findViewById(R.id.display_name_text);
            mNameEditButton = (Button) findViewById(R.id.display_name_edit);
            mEditNameInputLayout = (TextInputLayout) findViewById(R.id.name_layout);
            mNotEmptyValidator = new NotEmptyValidator(mEditNameInputLayout);
            mDisplayNameView = findViewById(R.id.display_name_view);

            if(mCurrentUser.getPhotoUrl() != null){
                mProfilePictureView.setImageURI(mCurrentUser.getPhotoUrl());
            }

            if(mCurrentUser.getDisplayName() != null){
                mProfileDisplayName.setText(mCurrentUser.getDisplayName());
                mNotEmptyValidator.setValue(mCurrentUser.getDisplayName());
            } else {
                mProfileDisplayName.setText("Please set your display name");
                mProfileDisplayName.setTextColor(getResources().getColor(R.color.errorColor));
            }

            mNameEditButton.setOnClickListener(this);
            mPicEditButton.setOnClickListener(this);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            mUserReference = database.getReference(mCurrentUser.getUid());
            mUserReference.setValue("skjdsk");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.save_changes){
            onSaveChanges();
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.edit_pic){
            onPictureEditButtonClick();
        }
        else if(view.getId() == R.id.display_name_edit){
            onNameEditButtonClick();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            mPicUri = data.getData();
            mProfilePictureView.setImageURI(mPicUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE){
            pickImage();
        }
    }

    private void onPictureEditButtonClick(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

        }
        else {
            pickImage();
        }
    }

    private void pickImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    private void onNameEditButtonClick() {
        mEditNameInputLayout.setVisibility(View.VISIBLE);
        mDisplayNameView.setVisibility(View.GONE);
    }

    private void onSaveChanges() {

        // hide the keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mDisplayNameView.getWindowToken(), 0);

        UserProfileChangeRequest.Builder profileUpdatesBuilder = new UserProfileChangeRequest.Builder();
        boolean update = false;
        if(mPicUri != null){
            profileUpdatesBuilder.setPhotoUri(mPicUri);
            update = true;
        }

        if(mEditNameInputLayout.getVisibility() == View.VISIBLE){
            if(mNotEmptyValidator.validate()) {
                profileUpdatesBuilder.setDisplayName(mNotEmptyValidator.getValue());
                update = true;
            }
            else return;
        }

        if(update){
            mCurrentUser.updateProfile(profileUpdatesBuilder.build()).addOnCompleteListener(this);
        }
    }

    // on complete saving changes
    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if(task.isSuccessful()){
            mEditNameInputLayout.setVisibility(View.GONE);
            mDisplayNameView.setVisibility(View.VISIBLE);

            mProfileDisplayName.setText(mCurrentUser.getDisplayName());
        }
        else {
            Snackbar.make(mProfilePictureView, "Error while saving to server.", Snackbar.LENGTH_LONG).show();
        }
    }
}
