package com.harsh.mywhatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.harsh.mywhatsapp.Models.Users;
import com.harsh.mywhatsapp.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {
    ActivitySignInBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    BeginSignInRequest signInRequest; // created by me only randmoly
    GoogleSignInClient mGoogleSignInClient;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide(); /// for hidind action bar

        /////////////////////////////////////////////////////////////////
        progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Login to your account");
        //////////////////////////////////////////////////////////////////
        auth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();

        //////////////////////////////// GOOGLE SIGN IN////////////////////////////////////////////////////////////
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))//Default_web_client_id will be matched with the
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);


        ///////////////////////////////////////////////////////////////////////////////////////////

        binding.btnSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                auth.signInWithEmailAndPassword
                        (binding.etEmailSignIn.getText().toString(),binding.etPasswordSignIn.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
 ///////////////////////////////////////////////////////////////////////////////////////////////////
        binding.tvClickForSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
////////////////////////////////////////////////////////////////////////////////////////////////////
        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(); // AFTER CLICKING GOOGLE BUTTON IT WILL CALL THIS METHOD
            }
        });
 ///////////////////////////////////////////////////////////////////////////////////////////////////
        if(auth.getCurrentUser() != null){
            Intent intent = new Intent(SignInActivity.this , MainActivity.class);
            startActivity(intent);
        }
 ///////////////////////////////////////////////////////////////////////////////////////////////////
    }
////////////////////////////GOOGLE SIGNIN ////////////////////////////////////////////////////////////////////////
    int RC_SIGN_IN = 65; // random any code number under 100
    private void signIn(){
        @Deprecated
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
// Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(this, "Google Sign in Succeeded",  Toast.LENGTH_LONG).show();
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
// Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                Toast.makeText(this, "Google Sign in Failed " ,  Toast.LENGTH_LONG).show();
            }
        }
    }
//////////////////////////////////GOOGLE SIGNIN//////////////////////////////////////////////////////////////////
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());
        //Calling get credential from the oogleAuthProviderG
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    //Override th onComplete() to see we are successful or not.
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
// Update UI with the sign-in user's information
                            FirebaseUser user = auth.getCurrentUser();
                            // Store user info in database //
                            Users users = new Users(); // user we have created
                            users.setUserId(user.getUid());
                            users.setUserName(user.getDisplayName());
                            users.setProfilepic(user.getPhotoUrl().toString());
                            database.getReference().child("Users").child(user.getUid()).setValue(users); // creat user database inside out firebase database
                            ///////////////////////////////////////////////////////////
                            Intent intent = new Intent(SignInActivity.this,MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(SignInActivity.this, "Sign In With Google ",  Toast.LENGTH_LONG).show();
                        } else {
// If sign-in fails to display a message to the user.
                            Toast.makeText(SignInActivity.this, "Sign in Failed", Toast.LENGTH_SHORT).show();
//                            Toast.makeText(SignInActivity.this, "Firebase Authentication failed:" + task.getException(),  Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
}