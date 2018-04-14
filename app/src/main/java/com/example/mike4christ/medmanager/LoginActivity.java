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
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.example.mike4christ.medmanager.Helper.Helper;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText emailInput;

    private EditText passwordInput;

    private TextView loginError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        FirebaseAuth mAuth = ((FirebaseApplication) getApplication()).getFirebaseAuth();
        ((FirebaseApplication)getApplication()).checkUserLogin(LoginActivity.this);

        loginError = findViewById(R.id.login_error);
        TextView forget_password = findViewById(R.id.forget_password);

        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);


        Button signUpText = findViewById(R.id.register);

        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });

        final Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredEmail = emailInput.getText().toString();
                String enteredPassword = passwordInput.getText().toString();

                if(TextUtils.isEmpty(enteredEmail) || TextUtils.isEmpty(enteredPassword)){
                    Helper.displayMessageToast(LoginActivity.this, "Login fields must be filled");
                    return;
                }
                if(Helper.isValidEmail(enteredEmail)){
                    Helper.displayMessageToast(LoginActivity.this, "Invalidate email entered");
                    return;
                }

                ((FirebaseApplication)getApplication()).loginAUser(LoginActivity.this, enteredEmail, enteredPassword, loginError);
            }
        });

        if(mAuth.getCurrentUser() != null)
            startActivity(new Intent(LoginActivity.this,ProfileActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(((FirebaseApplication)getApplication()).mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (((FirebaseApplication)getApplication()).mAuthListener != null) {
            //mAuth.removeAuthStateListener(((FirebaseApplication)getApplication()).mAuthListener);
        }
    }
}
