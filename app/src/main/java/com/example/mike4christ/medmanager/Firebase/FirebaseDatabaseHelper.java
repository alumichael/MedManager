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

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.mike4christ.medmanager.Adapters.RecyclerViewAdapter;
import com.example.mike4christ.medmanager.Helper.Helper;
import com.example.mike4christ.medmanager.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseDatabaseHelper {

    private static final String TAG = FirebaseDatabaseHelper.class.getSimpleName();

    private final DatabaseReference databaseReference;

    public FirebaseDatabaseHelper(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void createUserInFirebaseDatabase(String userId, FirebaseUserEntity firebaseUserEntity){
        Map<String, FirebaseUserEntity> user = new HashMap<String, FirebaseUserEntity>();
        user.put(userId, firebaseUserEntity);
        databaseReference.child("users").setValue(user);
    }

    public void isUserKeyExist(final String uid, final Context context, final RecyclerView recyclerView){
        databaseReference.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                System.out.println("User login 1 " + dataSnapshot.getKey() + " " + dataSnapshot.getValue());
                List<UserProfile> userData = adapterSourceData(dataSnapshot, uid);
                System.out.println("User login Size " + userData.size());
                RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(context, userData);
                recyclerView.setAdapter(recyclerViewAdapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                List<UserProfile> userData = adapterSourceData(dataSnapshot, uid);
                System.out.println("User login Size " + userData.size());
                RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(context, userData);
                recyclerView.setAdapter(recyclerViewAdapter);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private List<UserProfile> adapterSourceData(DataSnapshot dataSnapshot, String uId){
        List<UserProfile> allUserData = new ArrayList<UserProfile>();
        if(dataSnapshot.getKey().equals(uId)){
            FirebaseUserEntity userInformation = dataSnapshot.getValue(FirebaseUserEntity.class);
            allUserData.add(new UserProfile(Helper.NAME, userInformation.getName()));
            allUserData.add(new UserProfile(Helper.EMAIL, userInformation.getEmail()));
            allUserData.add(new UserProfile(Helper.BIRTHDAY, userInformation.getBirthday()));
            allUserData.add(new UserProfile(Helper.PHONE_NUMBER, userInformation.getPhone()));
            allUserData.add(new UserProfile(Helper.HOBBY_INTEREST, userInformation.getHobby()));
        }
        return allUserData;
    }
}
