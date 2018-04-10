package com.example.mike4christ.medmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class DashBoard extends AppCompatActivity implements View.OnClickListener {

    private TextView user;
    private Button dashboard_btn_proceed;


    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        //View
        user = (TextView)findViewById(R.id.user);
        dashboard_btn_proceed = (Button)findViewById(R.id.dashboard_btn_proceed);



        dashboard_btn_proceed.setOnClickListener(this);

        //Init Firebase
        auth = FirebaseAuth.getInstance();

        //Session check
        if(auth.getCurrentUser() != null)
            user.setText(auth.getCurrentUser().getEmail());


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.dashboard_btn_proceed){
           Intent intent=new Intent(DashBoard.this,MainActivity.class);
           startActivity(intent);
        }
    }


}
