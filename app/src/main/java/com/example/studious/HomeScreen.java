package com.example.studious;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class HomeScreen extends AppCompatActivity {
    private boolean newUser;

    private final static String EMAIL_KEY = "email";
    private final static String PASSWORD_KEY = "password";
    private final static String PACKAGE_NAME = "com.example.studious";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // get intent and get the value of newUser
        Intent intent = getIntent();
        newUser = intent.getBooleanExtra("newUser", false);
        
        if (newUser) { // new user finishing sign up, display dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This is your home page! This is where you will be directed to in " +
                    "the future when you log in. You can access everything in the app through your " +
                    "home page. Remember, you can edit your classes and preferences at any time. " +
                    "Any changes will NOT affect your current matches, only your future ones.\n" +
                    "And that finishes your sign up! We're so excited to have you join us, and " +
                    "we hope to match you to many study buddies. Happy studying!");
            builder.setTitle("Welcome to Studious!");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // When the user click yes button, the dialog will close
                    dialog.dismiss();
                }
            });
            AlertDialog newUserWelcome = builder.create();
            newUserWelcome.show();
        }
    }

    public void coursesClick(View view) {
        Intent coursesIntent = new Intent(this, AddClasses.class);
        startActivity(coursesIntent);
    }

    public void matchesClick(View view) {
        Intent matchesIntent = new Intent(this, Matches.class);
        //matchesIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(matchesIntent);
    }

    public void messagesClick(View view) {
        Intent messagesIntent = new Intent(this, Messages.class);
        startActivity(messagesIntent);
    }

    public void preferencesClick(View view) {
        Intent preferencesIntent = new Intent(this, Preferences.class);
        startActivity(preferencesIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                // remove data kept in the instance for the user since they are logging out
                SharedPreferences sharedPreferences = getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
                sharedPreferences.edit().remove(EMAIL_KEY).apply();
                sharedPreferences.edit().remove(PASSWORD_KEY).apply();

                Intent logoutIntent = new Intent(this, Login.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logoutIntent);
                return true;

            case R.id.preferences:
                Intent preferencesIntent = new Intent(this, Preferences.class);
                startActivity(preferencesIntent);
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

}
