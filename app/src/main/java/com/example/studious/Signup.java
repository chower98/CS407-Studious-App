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

public class Signup extends AppCompatActivity {
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

    public void signUpClick(View view) {
        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
            email = emailInput.getText().toString();
            password = passwordInput.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.studious", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString("email", email).apply();
            sharedPreferences.edit().putString("password", password).apply();

        // instantiate dbHelper to add new user
        Context context = getApplicationContext();
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("data", Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);

        int userExists = dbHelper.checkUserLogin(email, password);
        if (userExists == 0) { // 0 = user doesn't exist
            dbHelper.addUser(email, password);
            Log.e("Called addUser()", "should have");

            Intent intent = new Intent(this, AddClasses.class);
            startActivity(intent);
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
