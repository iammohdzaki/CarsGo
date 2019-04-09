package com.example.carsgo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.carsgo.R;
import com.example.carsgo.model.User;
import com.example.carsgo.util.CustomTextWatcher;
import com.example.carsgo.util.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etEmail,etPassword,etFirstName,etLastName,etMobileNumber,etHomeAddress,etConfirmPassword;
    private Button btnSignUp;
    private CheckBox cbTerms;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();

    }

    private void init(){
        mAuth=FirebaseAuth.getInstance();

        etFirstName=findViewById(R.id.et_user_first_name);
        etLastName=findViewById(R.id.et_user_last_name);
        etEmail=findViewById(R.id.et_user_email);
        etMobileNumber=findViewById(R.id.et_user_contact);
        etHomeAddress=findViewById(R.id.et_user_address);
        etPassword=findViewById(R.id.et_user_password);
        etConfirmPassword=findViewById(R.id.et_user_confirm_password);

        etFirstName.addTextChangedListener(new CustomTextWatcher(etFirstName));
        etLastName.addTextChangedListener(new CustomTextWatcher(etLastName));
        etEmail.addTextChangedListener(new CustomTextWatcher(etEmail));
        etMobileNumber.addTextChangedListener(new CustomTextWatcher(etMobileNumber));
        etHomeAddress.addTextChangedListener(new CustomTextWatcher(etHomeAddress));
        etPassword.addTextChangedListener(new CustomTextWatcher(etPassword));
        etConfirmPassword.addTextChangedListener(new CustomTextWatcher(etConfirmPassword));

        cbTerms=findViewById(R.id.cb_terms);
        cbTerms.addTextChangedListener(new CustomTextWatcher(cbTerms));
        btnSignUp=findViewById(R.id.btn_sign_up);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }

    private void createUser(){

        final String firstName=etFirstName.getText().toString().trim();
        final String lastName=etLastName.getText().toString().trim();
        final String email=etEmail.getText().toString().trim();
        final String contact=etMobileNumber.getText().toString().trim();
        final String homeAddress=etHomeAddress.getText().toString();
        String confirmPassword=etConfirmPassword.getText().toString().trim();
        final String password=etPassword.getText().toString().trim();

        //Validate First Name
        if(!Validator.isValidFirstName(firstName)){
            etFirstName.setError("First Name should be Valid!");
            requestFocus(etFirstName);
            return;
        }

        //Validate Last Name
        if(!Validator.isValidLastName(lastName)){
            etLastName.setError("Last Name should be Valid!");
            requestFocus(etLastName);
            return;
        }

        //Validate Email
        if(!Validator.isValidEmail(email)){
            etEmail.setError("Email is not Valid");
            requestFocus(etEmail);
            return;
        }

        //Valid Mobile Number
        if(!Validator.isValidMobile(contact)){
            etMobileNumber.setError("Number should not be empty or more then 10 digits!");
            requestFocus(etMobileNumber);
            return;
        }

        //Validate Address
        if(!Validator.isValidAddress(homeAddress)){
            etHomeAddress.setError("Provide Proper Address!");
            requestFocus(etHomeAddress);
            return;
        }

        //Validate Confirm Password
        if(!Validator.isValidConfirmPassword(password,confirmPassword)){
            etConfirmPassword.setError("Password doesn't matches!");
            requestFocus(etConfirmPassword);
            return;
        }

        //Validate Password
        if(!Validator.isValidPassword(password)){
            etPassword.setError("Password is not Valid");
            requestFocus(etPassword);
            return;
        }

        //Validate Terms and Condition
        if(!cbTerms.isChecked()){
           cbTerms.setError("You must agree to Terms and Conditions!");
           requestFocus(cbTerms);
           return;
        }

        dialog = ProgressDialog.show(this,"Signing You Up","Saving Details to Server", true);

        //Add User To Fire Base
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    User user=new User(
                            firstName,
                            lastName,
                            email,
                            contact,
                            homeAddress
                    );

                    FirebaseDatabase.getInstance().getReference("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this,"User Created Successfully!",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                startActivity(new Intent(SignUpActivity.this,HomeActivity.class));
                                finish();
                            }
                        }
                    });
                }else {
                    Toast.makeText(SignUpActivity.this,"Error While Signing Up!"+task.getResult(),Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
    }

    public void goBack(View view){
        finish();
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
