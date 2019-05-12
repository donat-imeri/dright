package com.dright;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static com.dright.EditProfileFragment.address;

public class ProfileFragment extends Fragment {

    View ProfileView;
    TextView currentfullname;
    TextView currentaddress;
    TextView currentphone;
    TextView currentfacebook;
    TextView currentfollowers;
    TextView currentfollowing;
    TextView currentemail;
    TextView currenttwitter;
    TextView userDocents;


    public  String fulltvname = null;
    public  String fulltvaddress = null;
    public  String email = null;
    public  String facebook = null;
    public  String followers = null;
    public  String following = null;
    public  String twitter = null;
    public  String phone = null;
    public String imageURL = null;
    ImageView profilePicture;

    private DatabaseReference db;
    public static FirebaseAuth currentUser;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileView = inflater.inflate(R.layout.myprofile,container,false);
        getActivity().setTitle("Profile");
        currentfullname = ProfileView.findViewById(R.id.tv_name);
        currentaddress  = ProfileView.findViewById(R.id.tv_address);
        currentphone    = ProfileView.findViewById(R.id.txt_current_phone);
        currentfacebook = ProfileView.findViewById(R.id.current_facebook);
        currentemail = ProfileView.findViewById(R.id.txt_current_email);
        currenttwitter = ProfileView.findViewById(R.id.current_twitter);
        currentfollowers = ProfileView.findViewById(R.id.followers);
        currentfollowing = ProfileView.findViewById(R.id.following);
        profilePicture = ProfileView.findViewById(R.id.myprofile_picture);
        userDocents = ProfileView.findViewById(R.id.docent_value);
        currentUser = FirebaseAuth.getInstance();
        db= FirebaseDatabase.getInstance().getReference("Users/"+currentUser.getUid());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fulltvname = dataSnapshot.child("name").getValue().toString();
                fulltvaddress = dataSnapshot.child("address").getValue().toString();
                email = dataSnapshot.child("email").getValue().toString();
                phone = dataSnapshot.child("phone").getValue().toString();
                facebook = dataSnapshot.child("facebook").getValue().toString();
                followers = String.valueOf(dataSnapshot.child("followers").getChildrenCount());
                following = String.valueOf(dataSnapshot.child("following").getChildrenCount());
                imageURL = dataSnapshot.child("imageURL").getValue().toString();
                userDocents.setText(String.valueOf(dataSnapshot.child("docents").child("amount").getValue(Integer.class))+" docents");
                currentfullname.setText(fulltvname);
                currentaddress.setText(fulltvaddress);
                currentfollowers.setText(followers);
                currentfollowing.setText(following);
                currentfacebook.setText(facebook);
                currenttwitter.setText(twitter);
                currentemail.setText(email);
                if(imageURL!=null){
                    if (!imageURL.equals(""))
                        Glide.with(ProfileView.getContext()).load(imageURL).apply(RequestOptions.circleCropTransform()).into(profilePicture);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });








        //if(bundle.getString("fullname") != null)
        //currentfullname.setText(bundle.getString("fullname"));
        //currentaddress.setText(bundle.getString("address"));
        //currentemail.setText(bundle.getString("email"));
        //currentfacebook.setText(bundle.getString("facebookaccount"));





        return ProfileView;

    }
}
