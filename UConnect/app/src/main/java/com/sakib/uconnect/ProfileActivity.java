package com.sakib.uconnect;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.sakib.uconnect.model.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private Context mContext = ProfileActivity.this;
    private static final String TAG = "ProfileActivity";

    private ImageView profileImage;
    private ImageView editImage;
    private TextInputLayout txtInputLayoutName, txtInputLayoutMobile;

    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;

    private static final int CAMERA_REQUEST = 1888, IMG_REQUEST = 1;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private String  mCurrentPhotoPath = " ";
    private Uri photoUri;
    private StorageTask storageTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        profileImage = findViewById(R.id.pro_profile_image);
        editImage = findViewById(R.id.pro_edit_image);
        txtInputLayoutName = findViewById(R.id.pro_txt_il_name);
        txtInputLayoutMobile =  findViewById(R.id.pro_txt_il_mobile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        storageReference = FirebaseStorage.getInstance().getReference("uploads/profile_picture");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                txtInputLayoutName.getEditText().setText(user.getName());
                txtInputLayoutMobile.getEditText().setText(user.getMobileNumber());

                if(user.getImageUrl().equals("default")){
                    profileImage.setImageResource(R.drawable.blank_pro_pic);
                }
                else {
                    Glide.with(mContext.getApplicationContext()).load(user.getImageUrl()).into(profileImage);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });



    }

/*    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                takePic();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }


    }*/

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = mContext.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){


        if(photoUri !=null){
            final StorageReference  fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(photoUri));
            storageTask = fileReference.putFile(photoUri);
            Log.d(TAG, "uploadImage: "+photoUri);

            storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        if (downloadUri != null) {
                            String mUri = downloadUri.toString();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("imageUrl",""+mUri);
                            Log.d(TAG, "onComplete: "+mUri);
                            reference.updateChildren(hashMap);
                        }



                    }else {
                        Toast.makeText(mContext,"Failed to upload!"+task.getException(),Toast.LENGTH_SHORT).show();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mContext,"Failed!",Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            Toast.makeText(mContext,"No image selected!",Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: request code+result code" + requestCode + "    " + resultCode + data);
        /*if (bitmap != null) {
            bitmap.recycle();
        }*/

        /*if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: " + mCurrentPhotoPath);

            //  bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            Log.d(TAG, "onActivityResult: path " + photoURI);

            Glide.with(this).load(photoURI).into(image_view_display_pic);

            // Initialize a new ByteArrayStream
            ByteArrayOutputStream stream = new ByteArrayOutputStream();


            Log.d(TAG, "onActivityResult: path " + photoURI);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //  Log.d(TAG, "onActivityResult: " + bitmap.getAllocationByteCount());

                if (photoURI != null) {
                    try (final InputStream inputStream = getContentResolver().openInputStream(photoURI)) {
                        final TranscodeOptions transcodeOptions =
                                TranscodeOptions.Builder(new EncodeRequirement(JPEG, 80))
                                        .resize(ResizeRequirement.Mode.EXACT_OR_SMALLER, new ImageSize(1024, 1024))
                                        .build();
                        if (inputStream != null) {
                            Log.d(TAG, "onActivityResult: before compressing  : " + inputStream.toString());
                        }

                        final SpectrumResult result = mSpectrum.transcode(
                                EncodedImageSource.from(inputStream),
                                EncodedImageSink.from(stream),
                                transcodeOptions,
                                "upload_flow_callsite_identifier");
                        byte[] bitmapdata = stream.toByteArray();
                        bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                        Log.d(TAG, "onActivityResult:spectrum result " + bitmap.getAllocationByteCount());

                    } catch (final IOException e) {
                        Log.d(TAG, "onActivityResult: " + e);
                        // e.g. file not found
                    } catch (final SpectrumException e) {
                        Log.d(TAG, "onActivityResult: spectrum err" + e);
                        // e.g. invalid input image
                    }
                }

            }


            tv_no_chosen.setVisibility(View.GONE);
            image_view_display_pic.setVisibility(View.VISIBLE);
            //  bitmap =  ((BitmapDrawable)image_view_display_pic.getDrawable()).getBitmap();
            btn_img_upload.setEnabled(true);
            btn_img_upload.setAlpha((float) 1.0);

            Log.d(TAG, "onActivityResult: Image displayed successful");


        }*/

        if (requestCode == IMG_REQUEST && resultCode == Activity.RESULT_OK && data != null) {

            photoUri = data.getData();

            Log.d(TAG, "onActivityResult: " + photoUri);

            // Initialize a new ByteArrayStream
            //ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Glide.with(mContext.getApplicationContext()).load(photoUri).into(profileImage);

            if(storageTask!=null && storageTask.isInProgress()){
                Toast.makeText(mContext,"Upload is in progress",Toast.LENGTH_SHORT).show();
            }
            else {
                uploadImage();
            }



/*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                if (path != null) {
                    try (final InputStream inputStream = getContentResolver().openInputStream(path)) {
                        final TranscodeOptions transcodeOptions =
                                TranscodeOptions.Builder(new EncodeRequirement(JPEG, 80))
                                        .resize(ResizeRequirement.Mode.EXACT_OR_SMALLER, new ImageSize(1024, 1024))
                                        .build();
                        if (inputStream != null) {
                            Log.d(TAG, "onActivityResult: before compressing  : " + inputStream.toString());
                        }

                        final SpectrumResult result = mSpectrum.transcode(
                                EncodedImageSource.from(inputStream),
                                EncodedImageSink.from(stream),
                                transcodeOptions,
                                "upload_flow_callsite_identifier");
                        byte[] bitmapdata = stream.toByteArray();
                        bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                        Log.d(TAG, "onActivityResult:spectrum result " + bitmap.getAllocationByteCount() + result.getClass());

                    } catch (final IOException e) {
                        Log.d(TAG, "onActivityResult: " + e);
                        // e.g. file not found
                    } catch (final SpectrumException e) {
                        Log.d(TAG, "onActivityResult: spectrum err" + e);
                        // e.g. invalid input image
                    }
                }

            }
            tv_no_chosen.setVisibility(View.GONE);
            image_view_display_pic.setVisibility(View.VISIBLE);


            btn_img_upload.setEnabled(true);
            btn_img_upload.setAlpha((float) 1.0);
*/

        }


    }

}
