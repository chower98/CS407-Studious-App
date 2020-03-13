package com.example.studious;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    public void signupClick(View view) {
        Intent intent = new Intent(this, Signup.class);
        startActivity(intent);
    }

    public void loginClick(View view) {
        Intent intent = new Intent(this, AddClasses.class);
        startActivity(intent);
    }
}
