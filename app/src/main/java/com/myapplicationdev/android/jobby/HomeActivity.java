package com.myapplicationdev.android.jobby;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Todo: UI objects
    BottomNavigationView myBottomNavigationView;
    FrameLayout myFrameLayout;


    // Todo: Fragment objects
    DashBoardFragment myDashBoardFragment;
    IncomeFragment myIncomeFragment;
    ExpenseFragment myExpenseFragment;

    // Todo: Firebase objects
    FirebaseAuth myFirebaseAuth;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // A standard toolbar for use within application content.
        //A Toolbar is a generalization of action bars for use within application layouts.
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Mini Expense Manager");
        setSupportActionBar(myToolbar);

        myFirebaseAuth = FirebaseAuth.getInstance();

        myBottomNavigationView = findViewById(R.id.bottomNavigationbar);
        myFrameLayout = findViewById(R.id.main_frame);
        DrawerLayout myDrawerLayout = findViewById(R.id.drawer_layout);

// Todo: This object provides a handy way to
//  tie together the functionality of DrawerLayout
//  and the framework ActionBar to implement
//  the recommended design for navigation drawers.
        ActionBarDrawerToggle myActionBarDrawerToggle = new ActionBarDrawerToggle(
                HomeActivity.this, myDrawerLayout, myToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        // Todo: Adds the specified listener to the list of listeners
        //  that will be notified of drawer events.
        myDrawerLayout.addDrawerListener(myActionBarDrawerToggle);

        // Todo:  Synchronize the state of the drawer indicator/affordance with the linked DrawerLayout.
        myActionBarDrawerToggle.syncState();

// todo: This object represents a standard navigation menu for application.
//  The menu contents can be populated by a menu resource file.
        NavigationView myNavigationView = findViewById(R.id.naView);
        myNavigationView.setNavigationItemSelectedListener(HomeActivity.this);

        myDashBoardFragment = new DashBoardFragment();
        myIncomeFragment = new IncomeFragment();
        myExpenseFragment = new ExpenseFragment();

        setFragment(myDashBoardFragment);

        myBottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.dashboard:
                    setFragment(myDashBoardFragment);
                    myBottomNavigationView.setItemBackgroundResource(R.color.dashboard_color);
                    return true;

                case R.id.income:
                    setFragment(myIncomeFragment);
                    myBottomNavigationView.setItemBackgroundResource(R.color.income_color);
                    return true;

                case R.id.expense:
                    setFragment(myExpenseFragment);
                    myBottomNavigationView.setItemBackgroundResource(R.color.expense_color);
                    return true;

                default:
                    return false;

            }
        });

    }

    void setFragment(Fragment fragment) {

        FragmentTransaction myFragmentTransaction = getSupportFragmentManager().beginTransaction();
        myFragmentTransaction.replace(R.id.main_frame, fragment);
        myFragmentTransaction.commit();


    }

    @Override
    public void onBackPressed() {

        DrawerLayout myDrawerLayout = findViewById(R.id.drawer_layout);

        if (myDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            myDrawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }


    }


    @SuppressLint("NonConstantResourceId")
    public void displaySelectedListener(int itemId) {

        Fragment myFragment = null;

        switch (itemId) {

            case R.id.dashboard:
                myFragment = new DashBoardFragment();
                break;

            case R.id.income:
                myFragment = new IncomeFragment();
                break;

            case R.id.expense:
                myFragment = new ExpenseFragment();
                break;

            case R.id.logout:
                myFirebaseAuth.signOut();
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                break;

        }
        if (myFragment != null) {
            FragmentTransaction myFragmentTransaction = getSupportFragmentManager().beginTransaction();
            myFragmentTransaction.replace(R.id.main_frame, myFragment);
            myFragmentTransaction.commit();

        }
        DrawerLayout myDrawerLayout = findViewById(R.id.drawer_layout);
        myDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedListener(item.getItemId());
        return true;
    }
}
