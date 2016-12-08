package t.a.m.com.doch1.management.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.UUID;

import t.a.m.com.doch1.Models.Group;
import t.a.m.com.doch1.Models.User;
import t.a.m.com.doch1.R;
import t.a.m.com.doch1.common.validators.EmailValidator;
import t.a.m.com.doch1.common.validators.NotEmptyValidator;
import t.a.m.com.doch1.common.validators.PhoneValidator;
import t.a.m.com.doch1.management.UserHolder;
import t.a.m.com.doch1.views.RoundedImageView;

/**
 * Created by Morad on 12/6/2016.
 */
public class AddUserFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "add_group_fragment";
    private static final int PICK_IMAGE = 123;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_PICK_IMAGE = 2356;

    private NotEmptyValidator mNameValidator, mPersonalIdValidator;
    private PhoneValidator mPhoneValidator;
    private EmailValidator mEmailValidator;
    private RoundedImageView mImageView;
    private Uri mPicUri;
    private ProgressDialog mSavingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_new_user_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNameValidator = new NotEmptyValidator((TextInputLayout) view.findViewById(R.id.name_layout));
        mPersonalIdValidator = new NotEmptyValidator((TextInputLayout) view.findViewById(R.id.personal_id_layout));
        mPhoneValidator = new PhoneValidator((TextInputLayout) view.findViewById(R.id.phone_layout));
        mEmailValidator = new EmailValidator((TextInputLayout) view.findViewById(R.id.email_layout));

        mImageView = (RoundedImageView) view.findViewById(R.id.picture);

        view.findViewById(R.id.save_btn).setOnClickListener(this);
        view.findViewById(R.id.cancel_btn).setOnClickListener(this);
        view.findViewById(R.id.edit_pic).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.save_btn :
                onSaveClicked();
                break;
            case R.id.cancel_btn :
                closeFragment();
                break;

            case R.id.edit_pic :
                onPictureEditButtonClick();
                break;
        }
    }

    private void onSaveClicked() {
        if(mNameValidator.validate() && mEmailValidator.validate()
                && mPersonalIdValidator.validate() && mPhoneValidator.validate())
        {
            mSavingDialog = ProgressDialog.show(getActivity(), "", "Saving...");
            final User user = new User();
            user.setId(UUID.randomUUID().toString());
            user.setName(mNameValidator.getValue());
            user.setEmail(mEmailValidator.getValue());
            user.setPersonalId(mPersonalIdValidator.getValue());
            user.setPhone(mPhoneValidator.getValue());

            if(mPicUri == null){
                saveUser(user);
            }else {

                StorageReference storageRef = FirebaseStorage.getInstance().getReference("profiles");
                StorageReference pic = storageRef.child(user.getId() + ".jpg");
                pic.putFile(mPicUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                user.setImage(taskSnapshot.getDownloadUrl().toString());
                                saveUser(user);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddUserFragment.this.getActivity(), "Error while uploading the image", Toast.LENGTH_LONG).show();
                                saveUser(user);
                            }
                        });
            }
        }
    }

    private void saveUser(final User user){
        FirebaseDatabase.getInstance().getReference("users").child(user.getId()).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Activity activity = AddUserFragment.this.getActivity();
                        if(activity instanceof CurrentDataGetter) {

                            CurrentDataGetter dataGetter = ((CurrentDataGetter) activity);

                            dataGetter.getTreeView()
                                    .addNode(dataGetter.getCurrentParentTreeNode(),
                                            new TreeNode(user).setViewHolder(new UserHolder(AddUserFragment.this.getActivity())));
                        }

                        mSavingDialog.dismiss();
                        closeFragment();
                    }
                });

        Activity activity = AddUserFragment.this.getActivity();
        if(activity instanceof CurrentDataGetter) {

            CurrentDataGetter dataGetter = ((CurrentDataGetter) activity);

            dataGetter.getCurrentGroup().addUser(user.getId());
            FirebaseDatabase.getInstance().getReference("groups").child(dataGetter.getCurrentGroup().getId())
                    .child(Group.USERS_PROPERTY).setValue(dataGetter.getCurrentGroup().getUsers());
        }
    }

    private void closeFragment() {
        FragmentManager manager = getActivity().getFragmentManager();
        Fragment fragment = manager.findFragmentByTag(AddUserFragment.TAG);

        if(fragment != null){
            manager.beginTransaction().remove(fragment).commit();
        }
    }

    private void onPictureEditButtonClick(){
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_PICK_IMAGE);
            }

        }
        else {
            pickImage();
        }
    }

    private void pickImage(){

        Uri tmpUri = Uri.parse(getString(R.string.temp_file_image));

        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tmpUri);
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
            mImageView.setImageURI(mPicUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_PICK_IMAGE){
            pickImage();
        }
//        else if(requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_SET_IMAGE){
//            if(mCurrentUser.getPhotoUrl() != null){
//                mProfilePictureView.setImageURI(mCurrentUser.getPhotoUrl());
//            }
//        }
    }

    public interface CurrentDataGetter{
        AndroidTreeView getTreeView();
        Group getCurrentGroup();
        TreeNode getCurrentParentTreeNode();
    }

}
