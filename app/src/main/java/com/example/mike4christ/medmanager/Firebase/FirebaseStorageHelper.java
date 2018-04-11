package com.example.mike4christ.medmanager.Firebase;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class FirebaseStorageHelper {

    private static final String TAG = FirebaseStorageHelper.class.getCanonicalName();

    private FirebaseStorage firebaseStorage;

    private StorageReference rootRef;

    private Context context;

    public FirebaseStorageHelper(Context context){
        this.context = context;
        init();
    }

    private void init(){
        this.firebaseStorage = FirebaseStorage.getInstance();
        rootRef = firebaseStorage.getReferenceFromUrl("gs://medmanager-c6281.appspot.com");
    }

    public void saveProfileImageToCloud(String userId, Uri selectedImageUri, final CircleImageView imageView) {

        StorageReference photoParentRef = rootRef.child(userId);
        StorageReference photoRef = photoParentRef.child(selectedImageUri.getLastPathSegment());
        UploadTask uploadTask = photoRef.putFile(selectedImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "OnFailure " + e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Glide.with(context).load(downloadUrl.getPath()).into(imageView);
            }
        });

    }
}
