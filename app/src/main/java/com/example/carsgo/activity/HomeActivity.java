package com.example.carsgo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.carsgo.R;
import com.example.carsgo.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private EditText etFirstName,etSecondName,etEmail,etMobile,etPassword;
    private Button btnSignOut;
    private DatabaseReference dbRef;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        init();
    }

    private void init(){
        etFirstName=findViewById(R.id.et_user_first_name_home);
        etSecondName=findViewById(R.id.et_user_last_name_home);
        etEmail=findViewById(R.id.et_user_email_home);
        etMobile=findViewById(R.id.et_user_contact_home);
        etPassword=findViewById(R.id.et_user_password_home);
        btnSignOut=findViewById(R.id.btn_sign_out_home);

        dialog=dialog.show(HomeActivity.this,"Just a Sec","Retrieving User Data",true);
        fillUserData();

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this,MainActivity.class));
                Toast.makeText(HomeActivity.this,"Signing you Out!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillUserData(){
        String UID=FirebaseAuth.getInstance().getCurrentUser().getUid();
          dbRef=FirebaseDatabase.getInstance().getReference().child("users").child(UID);
          dbRef.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  etFirstName.setText(dataSnapshot.child("firstName").getValue().toString());
                  etFirstName.setEnabled(false);
                  etSecondName.setText(dataSnapshot.child("lastName").getValue().toString());
                  etSecondName.setEnabled(false);
                  etEmail.setText(dataSnapshot.child("email").getValue().toString());
                  etEmail.setEnabled(false);
                  etMobile.setText(dataSnapshot.child("mobileNumber").getValue().toString());
                  etMobile.setEnabled(false);
                  etPassword.setText("000000000");
                  etPassword.setEnabled(false);

                  dialog.dismiss();
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
          });
    }

}

