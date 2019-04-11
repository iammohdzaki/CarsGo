package com.example.carsgo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.carsgo.R;
import com.example.carsgo.util.CustomTextWatcher;
import com.example.carsgo.util.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etEmail,etPassword;
    private Button btnSignIn;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        init();
    }


    private void init(){
        mAuth=FirebaseAuth.getInstance();
        etEmail=findViewById(R.id.et_user_email);
        etPassword=findViewById(R.id.et_user_password);

        etEmail.addTextChangedListener(new CustomTextWatcher(etEmail));
        etPassword.addTextChangedListener(new CustomTextWatcher(etPassword));

        btnSignIn=findViewById(R.id.btn_sign_in);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateUser();
            }
        });
    }

    private void authenticateUser(){
        final String email=etEmail.getText().toString();
        final String password=etPassword.getText().toString();

        if(!Validator.isValidEmail(email)){
            etEmail.setError("Email is not Valid");
            requestFocus(etEmail);
            return;
        }

        if(!Validator.isValidPassword(password)){
            etPassword.setError("Password is not Valid");
            requestFocus(etPassword);
            return;
        }

        dialog = ProgressDialog.show(this,"Signing You In","Fetching Details from Server", true);
        signInUser(email,password);
    }

    private void signInUser(String email,String password){
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignInActivity.this,"Successfully Signed In!"+task.getResult(),Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            startActivity(new Intent(SignInActivity.this,HomeActivity.class));
                            finish();
                        }else{
                            Toast.makeText(SignInActivity.this,"Wrong Email or Password!"+task.getResult(),Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


    public void goBack(View view){
        finish();
    }

    public void openForgotPasswordScreen(View view){
        startActivity(new Intent(SignInActivity.this,ForgotPasswordActivity.class));
    }

    public void openSignUpScreen(View view){
        startActivity(new Intent(SignInActivity.this,SignUpActivity.class));
    }

    /**
     * Focus on the view where error occur
     * @param view as Current View Reference
     */
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
