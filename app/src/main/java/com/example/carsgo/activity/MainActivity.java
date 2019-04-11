package com.example.carsgo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.carsgo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    private Button btnSignUp,btnSignIn;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkInternetConnection();
        checkUserStatus();
    }

    private void init(){
        btnSignIn=findViewById(R.id.btn_sign_in_splash);
        btnSignUp=findViewById(R.id.btn_sign_up_splash);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SignInActivity.class));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SignUpActivity.class));
            }
        });
    }

    private void initSplash(){
        btnSignIn=findViewById(R.id.btn_sign_in_splash);
        btnSignUp=findViewById(R.id.btn_sign_up_splash);

        btnSignIn.setVisibility(View.INVISIBLE);
        btnSignUp.setVisibility(View.INVISIBLE);

        dialog=dialog.show(MainActivity.this,"Connecting to Server","",true);
        //check user session
    }

    private void checkUserStatus(){
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            initSplash();
            startActivity(new Intent(MainActivity.this,HomeActivity.class));
            dialog.dismiss();
            finish();
        }else{
            init();
        }
    }

    protected boolean isOnline(){
        ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnectedOrConnecting()){
            return true;
        }else{
            return false;
        }
    }

    private void checkInternetConnection(){
        if(isOnline()){

        }else{
            AlertDialog.Builder mDialog=new AlertDialog.Builder(MainActivity.this)
                    .setTitle("No Internet Available ?")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkInternetConnection();
                        }
                    })
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            mDialog.show();
        }
    }
}
