package com.example.studious;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {
    String name, email, password, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

    public void signUpClick(View view) {
        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        EditText nameInput = findViewById(R.id.nameInput);
        EditText phoneInput = findViewById(R.id.phoneInput);

        name = nameInput.getText().toString();
        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();
        phone = phoneInput.getText().toString();

        // shared preferences to keep user logged in app if they do not manually log out
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.studious", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("email", email).apply();
        sharedPreferences.edit().putString("password", password).apply();

        // instantiate firebaseHelper to add new user
        FirebaseHelper firebaseHelper = new FirebaseHelper();

        int userExists = firebaseHelper.checkUserLogin(email, password);
        Log.e("user exists?", Integer.toString(userExists)); // TODO: debug code

        if (userExists == 0) { // 0 = user doesn't exist
            try {
                User newUser = new User(name, email, password, phone); // create new User object
                firebaseHelper.addUserInfo(newUser); // add new user to database

                Intent continueIntent = new Intent(this, AddClasses.class);
                continueIntent.putExtra("newUser", true);
                startActivity(continueIntent);
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else { // 1 or 2 = user exists
            AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);

            builder.setMessage("This email already has an account!");
            builder.setTitle("Alert!");

            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // When the user click yes button, the dialog will close
                    dialog.dismiss();

                    // redirect to login screen
                    Intent loginIntent = new Intent(Signup.this, Login.class);
                    startActivity(loginIntent);
                }
            });
            AlertDialog duplicateEmailAlert = builder.create();
            duplicateEmailAlert.show();
        }


    }
}
