package com.example.studious;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Login extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String usernameKey = "username";
        String passwordKey = "password";
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.studious", Context.MODE_PRIVATE);

        if(!sharedPreferences.getString(usernameKey, "").equals("")
                && !sharedPreferences.getString(passwordKey, "").equals("")) {

            Intent intent = new Intent(this, homeScreen.class);
            startActivity(intent);

        } else {
            setContentView(R.layout.login);
        }
    }

    public void signupClick(View view) {
        EditText editText =findViewById(R.id.usernameText);

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.studious", Context.MODE_PRIVATE);

        //checks if this email is already contained
        if(!sharedPreferences.contains(editText.getText().toString())) {
            Intent intent = new Intent(this, Signup.class);
            startActivity(intent);
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);

            builder.setMessage("This email already has an account!");
            builder.setTitle("Alert!");

            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // When the user click yes button
                    // then app will close
                    finish();
                }
            });
        }
    }

    public void loginClick(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.studious", Context.MODE_PRIVATE);
        EditText emailText = findViewById(R.id.usernameText);
        EditText passwordText = findViewById(R.id.passwordText);

        sharedPreferences.edit().putString("email", emailText.getText().toString());
        sharedPreferences.edit().putString("password", passwordText.getText().toString());

        Intent intent = new Intent(this, AddClasses.class);
        startActivity(intent);
    }
}
