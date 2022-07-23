package com.example.fyp.AppDrawer;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.fyp.FlightFolder.FlightFragment;
import com.example.fyp.FoodFolder.FoodFragment;
import com.example.fyp.HomeFragment;
import com.example.fyp.R;
import com.example.fyp.UserFolder.User;
import com.example.fyp.UserFolder.UserFragment;
import com.example.fyp.UtilitiesFolder.UtilitiesFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private TextView email,username;
    private FirebaseAuth firebaseAuth;

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
        View header = navigationView.getHeaderView(0);
        email= header.findViewById(R.id.nav_header_email);
        username= header.findViewById(R.id.nav_header_username);

        //handles the hamburger menu in the top left of the screen, requires strings as input for visually impaired people
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();//handles the rotating icon

        //set default fragment to the home fragment
        // the if makes sure that the fragment is not reset everytime the phone is rotated

        if(savedInstanceState == null) {//only if the state hasnt been set in the current session
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }


        //get the current user email and username and display it in the nav header

        firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    email.setText(user.getEmail());
                    username.setText(user.getUsernname());

                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }
        });
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
            case R.id.nav_home:
                //pass the frame layout inside the linear layout of the nav drawer the fragment selected
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;
            case R.id.nav_flight:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FlightFragment()).commit();
                break;
            case R.id.nav_food:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FoodFragment()).commit();
                break;
            case R.id.nav_utilities:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new UtilitiesFragment()).commit();
                break;
            case R.id.nav_account:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new UserFragment()).commit();
                break;
            case R.id.nav_groups:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FlightFragment()).commit();
            case R.id.nav_pledge:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FlightFragment()).commit();
                break;

        }
    drawer.closeDrawer(GravityCompat.START);


        return true;
    }
}