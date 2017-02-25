package t.a.m.com.doch1.profile;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import t.a.m.com.doch1.Models.User;
import t.a.m.com.doch1.R;
import t.a.m.com.doch1.views.MyImageInputLayout;
import t.a.m.com.doch1.views.MyTextInputLayout;

public class ProfileFragment extends Fragment implements MyTextInputLayout.IValidator, MyTextInputLayout.IEditCallback, MyImageInputLayout.IEditCallback {

    private static ProfileFragment mInstance;

    public static Fragment of(long id) {

        instance();
        mInstance.setUserId(id);
        return mInstance;
    }

    public static Fragment instance(){
        if(mInstance == null){
            mInstance = new ProfileFragment();
        }
        return mInstance;
    }

    // data members
    private User mUser;
    private long mUserId;
    private boolean mIsCreated;

    // view members
    private MyImageInputLayout mUserImageView;
    private MyTextInputLayout mUserNameInputView;
    private MyTextInputLayout mUserEmailInputView;
    private MyTextInputLayout mUserPhoneInputView;

    private ProfileFragment() {
        mUserId = -1;
    }


    private void setUserId(long id) {
        if(mUserId != id) {
            mUserId = id;
            mUser = User.load(User.class, mUserId);
            refresh();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mIsCreated = true;
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUserImageView = (MyImageInputLayout) view.findViewById(R.id.profile_image);
        mUserNameInputView = (MyTextInputLayout) view.findViewById(R.id.profile_name);
        mUserEmailInputView =(MyTextInputLayout) view.findViewById(R.id.profile_email);
        mUserPhoneInputView =(MyTextInputLayout) view.findViewById(R.id.profile_phone);
        mUserImageView.setFragment(this);
        mUserNameInputView.setValidator(this);
        mUserEmailInputView.setValidator(this);
        mUserPhoneInputView.setValidator(this);
        mUserImageView.setOnImageEditCallback(this);
        mUserNameInputView.setOnEditCallback(this);
        mUserEmailInputView.setOnEditCallback(this);
        mUserPhoneInputView.setOnEditCallback(this);
        refresh();
    }

    private void refresh() {
        if(mIsCreated) {
            // update the title of the ACTIVITY
            getActivity().setTitle(mUser.getName());

            mUserImageView.setImageUrl(mUser.getImage());
            mUserNameInputView.setText(mUser.getName());
            mUserEmailInputView.setText(mUser.getEmail());
            mUserPhoneInputView.setText(mUser.getPhone());

            boolean editable = mUser.isCurrentUser(getActivity());

            mUserNameInputView.view();
            mUserEmailInputView.view();
            mUserPhoneInputView.view();
            mUserImageView.setEditable(editable);
            mUserNameInputView.setEditable(editable);
            mUserEmailInputView.setEditable(editable);
            mUserPhoneInputView.setEditable(editable);
        }
    }

    @Override
    public boolean validate(MyTextInputLayout layout, CharSequence value) {

        switch (layout.getId()){
            case R.id.profile_email : return Patterns.EMAIL_ADDRESS.matcher(value).matches();
            case R.id.profile_phone : return value.toString().matches("\\d{10}") || value.toString().matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}");
        }

        return true;
    }

    @Override
    public boolean onEditFinished(MyTextInputLayout layout) {
        switch (layout.getId()){
            case R.id.profile_name : afterNameEditing(layout); break;
            case R.id.profile_email : afterEmailEditing(layout); break;
            case R.id.profile_phone : afterPhoneEditing(layout); break;
        }

        return true;
    }

    private void afterPhoneEditing(MyTextInputLayout layout) {
        String phone = layout.getText().toString();
        mUser.setPhone(phone);
        mUser.save();

        FirebaseDatabase.getInstance()
                .getReference(User.USERS_REFERENCE_KEY)
                .child(mUser.getId().toString())
                .child(User.PHONE_PROPERTY)
                .setValue(phone);
    }

    private void afterNameEditing(final MyTextInputLayout layout) {

        UserProfileChangeRequest.Builder profileUpdatesBuilder = new UserProfileChangeRequest.Builder();
        final String name = layout.getText().toString();
        profileUpdatesBuilder.setDisplayName(name);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUser.updateProfile(profileUpdatesBuilder.build())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mUser.setName(name);
                            mUser.save();

                            FirebaseDatabase.getInstance()
                                    .getReference(User.USERS_REFERENCE_KEY)
                                    .child(mUser.getId().toString())
                                    .child(User.NAME_PROPERTY)
                                    .setValue(name);
                        } else {
                            Snackbar.make(layout, "Error while saving to server.", Snackbar.LENGTH_LONG).show();
                            layout.setText(mUser.getName());
                        }
                    }
                });


    }

    private void afterEmailEditing(final MyTextInputLayout layout) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle("Password to re-authenticate");

        final EditText editText = new EditText(getActivity());
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        dialogBuilder.setView(editText);

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String password = editText.getText().toString();
                final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                currentUser.reauthenticate(EmailAuthProvider
                        .getCredential(currentUser.getEmail(), password))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    layout.setText(mUser.getEmail());
                                    Snackbar.make(layout, "Invalid password", Snackbar.LENGTH_LONG).show();
                                    return;
                                }
                                final String newEmail = layout.getText().toString();
                                currentUser.updateEmail(newEmail)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    mUser.setEmail(newEmail);
                                                    mUser.save();

                                                    FirebaseDatabase.getInstance()
                                                            .getReference(User.USERS_REFERENCE_KEY)
                                                            .child(mUser.getId().toString())
                                                            .child(User.EMAIL_PROPERTY)
                                                            .setValue(newEmail);

                                                } else {
                                                    layout.setText(mUser.getEmail());
                                                    Snackbar.make(layout, "Error while saving email", Snackbar.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                        });
            }
        });

        dialogBuilder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUserImageView.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mUserImageView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onImageEditFinished(final MyImageInputLayout layout, Uri uri) {

        // upload the image to sserver
        StorageReference profilesRef = FirebaseStorage.getInstance().getReference("profiles");

        //TODO : resize the image before uploading ...

        UploadTask uploadTask = profilesRef.child(mUser.getId() + "_" + System.currentTimeMillis() + ".jpg").putFile(uri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Snackbar.make(layout, "Error while uploading the photo to server", BaseTransientBottomBar.LENGTH_LONG).show();
                mUserImageView.setImageUrl(mUser.getImage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                mUser.setImage(downloadUrl.toString());
                mUser.save();

                try {
                    FirebaseStorage.getInstance().getReferenceFromUrl(mUser.getImage()).delete();
                }
                catch (Exception e){

                }

                FirebaseDatabase.getInstance()
                        .getReference(User.USERS_REFERENCE_KEY)
                        .child(mUser.getId().toString())
                        .child(User.IMAGE_PROPERTY)
                        .setValue(downloadUrl.toString());
            }
        });


        return false;
    }
}
