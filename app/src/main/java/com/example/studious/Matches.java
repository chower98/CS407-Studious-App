package com.example.studious;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Matches extends AppCompatActivity {

    private final static String EMAIL_KEY = "email";
    private final static String PASSWORD_KEY = "password";
    private final static String PACKAGE_NAME = "com.example.studious";
    BottomNavigationView bottomNavigation;
    private boolean newUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        // TODO: make connections a part of the actual layout file and not a fragment
        FragmentManager connectionsFragmentManager = getSupportFragmentManager();
        connectionsFragmentManager.popBackStack("root_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // Add the new tab fragment
        connectionsFragmentManager.beginTransaction()
                .replace(R.id.container, ConnectionsFragment.newInstance("",""))
                .addToBackStack("root_fragment")
                .commit();
    }
        BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.fragment_connections:
                                FragmentManager connectionsFragmentManager = getSupportFragmentManager();
                                connectionsFragmentManager.popBackStack("new_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                                // Add the new tab fragment
                                connectionsFragmentManager.beginTransaction()
                                        .replace(R.id.container, ConnectionsFragment.newInstance("",""))
                                        .addToBackStack("new_fragment")
                                        .commit();
                                return true;
                            case R.id.fragment_requests:
                                FragmentManager requestsFragmentManager = getSupportFragmentManager();
                                requestsFragmentManager.popBackStack("new_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                                // Add the new tab fragment
                                requestsFragmentManager.beginTransaction()
                                        .replace(R.id.container, RequestFragment.newInstance("",""))
                                        .addToBackStack("new_fragment")
                                        .commit();
                                return true;
                            case R.id.fragment_recommendations:
                                FragmentManager recFragmentManager = getSupportFragmentManager();
                                recFragmentManager.popBackStack("new_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                                // Add the new tab fragment
                                recFragmentManager.beginTransaction()
                                        .replace(R.id.container, RecommendationsFragment.newInstance("",""))
                                        .addToBackStack("new_fragment")
                                        .commit();
                                return true;
                            case R.id.home_screen:
                                Intent homeIntent = new Intent(Matches.this, HomeScreen.class);
                                startActivity(homeIntent);
                                return true;
                        }
                        return false;
                    }
                };

    public void backToHome(View view){
        Intent intent = new Intent(this, HomeScreen.class);
        intent.putExtra("newUser", newUser);
        startActivity(intent);
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.main_menu, menu);
//        return true;
//    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.logout:
//                // remove data kept in the instance for the user since they are logging out
//                SharedPreferences sharedPreferences = getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
//                sharedPreferences.edit().remove(EMAIL_KEY).apply();
//                sharedPreferences.edit().remove(PASSWORD_KEY).apply();
//
//                Intent logoutIntent = new Intent(this, Login.class);
//                startActivity(logoutIntent);
//                return true;
//
//            case R.id.preferences:
//                Intent preferencesIntent = new Intent(this, Preferences.class);
//                startActivity(preferencesIntent);
//                return true;
//
//            default: return super.onOptionsItemSelected(item);
//        }
//    }

}
