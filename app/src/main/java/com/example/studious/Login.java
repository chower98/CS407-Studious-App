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

public class Login extends AppCompatActivity {
    String email, password;
    EditText emailInput, passwordInput;
    DBHelper dbHelper;

    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";
    private final static String PACKAGE_NAME = "com.example.studious";
    private final static String DATABASE_NAME = "data";

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
            setContentView(R.layout.activity_login);
            emailInput = findViewById(R.id.emailInput);
            passwordInput = findViewById(R.id.passwordInput);
        }
    }

    public void signupClick(View view) {
        EditText editText =findViewById(R.id.emailInput);

        SharedPreferences sharedPreferences = getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);

        //checks if this email is already contained
        if(!sharedPreferences.contains(editText.getText().toString())) {
            Intent signupIntent = new Intent(this, Signup.class);
            signupIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(signupIntent);
        } else {
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
    }

    public void loginClick(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();

        // add email and password to sharedPreferences
        sharedPreferences.edit().putString(EMAIL_KEY, email).apply();
        sharedPreferences.edit().putString(PASSWORD_KEY, password).apply();

        // instantiate dbHelper to check if correct login info was inputted
        Context context = getApplicationContext();
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase);
        int loginCheck = dbHelper.checkUserLogin(email, password);

        if (loginCheck == 1) { // correct login, go to home screen
            Intent loginIntent = new Intent(this, HomeScreen.class);
            startActivity(loginIntent);
        } else { // incorrect login, display alert
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);

            builder.setMessage("Incorrect email or password!");
            builder.setTitle("Alert!");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // When the user click yes button, then dialog will close
                    // password field will be reset
                    passwordInput.setText("");
                    dialog.dismiss();
                }
            });

            AlertDialog incorrectLogin = builder.create();
            incorrectLogin.show();
        }
    }
}
