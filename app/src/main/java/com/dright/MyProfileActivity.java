package com.dright;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    private DatabaseReference db;
    private FirebaseAuth currentUser;
    Runnable run;
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


        FragmentManager fragmentActivity = getSupportFragmentManager();
        fragmentActivity.beginTransaction().replace(R.id.profile_content_frame
                , new ProfileFragment()).commit();
        Log.d("runnable","thirret");








    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, DecisionsAcitivity.class);
        startActivity(intent);
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
        } else if(id == R.id.nav_following_myprofile){
            fragmentActivity.beginTransaction().replace(R.id.profile_content_frame
                    , new FollowingProfileFragment()).commit();
        }
        else if(id == R.id.nav_search_profiles){
            fragmentActivity.beginTransaction().replace(R.id.profile_content_frame
                    , new SearchUsersFragment()).commit();
        }
        else{
            fragmentActivity.beginTransaction().replace(R.id.profile_content_frame
                    , new DilemmasinProgressFragment()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

class ProfileModel {

    public String name;
    public String hash;
    public String image;
    public String address;


    public ProfileModel(String name, String image , String address, String hash) {
        this.name = name;
        this.hash = hash;
        this.address = address;
        this.image = image;

    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getHash() {
        return hash;
    }

    public String getImage() {
        return image;
    }
}

class DilemmaModel
{
    public String description;
    public String commentNumber;
    public String timeLeft;
    public String hash;

    public DilemmaModel(String description, String commentNumber, String timeLeft, String hash) {
        this.description = description;
        this.commentNumber = commentNumber;
        this.timeLeft = timeLeft;
        this.hash = hash;
    }

    public String getDescription() {
        return description;
    }

    public String getCommentNumber() {
        return commentNumber;
    }

    public String getTimeLeft() {
        return timeLeft;
    }

    public String getHash() {
        return hash;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCommentNumber(String commentNumber) {
        this.commentNumber = commentNumber;
    }

    public void setTimeLeft(String timeLeft) {
        this.timeLeft = timeLeft;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
