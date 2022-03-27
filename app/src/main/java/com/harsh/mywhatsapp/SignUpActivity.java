package com.harsh.mywhatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.harsh.mywhatsapp.Models.Users;
import com.harsh.mywhatsapp.databinding.ActivitySignInBinding;
import com.harsh.mywhatsapp.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding; /// --- ActivitySignUp is the name of current activity
    private FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog progressDialog; // used for buffering effect
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


////////////////////////////////////////////////////////////////////////////////////////////////////
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We are creating your account");
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
        binding.btnSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show(); ////----- show dialog
                auth.createUserWithEmailAndPassword
                        (binding.etEmail.getText().toString(),binding.etPassword.getText().toString()).
                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Users user = new Users
                                    (binding.etUserName.getText().toString(),binding.etEmail.getText().toString(),
                                    binding.etPassword.getText().toString());

                            String id = task.getResult().getUser().getUid(); // we need id Step 16
                            database.getReference().child("Users").child(id).setValue(user); // user is made above which is signup now

                            Toast.makeText(SignUpActivity.this, "User created Sucessfully", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
////////////////////////////////////////////////////////////////////////////////////////////////////
        binding.tvAlreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
////////////////////////////////////////////////////////////////////////////////////////////////////
    }

}