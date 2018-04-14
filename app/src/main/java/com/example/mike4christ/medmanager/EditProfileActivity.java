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
package com.example.mike4christ.medmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.mike4christ.medmanager.Firebase.FirebaseDatabaseHelper;
import com.example.mike4christ.medmanager.Firebase.FirebaseUserEntity;
import com.example.mike4christ.medmanager.Helper.Helper;

/**
 * Created by Alu Michael on 9/4/18.
 */
//This is Edit Activity for edit an existing profile


public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = EditProfileActivity.class.getSimpleName();

    private EditText editProfileName;

    private EditText editProfileCountry;

    private EditText editProfilePhoneNumber;

    private EditText editProfileHobby;

    private EditText editProfileBirthday;

    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        setTitle("Edit Profile Information");

        editProfileName = findViewById(R.id.profile_name);
        editProfileCountry = findViewById(R.id.profile_country);
        editProfilePhoneNumber = findViewById(R.id.profile_phone);
        editProfileHobby = findViewById(R.id.profile_hobby);
        editProfileBirthday = findViewById(R.id.profile_birth);

        Button saveEditButton = findViewById(R.id.save_edit_button);
        saveEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String profileName = editProfileName.getText().toString();
                String profileCountry = editProfileCountry.getText().toString();
                String profilePhoneNumber = editProfilePhoneNumber.getText().toString();
                String profileHobby = editProfileHobby.getText().toString();
                String profileBirthday = editProfileBirthday.getText().toString();

                // update the user profile information in Firebase database.
                if(TextUtils.isEmpty(profileName) || TextUtils.isEmpty(profileCountry) || TextUtils.isEmpty(profilePhoneNumber)
                        || TextUtils.isEmpty(profileHobby) || TextUtils.isEmpty(profileBirthday)){
                    Helper.displayMessageToast(EditProfileActivity.this, "All fields must be filled");
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Intent firebaseUserIntent = new Intent(EditProfileActivity.this, LoginActivity.class);
                    startActivity(firebaseUserIntent);
                    finish();
                } else {
                    String userId = user.getProviderId();
                    String id = user.getUid();
                    String profileEmail = user.getEmail();

                    FirebaseUserEntity userEntity = new FirebaseUserEntity(id, profileEmail, profileName, profileCountry, profilePhoneNumber, profileBirthday, profileHobby);
                    FirebaseDatabaseHelper firebaseDatabaseHelper = new FirebaseDatabaseHelper();
                    firebaseDatabaseHelper.createUserInFirebaseDatabase(id, userEntity);

                    Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();

                    editProfileName.setText("");
                    editProfileCountry.setText("");
                    editProfilePhoneNumber.setText("");
                    editProfileHobby.setText("");
                    editProfileBirthday.setText("");
                }
            }
        });
    }
}
