/*
 * Copyright (C) 2016 The Android Open Source Project
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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hendraanggrian.bundler.BindExtra;
import com.hendraanggrian.bundler.Bundler;
import com.hendraanggrian.kota.content.Themes;
import com.hendraanggrian.reveallayout.Radius;
import com.hendraanggrian.reveallayout.RevealableLayout;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Allows user to create a new employee or edit an existing one.
 */
public class EmployeeEditor extends AppCompatActivity {

    /**
     * Identifier for the employee data loader
     */
    private static final int EXISTING_EMPLOYEE_LOADER = 0;

    /**
     * Content URI for the existing employee (null if it's a new employee)
     */
    private Uri mCurrentEmployeeUri;


    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mCity;
    private EditText mPhone;
    private EditText mEmail;

    /**
     * EditText field to enter the employee's gender
     */
    private Spinner mGenderSpinner;

    //TODO
    private ImageView profileImageView;
    private Button pickImage;

    String mGender;


    private static final int REQUEST_CAMERA = 3;
    private static final int SELECT_FILE = 2;
    private ProgressDialog mProgress;

    private Handler progressBarbHandler = new Handler();
    private boolean hasImageChanged = false;
    Bitmap thumbnail;

    private  Button saveProfile;




    /**
     * Boolean flag that keeps track of whether the employee has been edited (true) or not (false)
     */


    View rootLayout;

    public static final String EXTRA_RECT = "com.example.mike4christ.medmanager";
    @BindExtra(EXTRA_RECT)
    Rect rect;

    @BindView(R.id.revealLayout)
    RevealableLayout revealLayout;

    @BindView(R.id.layout)
    ViewGroup layout;
    //FIREBASE AUTHENTICATION FIELDS
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    //FIREBASE DATABASE FIELDS
    DatabaseReference mUserDatabse;
    StorageReference mStorageRef;

    //IMAGE HOLD URI
    Uri imageHoldUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Bundler.bindExtras(this);
        ButterKnife.bind(this);


        // Find all relevant views that we will need to read user input from

        mFirstNameEditText = (EditText) findViewById(R.id.edit_employee_firstname);
        mLastNameEditText = (EditText) findViewById(R.id.edit_employee_lastname);
        mCity = (EditText) findViewById(R.id.edit_employee_city);
        mPhone = (EditText) findViewById(R.id.edit_employee_phone);
        mEmail = (EditText) findViewById(R.id.edit_employee_email);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        saveProfile=(Button)findViewById(R.id.saveProfile);


        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = "Male";
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = "Female";
                    } else {
                        mGender = "Unknown";
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = "Unknown";
            }
        });

        rootLayout = findViewById(R.id.root_layout);

        layout.post(new Runnable() {
            @Override
            public void run() {
                Animator animator = revealLayout.reveal(layout, rect.centerX(), rect.centerY(), Radius.GONE_ACTIVITY);
                animator.setDuration(1000);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (Build.VERSION.SDK_INT >= 21) {
                            getWindow().setStatusBarColor(Themes.getColor(getTheme(), R.attr.colorAccent, true));
                        }
                    }
                });
                animator.start();
            }
        });

        //TODO
        profileImageView = (ImageView) findViewById(R.id.profileImageView);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                //LOGIC CHECK USER
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {

                    finish();
                    Intent moveToHome = new Intent(EmployeeEditor.this, EmployeeDetails.class);
                    moveToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(moveToHome);

                }

            }
        };

        //PROGRESS DIALOG
        mProgress = new ProgressDialog(this);

        //FIREBASE DATABASE INSTANCE
        mUserDatabse = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //ONCLICK SAVE PROFILE BTN
        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //LOGIC FOR SAVING USER PROFILE
                saveUserProfile();

            }
        });





        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profilePicSelection();
            }
        });


    }


    /**
     * Get user input from editor and save employee into database.
     */
    private void saveUserProfile() {

        final String firstName, lastName,city,Phone_num,email,gender;

        firstName=mFirstNameEditText.getText().toString().trim();
        lastName=mFirstNameEditText.getText().toString().trim();
        city=mCity.getText().toString().trim();
        Phone_num=mPhone.getText().toString().trim();
        email=mEmail.getText().toString().trim();
        gender=mGender;




        if( !TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)&&!TextUtils.isEmpty(city)&&!TextUtils.isEmpty(Phone_num)&&!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(gender))
        {

            if( imageHoldUri != null )
            {

                mProgress.setTitle("Saving Profile");
                mProgress.setMessage("Please wait....");
                mProgress.show();

                StorageReference mChildStorage = mStorageRef.child("User_Profile").child(imageHoldUri.getLastPathSegment());
                String profilePicUrl = imageHoldUri.getLastPathSegment();

                mChildStorage.putFile(imageHoldUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        final Uri imageUrl = taskSnapshot.getDownloadUrl();

                        mUserDatabse.child("firstname").setValue(firstName);
                        mUserDatabse.child("lastname").setValue(lastName);
                        mUserDatabse.child("city").setValue(city);
                        mUserDatabse.child("phone_num").setValue(Phone_num);
                        mUserDatabse.child("email").setValue(email);
                        mUserDatabse.child("gender").setValue(gender);
                        mUserDatabse.child("userid").setValue(mAuth.getCurrentUser().getUid());
                        mUserDatabse.child("imageurl").setValue(imageUrl.toString());

                        mProgress.dismiss();

                        Toast.makeText(EmployeeEditor.this, "Profile Successfull", Toast.LENGTH_LONG).show();


                    }
                });
            }else
            {

                Toast.makeText(EmployeeEditor.this, "Please select the profile pic", Toast.LENGTH_LONG).show();

            }

        }else
        {

            Toast.makeText(EmployeeEditor.this, "Please enter username and status", Toast.LENGTH_LONG).show();

        }

    }
    public void profilePicSelection() {

        //DISPLAY DIALOG TO CHOOSE CAMERA OR GALLERY

        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeEditor.this);
        builder.setTitle("Add Photo!");

        //SET ITEMS AND THERE LISTENERS
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    private void cameraIntent() {

        //CHOOSE CAMERA
        Log.d("gola", "entered here");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {

        //CHOOSE IMAGE FROM GALLERY
        Log.d("gola", "entered here");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_FILE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //SAVE URI FROM GALLERY
        if(requestCode == SELECT_FILE && resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }else if ( requestCode == REQUEST_CAMERA && resultCode == RESULT_OK ){
            //SAVE URI FROM CAMERA

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }


        //image crop library code
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageHoldUri = result.getUri();

                profileImageView.setImageURI(imageHoldUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save employee to database
                saveUserProfile();
                // Exit activity
                finish();
                return true;



        }
        return super.onOptionsItemSelected(item);
    }

        /**
         * This method is called when the back button is pressed.
         */
        @Override
        public void onBackPressed (){

            Animator animator = revealLayout.reveal(layout, rect.centerX(), rect.centerY(), Radius.ACTIVITY_GONE);
            animator.setDuration(1000);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        getWindow().setStatusBarColor(Themes.getColor(getTheme(), R.attr.colorPrimaryDark, true));
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    layout.setVisibility(View.INVISIBLE);
                    finish();
                    overridePendingTransition(0, 0);
                }
            });
            animator.start();
        }

}