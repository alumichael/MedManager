/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    private StorageReference rootRef;

    private final Context context;

    public FirebaseStorageHelper(Context context){
        this.context = context;
        init();
    }

    private void init(){
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
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
