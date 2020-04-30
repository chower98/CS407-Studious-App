package com.example.studious;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

//import android.support.design.widget.BottomNavigationView;
//import android.support.v7.app.AppCompatActivity;

public class NavigationActivity extends AppCompatActivity {
    private BottomNavigationItemView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.fragment_connections:
                            openFragment(ConnectionsFragment.newInstance("", ""));
                            return true;
                        case R.id.fragment_request:
                            openFragment(RequestFragment.newInstance("", ""));
                            return true;
                        case R.id.fragment_matches:
                            openFragment(MatchesFragment.newInstance("", ""));
                            return true;
                    }
                    return false;
                }
            };

}
