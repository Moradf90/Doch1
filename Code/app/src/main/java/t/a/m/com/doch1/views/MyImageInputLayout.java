package t.a.m.com.doch1.views;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;

import t.a.m.com.doch1.R;
import t.a.m.com.doch1.common.utils.ImagePicker;

public class MyImageInputLayout extends RelativeLayout implements View.OnClickListener {

    private static final int PICK_IMAGE = 1233;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_PICK_IMAGE = 36411; // must be 16 bit


    private boolean isEditable;
    private int mDefaultImage;
    private Uri mPicUri;
    private CircleImageView mImageView;
    private ImageButton mEditButton;
    private Fragment mFragmentContainer;
    private IEditCallback mEditingCallback;

    public MyImageInputLayout(Context context) {
        super(context);
    }

    public MyImageInputLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.AppTheme);
    }

    public MyImageInputLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, R.style.AppTheme);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyImageInputLayout, 0, R.style.AppTheme);

        isEditable = a.getBoolean(R.styleable.MyImageInputLayout_editable, false);
        mDefaultImage = a.getResourceId(R.styleable.MyImageInputLayout_image, R.drawable.profile_pic);

        a.recycle();

        init();
    }


    private void init() {
        inflate(getContext(), R.layout.my_image_input_layout, this);

        mImageView = (CircleImageView) findViewById(R.id.picture);
        mEditButton = (ImageButton) findViewById(R.id.edit_pic);

        mEditButton.setVisibility(isEditable ? VISIBLE : GONE);
        mEditButton.setOnClickListener(this);
        mImageView.setImageResource(mDefaultImage);
    }

    public void setFragment(Fragment fragment){
        mFragmentContainer = fragment;
    }

    public void setImageUrl(String url){

        Glide.with(getContext())
                .load(url)
                .centerCrop()
                .fitCenter()
                .error(mDefaultImage)
                .into(mImageView);
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
        mEditButton.setVisibility(isEditable ? VISIBLE : GONE);
    }

    public void setOnImageEditCallback(IEditCallback callback){
        mEditingCallback = callback;
    }

    @Override
    public void onClick(View view) {
        if(getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (mFragmentContainer != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mFragmentContainer.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_PICK_IMAGE);
                }
                else
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_PICK_IMAGE);

            } else {
                pickImage();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            mPicUri = data.getData();
            // In case it's from camera
            if (mPicUri == null) {
                Bitmap bitmap = ImagePicker.getImageFromResult(getContext(), resultCode, data);
                mPicUri = getImageUri(getContext(), bitmap);
            }
            mImageView.setImageURI(mPicUri);

            if(mEditingCallback != null){
                mEditingCallback.onImageEditFinished(this, mPicUri);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FOR_PICK_IMAGE) {
            pickImage();
        }
    }

    private void pickImage() {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(getContext());
        if(mFragmentContainer != null){
            mFragmentContainer.startActivityForResult(chooseImageIntent, PICK_IMAGE);
        }
        else if(getContext() instanceof Activity) {
            ((Activity) getContext()).startActivityForResult(chooseImageIntent, PICK_IMAGE);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "doch1UserImage", null);
        return Uri.parse(path);
    }

    public interface IEditCallback{
        boolean onImageEditFinished(MyImageInputLayout layout, Uri uri);
    }
}
