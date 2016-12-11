package t.a.m.com.doch1;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.ByteArrayOutputStream;

import t.a.m.com.doch1.common.utils.ImagePicker;
import t.a.m.com.doch1.common.validators.EmailValidator;
import t.a.m.com.doch1.common.validators.NotEmptyValidator;
import t.a.m.com.doch1.views.RoundedImageView;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final int PICK_IMAGE = 1233;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_PICK_IMAGE = 36411; // must be 16 bit
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_SET_IMAGE = 36302;

    private FirebaseUser mCurrentUser;

    private RoundedImageView mProfilePictureView;
    private TextView mProfileDisplayName;
    private TextInputLayout mEditNameInputLayout;
    private NotEmptyValidator mNotEmptyValidator;
    private View mDisplayNameView;
    private TextView mProfileEmail;
    private TextInputLayout mEditEmailInputLayout;
    private EmailValidator mEmailValidator;
    private View mEmailView;

    private Uri mPicUri;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vFragmentLayout = inflater.inflate(R.layout.activity_profile, container, false);


        getActivity().setTitle("Profile");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mCurrentUser == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        } else {
            mProfilePictureView = (RoundedImageView) vFragmentLayout.findViewById(R.id.picture);
            mProfileDisplayName = (TextView) vFragmentLayout.findViewById(R.id.display_name_text);
            ImageButton mNameEditButton = (ImageButton) vFragmentLayout.findViewById(R.id.display_name_edit);
            mEditNameInputLayout = (TextInputLayout) vFragmentLayout.findViewById(R.id.name_layout);
            ImageButton mPicEditButton = (ImageButton) vFragmentLayout.findViewById(R.id.edit_pic);

            mNotEmptyValidator = new NotEmptyValidator(mEditNameInputLayout);
            mDisplayNameView = vFragmentLayout.findViewById(R.id.display_name_view);

            mProfileEmail = (TextView) vFragmentLayout.findViewById(R.id.email_text);
            ImageButton mEmailEditButton = (ImageButton) vFragmentLayout.findViewById(R.id.email_edit);
            mEditEmailInputLayout = (TextInputLayout) vFragmentLayout.findViewById(R.id.email_layout);
            mEmailValidator = new EmailValidator(mEditEmailInputLayout);
            mEmailView = vFragmentLayout.findViewById(R.id.email_view);

            if (mCurrentUser.getPhotoUrl() != null) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_SET_IMAGE);
                    }

                } else
                    mProfilePictureView.setImageURI(mCurrentUser.getPhotoUrl());
            }

            if (mCurrentUser.getDisplayName() != null) {
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

        return vFragmentLayout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_activity_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.save_changes) {
            onSaveChanges();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.edit_pic:
                onPictureEditButtonClick();
                break;

            case R.id.display_name_edit:
                onNameEditButtonClick();
                break;

            case R.id.email_edit:
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
            // In case it's from camera
            if (mPicUri == null) {
                Bitmap bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data);
                mPicUri = getImageUri(getActivity(), bitmap);
            }
            mProfilePictureView.setImageURI(mPicUri);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "doch1UserImage", null);
        return Uri.parse(path);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_PICK_IMAGE) {
            pickImage();
        } else if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_SET_IMAGE) {
            if (mCurrentUser.getPhotoUrl() != null) {
                mProfilePictureView.setImageURI(mCurrentUser.getPhotoUrl());
            }
        }
    }

    private void onPictureEditButtonClick() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_PICK_IMAGE);

        } else {
            Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity());
            startActivityForResult(chooseImageIntent, PICK_IMAGE);
//            pickImage();
        }
    }

    private void pickImage() {
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
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mDisplayNameView.getWindowToken(), 0);

        UserProfileChangeRequest.Builder profileUpdatesBuilder = new UserProfileChangeRequest.Builder();
        boolean update = false;
        if (mPicUri != null) {
            profileUpdatesBuilder.setPhotoUri(mPicUri);
            update = true;
        }

        if (mEditNameInputLayout.getVisibility() == View.VISIBLE) {
            if (mNotEmptyValidator.validate()) {
                profileUpdatesBuilder.setDisplayName(mNotEmptyValidator.getValue());
                update = true;
            } else return;
        }

        if (mEmailValidator.validate() && mCurrentUser.getEmail() != null && !mCurrentUser.getEmail().equals(mEmailValidator.getValue())) {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setTitle("Password to re-authenticate");

            final EditText editText = new EditText(getActivity());
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
                                    if (!task.isSuccessful()) {
                                        Snackbar.make(mProfilePictureView, "Invalid password", Snackbar.LENGTH_LONG).show();
                                        return;
                                    }
                                    mCurrentUser.updateEmail(mEmailValidator.getValue())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        mEditEmailInputLayout.setVisibility(View.GONE);
                                                        mEmailView.setVisibility(View.VISIBLE);
                                                        mProfileEmail.setText(mCurrentUser.getEmail());

                                                        // This line updates the header in the navigation drawer - the profile data.
//                                                        ((DrawerActivity)getActivity()).updateProfileInDrawer(mCurrentUser);
                                                    } else {
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

        if (update) {
            mCurrentUser.updateProfile(profileUpdatesBuilder.build())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mEditNameInputLayout.setVisibility(View.GONE);
                                mDisplayNameView.setVisibility(View.VISIBLE);

                                mProfileDisplayName.setText(mCurrentUser.getDisplayName());

                                // This line updates the header in the navigation drawer - the profile data.
//                                ((DrawerActivity)getActivity()).updateProfileInDrawer(mCurrentUser);
                            } else {
                                Snackbar.make(mProfilePictureView, "Error while saving to server.", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}
