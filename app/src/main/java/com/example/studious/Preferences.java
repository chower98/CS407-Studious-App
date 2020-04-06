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
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Preferences extends AppCompatActivity {

    private final static String EMAIL_KEY = "email";
    private final static String PASSWORD_KEY = "password";
    private final static String PACKAGE_NAME = "com.example.studious";

    private Button homeButton;
    private boolean newUser;

    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        homeButton = findViewById(R.id.homeButton);
        saveButton = findViewById(R.id.saveButton);

        // get intent and get the value of newUser
        Intent intent = getIntent();
        newUser = intent.getBooleanExtra("newUser", false);

        if (newUser) { // new user choosing preferences, so buttonHome will say "Finished!"
            homeButton.setText("Finished!");
            saveButton.setVisibility(View.GONE); // it's going to save b/c this is first time, don't need a save button yet

            // show dialog telling new user what to do
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Now that you've added all your classes, let's set your preferences. " +
                    "This will help us match you to study buddies with similar studying " +
                    "preferences! Once you are done, press the Finished button at the bottom of " +
                    "your screen to complete your sign up.\n" +
                    "If you want to change your options, you can come back to this page at any time. " +
                    "Changing your preferences will not affect any matches you already have, it will only " +
                    "change how you are matched to others in the future.");
            builder.setTitle("Continuing On!");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // When the user click yes button, the dialog will close
                    dialog.dismiss();
                }
            });
            AlertDialog newUserWelcome = builder.create();
            newUserWelcome.show();

        } else { // not a new user, do not show dialog, and buttonHome should say "Back to Home" & there should be a save changes button
            homeButton.setText("Back to Home");
            saveButton.setText("Save Changes");
            //add functionality for save changes button click

        }
    }

    public void backToHome(View view){
        Intent intent = new Intent(this, HomeScreen.class);
        intent.putExtra("newUser", newUser);
        startActivity(intent);
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
