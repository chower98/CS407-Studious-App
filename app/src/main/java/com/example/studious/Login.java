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

public class Login extends AppCompatActivity {
    String email, password;
    EditText emailInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String usernameKey = "username";
        String passwordKey = "password";
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.studious", Context.MODE_PRIVATE);

        if(!sharedPreferences.getString(usernameKey, "").equals("")
                && !sharedPreferences.getString(passwordKey, "").equals("")) {

            Intent intent = new Intent(this, HomeScreen.class);
            startActivity(intent);

        } else {
            setContentView(R.layout.activity_login);
            emailInput = findViewById(R.id.emailInput);
            passwordInput = findViewById(R.id.passwordInput);
        }
    }

    public void signupClick(View view) {
        EditText editText =findViewById(R.id.emailInput);

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.studious", Context.MODE_PRIVATE);

        //checks if this email is already contained
        if(!sharedPreferences.contains(editText.getText().toString())) {
            Intent signupIntent = new Intent(this, Signup.class);
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
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.studious", Context.MODE_PRIVATE);
        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();

        sharedPreferences.edit().putString("email", email).apply();
        sharedPreferences.edit().putString("password", password).apply();

        // instantiate dbHelper to check if correct login info was inputted
        Context context = getApplicationContext();
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("data", Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);
        int loginCheck = dbHelper.checkUserLogin(email, password);

        if (loginCheck == 1) { // correct login, go to home screen
            // TODO: HOME SCREEN INTENT ISN'T WORKING, IT'S CRASHING THE APP (that's why it's set to AddClasses rn
            Intent loginIntent = new Intent(this, AddClasses.class);
            loginIntent.putExtra("login_info", new String[]{email, password}); // don't know if this is needed rn
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
