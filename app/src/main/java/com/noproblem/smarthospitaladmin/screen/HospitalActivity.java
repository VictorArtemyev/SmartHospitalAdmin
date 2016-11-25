package com.noproblem.smarthospitaladmin.screen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.noproblem.smarthospitaladmin.BaseActivity;
import com.noproblem.smarthospitaladmin.util.ImagePicker;
import com.noproblem.smarthospitaladmin.R;
import com.noproblem.smarthospitaladmin.model.Hospital;
import com.noproblem.smarthospitaladmin.model.Image;
import com.noproblem.smarthospitaladmin.model.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by Victor Artemyev on 24/11/2016.
 */

public class HospitalActivity extends BaseActivity {

    private static final String TAG = HospitalActivity.class.getSimpleName();

    private static final String REFERENCE_STORAGE_URL = "gs://smart-hospital-e63e4.appspot.com";

    public static Intent getStartIntent(Context context) {
        return new Intent(context, HospitalActivity.class);
    }

    DatabaseReference mDatabase;
    StorageReference mStorage;

    ImageView mImageView;
    EditText mNameEditText;
    EditText mDescriptionEditText;
    EditText mAddressEditText;
    EditText mLongitudeEditText;
    EditText mLatitudeEditText;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReferenceFromUrl(REFERENCE_STORAGE_URL);

        mImageView = (ImageView) findViewById(R.id.image_view);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                ImagePicker.openGallery(HospitalActivity.this);
            }
        });

        mNameEditText = (EditText) findViewById(R.id.edit_text_title);
        mDescriptionEditText = (EditText) findViewById(R.id.edit_text_description);
        mAddressEditText = (EditText) findViewById(R.id.edit_text_address);
        mLongitudeEditText = (EditText) findViewById(R.id.edit_text_longitude);
        mLatitudeEditText = (EditText) findViewById(R.id.edit_text_latitude);

        findViewById(R.id.button_add).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                writeNewHospital();
            }
        });
    }

    Image mImage;

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImagePicker.handleActivityResult(requestCode, resultCode, data, HospitalActivity.this,
                new ImagePicker.OnImagePickedListener() {
                    @Override public void onImagePicked(File imageFile, String source) {
                        uploadFile(imageFile);
                    }

                    @Override public void onCanceled(String source) {
                    }

                    @Override public void onError(Throwable throwable, String source) {
                        Log.e(TAG, "Pick image error:", throwable);
                    }
                });
    }

    void uploadFile(File file) {
        try {
            showProgressDialog();
            InputStream stream = new FileInputStream(file);
            final String imageId = UUID.randomUUID().toString();
            StorageReference storageReference = mStorage.child(imageId);
            UploadTask uploadTask = storageReference.putStream(stream);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "uploadFile", e);
                    showToast(getString(R.string.error_upload_image));
                    hideProgressDialog();
                }
            });

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mImage = new Image(imageId, taskSnapshot.getDownloadUrl().toString());
                    Glide.with(HospitalActivity.this)
                            .load(mImage.getUrl())
                            .centerCrop()
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    hideProgressDialog();
                                    return false;
                                }

                                @Override public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    hideProgressDialog();
                                    return false;
                                }
                            })
                            .into(mImageView);
                }
            });
        } catch (FileNotFoundException e) {
            Log.e(TAG, "uploadFile", e);
            showToast(getString(R.string.error_upload_image));
        }
    }

    void writeNewHospital() {
        showProgressDialog();
        String key = mDatabase.child("hospitals").push().getKey();
        Location location = new Location(getAddress(), getLongitude(), getLatitude());
        Hospital hospital = new Hospital(key, getName(), getDescription(), mImage, location);
        mDatabase.child("hospitals").child(key).setValue(hospital)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override public void onSuccess(Void aVoid) {
                hideProgressDialog();
                showToast(getString(R.string.success_hospital_created));
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Write new hospital", e);
                hideProgressDialog();
            }
        });
    }

    String getName() {
        return mNameEditText.getText().toString().trim();
    }

    String getDescription() {
        return mDescriptionEditText.getText().toString().trim();
    }

    String getAddress() {
        return mAddressEditText.getText().toString().trim();
    }

    double getLongitude() {
        String value = mLongitudeEditText.getText().toString().trim();
        return TextUtils.isEmpty(value) ? 0.0 : Double.valueOf(value);
    }

    double getLatitude() {
        String value = mLatitudeEditText.getText().toString().trim();
        return TextUtils.isEmpty(value) ? 0.0 : Double.valueOf(value);
    }

    void showToast(String message) {
        Toast.makeText(HospitalActivity.this, message, Toast.LENGTH_LONG).show();
    }

}
