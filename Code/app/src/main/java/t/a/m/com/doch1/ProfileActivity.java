package t.a.m.com.doch1;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;

import t.a.m.com.doch1.common.validators.EmailValidator;
import t.a.m.com.doch1.common.validators.NotEmptyValidator;
import t.a.m.com.doch1.views.RoundedImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE = 1233;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_PICK_IMAGE = 36411; // must be 16 bit
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_SET_IMAGE = 36302;

    private FirebaseUser mCurrentUser;

    private ImageButton mPicEditButton;
    private RoundedImageView mProfilePictureView;
    private TextView mProfileDisplayName;
    private ImageButton mNameEditButton;
    private TextInputLayout mEditNameInputLayout;
    private NotEmptyValidator mNotEmptyValidator;
    private View mDisplayNameView;
    private TextView mProfileEmail;
    private ImageButton mEmailEditButton;
    private TextInputLayout mEditEmailInputLayout;
    private EmailValidator mEmailValidator;
    private View mEmailView;

    private Uri mPicUri;

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
            mNameEditButton = (ImageButton) findViewById(R.id.display_name_edit);
            mEditNameInputLayout = (TextInputLayout) findViewById(R.id.name_layout);
            mNotEmptyValidator = new NotEmptyValidator(mEditNameInputLayout);
            mDisplayNameView = findViewById(R.id.display_name_view);

            mProfileEmail = (TextView) findViewById(R.id.email_text);
            mEmailEditButton = (ImageButton) findViewById(R.id.email_edit);
            mEditEmailInputLayout = (TextInputLayout) findViewById(R.id.email_layout);
            mEmailValidator = new EmailValidator(mEditEmailInputLayout);
            mEmailView = findViewById(R.id.email_view);

            if(mCurrentUser.getPhotoUrl() != null){
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_SET_IMAGE);

                }
                else
                    mProfilePictureView.setImageURI(mCurrentUser.getPhotoUrl());
            }

            if(mCurrentUser.getDisplayName() != null){
                mProfileDisplayName.setText(mCurrentUser.getDisplayName());
                mNotEmptyValidator.setValue(mCurrentUser.getDisplayName());
            } else {
                mProfileDisplayName.setText(getResources().getString(R.string.no_display_name));
                mProfileDisplayName.setTextColor(getResources().getColor(R.color.errorColor));
            }

            mProfileEmail.setText(mCurrentUser.getEmail());
            mEmailValidator.setValue(mCurrentUser.getEmail());

            mNameEditButton.setOnClickListener(this);
            mPicEditButton.setOnClickListener(this);
            mEmailEditButton.setOnClickListener(this);
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

        switch (view.getId()){
            case R.id.edit_pic :
                onPictureEditButtonClick();
                break;

            case R.id.display_name_edit :
                onNameEditButtonClick();
                break;

            case R.id.email_edit :
                onEmailEditButtonClick();
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
        if(requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_PICK_IMAGE){
            pickImage();
        }
        else if(requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_SET_IMAGE){
            if(mCurrentUser.getPhotoUrl() != null){
                mProfilePictureView.setImageURI(mCurrentUser.getPhotoUrl());
            }
        }
    }

    private void onPictureEditButtonClick(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_PICK_IMAGE);

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

    private void onEmailEditButtonClick() {
        mEditEmailInputLayout.setVisibility(View.VISIBLE);
        mEmailView.setVisibility(View.GONE);
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

        if (mEmailValidator.validate() && !mCurrentUser.getEmail().equals(mEmailValidator.getValue())){

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle("Password to re-authenticate");

            final EditText editText = new EditText(this);
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            dialogBuilder.setView(editText);

            dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String password = editText.getText().toString();
                    mCurrentUser.reauthenticate(EmailAuthProvider
                            .getCredential(mCurrentUser.getEmail(), password))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful()){
                                        Snackbar.make(mProfilePictureView, "Invalid password", Snackbar.LENGTH_LONG).show();
                                        return;
                                    }
                                    mCurrentUser.updateEmail(mEmailValidator.getValue())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        mEditEmailInputLayout.setVisibility(View.GONE);
                                                        mEmailView.setVisibility(View.VISIBLE);
                                                        mProfileEmail.setText(mCurrentUser.getEmail());
                                                    }
                                                    else {
                                                        Snackbar.make(mProfilePictureView, "Error while updating the email", Snackbar.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                }
                            });
                }
            });

            dialogBuilder.create().show();
        }

        if(update){
            mCurrentUser.updateProfile(profileUpdatesBuilder.build())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
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
                    });
        }
    }
}
