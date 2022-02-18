package com.example.fyp;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

       // Toolbar toolbar = findViewById(R.id.toolbar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //handles the hamburger menu in the top left of the screen, requriers strings as input for visually impaired people
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();//handles the rotating icon

        //set default fragment to the flight fragment
        // the if makes sure that the fragment is not reset everytime the phone is rotated

        if(savedInstanceState == null) {//only if the state hasnt been set in the current session
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new FlightFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_flight);
        }

    }

    @Override
    public void onBackPressed() {
        //closes nav bar rather than leaving activity when back is pressed with nav bar open
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_flight:
                //pass the frame layout inside the linear layout of the nav drawer the fragment selected
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FlightFragment()).commit();
                break;
            case R.id.nav_food:
                //pass the frame layout inside the linear layout of the nav drawer the fragment selected
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FoodFragment()).commit();
                break;
            case R.id.nav_utilities:
                //pass the frame layout inside the linear layout of the nav drawer the fragment selected
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new UtilitiesFragment()).commit();
                break;
            case R.id.nav_account:
                //pass the frame layout inside the linear layout of the nav drawer the fragment selected
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FlightFragment()).commit();
                break;
            case R.id.nav_groups:
                //pass the frame layout inside the linear layout of the nav drawer the fragment selected
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FlightFragment()).commit();
            case R.id.nav_pledge:
                //pass the frame layout inside the linear layout of the nav drawer the fragment selected
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FlightFragment()).commit();
                break;

        }
    drawer.closeDrawer(GravityCompat.START);


        return true;
    }
}