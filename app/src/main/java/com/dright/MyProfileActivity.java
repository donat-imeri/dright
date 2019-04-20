package com.dright;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    private DatabaseReference db;
    private FirebaseAuth currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentUser = FirebaseAuth.getInstance();
        db= FirebaseDatabase.getInstance().getReference("Users/"+currentUser.getUid());
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ProfileFragment.fulltvname = dataSnapshot.child("name").getValue().toString();
                ProfileFragment.fulltvaddress = dataSnapshot.child("address").getValue().toString();

                ProfileFragment.email = dataSnapshot.child("email").getValue().toString();
                ProfileFragment.phone = dataSnapshot.child("phone").getValue().toString();
                ProfileFragment.twitter = dataSnapshot.child("twitter").getValue().toString();
                ProfileFragment.facebook = dataSnapshot.child("facebook").getValue().toString();

                ProfileFragment.following = dataSnapshot.child("following").getValue().toString();
                ProfileFragment.followers = dataSnapshot.child("followers").getValue().toString();

                Log.d("vlera",dataSnapshot.child("name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentManager fragmentActivity = getSupportFragmentManager();
                fragmentActivity.beginTransaction().replace(R.id.profile_content_frame
                        , new ProfileFragment()).commit();
            }
        },500);



    }

    @Override
    public void onBackPressed() {
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //if (drawer.isDrawerOpen(GravityCompat.START)) {
          //  drawer.closeDrawer(GravityCompat.START);
        //} else {
          //  super.onBackPressed();
        //}
        setContentView(R.layout.activity_my_profile);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fragmentActivity = getSupportFragmentManager();

        if(id == R.id.nav_myprofile)
        {
            fragmentActivity.beginTransaction().replace(R.id.profile_content_frame
                    , new ProfileFragment()).commit();
        }
        else if (id == R.id.nav_edit_myprofile) {
            fragmentActivity.beginTransaction().replace(R.id.profile_content_frame
                    , new EditProfileFragment()).commit();
        } else if (id == R.id.nav_followers_myprofile) {
            fragmentActivity.beginTransaction().replace(R.id.profile_content_frame
                    , new FollowersProfileFragment()).commit();
        } else {
            fragmentActivity.beginTransaction().replace(R.id.profile_content_frame
                    , new FollowingProfileFragment()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
