package com.example.mike4christ.medmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin;
    EditText input_email,input_password;
    TextView btnSignup,btnForgotPass;

    RelativeLayout activity_main;

    private FirebaseAuth auth;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //View
        btnLogin = (Button)findViewById(R.id.login_btn_login);
        input_email = (EditText)findViewById(R.id.login_email);
        input_password = (EditText)findViewById(R.id.login_password);
        btnSignup = (TextView)findViewById(R.id.login_btn_signup);
        btnForgotPass = (TextView)findViewById(R.id.login_btn_forgot_password);
        activity_main = (RelativeLayout)findViewById(R.id.activity_main);

        btnSignup.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        //Init Firebase Auth
        auth = FirebaseAuth.getInstance();
        /////
         mProgressDialog = new ProgressDialog(Login.this);
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setIndeterminate(true);

        //Check already session , if ok-> DashBoard
        if(auth.getCurrentUser() != null)
            startActivity(new Intent(Login.this,DashBoard.class));
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.login_btn_forgot_password)
        {
            startActivity(new Intent(Login.this,ForgotPassword.class));
            finish();
        }
        else if(view.getId() == R.id.login_btn_signup)
        {
            startActivity(new Intent(Login.this,SignUp.class));
            finish();
        }
        else if(view.getId() == R.id.login_btn_login)
        {

            loginUser(input_email.getText().toString(),input_password.getText().toString());
            mProgressDialog.show();
        }
    }

    private void loginUser(String email, final String password) {
        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            mProgressDialog.dismiss();
                            if(password.length() < 6)
                            {

                                Snackbar snackBar = Snackbar.make(activity_main,"Password length must be over 6",Snackbar.LENGTH_SHORT);
                                snackBar.show();
                            }
                            Snackbar snackBar = Snackbar.make(activity_main,"Check your Internet or try to input Valid data",Snackbar.LENGTH_SHORT);
                            snackBar.show();
                        }
                        else{
                            mProgressDialog.dismiss();
                            startActivity(new Intent(Login.this,DashBoard.class));
                        }
                    }
                });
    }
}
