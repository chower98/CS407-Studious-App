package com.example.studious;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    private String email, netID, password;
    private EditText emailInput, passwordInput;

    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";
    private final static String PACKAGE_NAME = "com.example.studious";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);

        // checks if a user hasn't logged out
        if(!sharedPreferences.getString(EMAIL_KEY, "").equals("")
                && !sharedPreferences.getString(PASSWORD_KEY, "").equals("")) {

            // go automatically to home screen if user is still logged in
            Intent intent = new Intent(this, HomeScreen.class);
            startActivity(intent);
        } else { // go to login screen if no user logged in
            //make new thread, run matchmaker algorithm
            String email = sharedPreferences.getString(EMAIL_KEY,"");
            String netID = email.substring(0, email.length() - 9);
            MatchRunnable matchMaker = new MatchRunnable(netID);
            new Thread(matchMaker).start();

            setContentView(R.layout.activity_login);
            emailInput = findViewById(R.id.emailInput);
            passwordInput = findViewById(R.id.passwordInput);
        }
    }

    public void signupClick(View view) {
        EditText editText = findViewById(R.id.emailInput);

        SharedPreferences sharedPreferences = getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);

        //checks if this email is already contained
        if(!sharedPreferences.contains(editText.getText().toString())) {
            Intent signupIntent = new Intent(this, Signup.class);
            // will not keep Signup activity in the stack; user cannot back to the signup screen
            signupIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(signupIntent);
        } else {
            createDuplicateEmailAlert(); // call private method to create duplicate email alert
        }
    }

    public void loginClick(View view) {
        email = emailInput.getText().toString();
        netID = email.substring(0, email.length() - 9); // get netID from email
        password = passwordInput.getText().toString();

        checkLogin(); // call helper method to check if login is valid
    }

    private void createDuplicateEmailAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);

        builder.setMessage("This email already has an account!");
        builder.setTitle("Alert!");

        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // When the user click yes button, then dialog will close
                dialog.dismiss();
            }
        });
        AlertDialog duplicateEmailAlert = builder.create();
        duplicateEmailAlert.show();
    }

    private void checkLogin() {
        //TODO: debug final boolean loginCheck = false;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference currentUser = firebaseDatabase.getReference().child("UserInfo").child(netID);

        currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user == null) {
                    createIncorrectLoginAlert(); // no user exists
                } else {
                    if (user.getPassword().equals(password)) {
                        loginUser(); // correct login info
                    } else {
                        createIncorrectLoginAlert(); // incorrect password
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO: dunno
            }
        });
    }

    private void loginUser() {
        // add email and password to sharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(EMAIL_KEY, email).apply();
            sharedPreferences.edit().putString(PASSWORD_KEY, password).apply();

        String email = sharedPreferences.getString(EMAIL_KEY,"");
        String netID = email.substring(0, email.length() - 9);
        MatchRunnable matchMaker = new MatchRunnable(netID);
        new Thread(matchMaker).start();

        Intent loginIntent = new Intent(this, HomeScreen.class);
        startActivity(loginIntent);
    }

    private void createIncorrectLoginAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);

        builder.setMessage("Incorrect email or password!");
        builder.setTitle("Alert!");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // When the user click yes button, then dialog will close password field will be reset
                passwordInput.setText("");
                dialog.dismiss();
            }
        });

        AlertDialog incorrectLogin = builder.create();
        incorrectLogin.show();
    }

}
