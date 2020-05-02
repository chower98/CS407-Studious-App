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
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Signup extends AppCompatActivity {
    private EditText emailInput, passwordInput, confirmPasswordInput, nameInput, phoneInput;
    private String name, email, password, confirmPassword, phone;
    private String netID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

    public void signUpClick(View view) {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);

        name = nameInput.getText().toString();
        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();
        confirmPassword = confirmPasswordInput.getText().toString();
        phone = phoneInput.getText().toString();

        if (name.equals("") || email.equals("") || password.equals("") || phone.equals("")) {
            createIncorrectInfoAlert(); // one of the fields are not specified
            return; // end signup method early
        }

        String[] emailName = email.split("@");
        if (!emailName[1].equals("wisc.edu")) { // check that it's a wisc.edu email
            createIncorrectInfoAlert();
            return; // end signup method early
        }

        if (!password.equals(confirmPassword)) {
            createIncorrectPasswordAlert();
            return; // end signup method early
        }

        netID = email.substring(0, email.length() - 9);
        checkEmail(); // call helper method to check email and proceed with appropriate action
    }

    private void checkEmail() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference signupUser = firebaseDatabase.getReference().child("UserInfo").child(netID);

        signupUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user == null) {
                    storeNewUser(); // no user exists
                } else {
                    createDuplicateUserAlert();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO: dunno
            }
        });
    }

    private void storeNewUser() {
        User newUser = new User(name, email, password, phone); // create new User object

        // store newUser in firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference newUserRef = firebaseDatabase.getReference().child("UserInfo").child(netID);
        newUserRef.setValue(newUser);

        firebaseDatabase.getReference().child("UserMatches").child(netID).setValue("Matches:");
        // shared preferences to keep user logged in app if they do not manually log out
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.studious", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("email", email).apply();
        sharedPreferences.edit().putString("password", password).apply();

        // continue to next activity in signup process
        Intent continueIntent = new Intent(this, AddClasses.class);
        continueIntent.putExtra("newUser", true);
        startActivity(continueIntent);
    }

    private void createDuplicateUserAlert() {
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

    private void createIncorrectInfoAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);

        builder.setMessage("Incorrect information detected. Check to make sure all fields are " +
                "filled out and that email is a 'wisc.edu' address.");
        builder.setTitle("Alert!");

        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // When the user click yes button, the dialog will close
                dialog.dismiss();

                passwordInput.setText(""); // reset password field to ""
                confirmPasswordInput.setText("");
            }
        });

        AlertDialog incorrectInfoAlert = builder.create();
        incorrectInfoAlert.show();
    }

    private void createIncorrectPasswordAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);

        builder.setMessage("Passwords must match!");
        builder.setTitle("Alert!");

        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // When the user click yes button, the dialog will close
                dialog.dismiss();

                passwordInput.setText(""); // reset password field to ""
                confirmPasswordInput.setText("");
            }
        });

        AlertDialog incorrectPassAlert = builder.create();
        incorrectPassAlert.show();
    }


}
