package com.harsh.mywhatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.harsh.mywhatsapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityMainBinding.inflate(getLayoutInflater());
        auth = FirebaseAuth.getInstance();
        setContentView(binding.getRoot());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // Menu bar is created
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // After any option is selected from menu bar
        switch (item.getItemId()){ // we take options from id , settigs , logout we have created inside menu
            case R.id.settings:
                Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show();
                break;

            case R.id.logout:
                auth.signOut(); // when we clicked logout it will log out
                // After clicking logout tell go back to sign in activity so use Intent
                Intent intent = new Intent(MainActivity.this , SignInActivity.class);
                startActivity(intent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}