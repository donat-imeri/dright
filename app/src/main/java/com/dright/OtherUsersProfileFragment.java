package com.dright;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


public class OtherUsersProfileFragment extends Fragment {
    View ProfileView;
    Button mSubscribe;
    Button mUnsubscribe;
    TextView userName;
    TextView userAddress;
    TextView userEmail;
    TextView userFacebook;
    TextView userTwitter;
    TextView userPhone;
    TextView userFollowers;
    TextView userFollowing;
    ImageView profilePicture;
    private DatabaseReference db;
    private FirebaseAuth currentUser;
    String fullname= null;
    String hash = null;
    public String imageUrl;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileView = inflater.inflate(R.layout.other_profile,container,false);
        currentUser = FirebaseAuth.getInstance();
        mSubscribe = ProfileView.findViewById(R.id.btn_follow);
        mUnsubscribe = ProfileView.findViewById(R.id.btn_unfollow);

        userName = ProfileView.findViewById(R.id.user_tv_name);
        userAddress = ProfileView.findViewById(R.id.user_tv_address);
        userFacebook = ProfileView.findViewById(R.id.user_current_facebook);
        userTwitter = ProfileView.findViewById(R.id.user_current_twitter);
        userPhone = ProfileView.findViewById(R.id.user_current_phone);
        userEmail = ProfileView.findViewById(R.id.user_current_email);
        userFollowers = ProfileView.findViewById(R.id.user_followers);
        userFollowing = ProfileView.findViewById(R.id.user_following);
        profilePicture = ProfileView.findViewById(R.id.other_users_profile);



        mUnsubscribe.setVisibility(View.INVISIBLE);
        mUnsubscribe.setEnabled(false);

        Log.d("lol3", OtherUsersProfile.profilekey);
        db= FirebaseDatabase.getInstance().getReference("Users").child(OtherUsersProfile.profilekey);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName.setText(dataSnapshot.child("name").getValue().toString());
                userAddress.setText(dataSnapshot.child("address").getValue().toString());
                userPhone.setText(dataSnapshot.child("phone").getValue().toString());
                userFacebook.setText(dataSnapshot.child("facebook").getValue().toString());
                userTwitter.setText(dataSnapshot.child("twitter").getValue().toString());
                userEmail.setText(dataSnapshot.child("email").getValue().toString());
                userFollowing.setText(String.valueOf(dataSnapshot.child("following").getChildrenCount()));
                userFollowers.setText(String.valueOf(dataSnapshot.child("followers").getChildrenCount()));
                imageUrl = dataSnapshot.child("imageURL").getValue().toString();
                if(!imageUrl.equals(""))
                {
                    Glide.with(getContext()).load(imageUrl).apply(RequestOptions.circleCropTransform()).into(profilePicture);
                }
                if(dataSnapshot.child("followers").hasChild(currentUser.getUid()))
                {
                    mSubscribe.setEnabled(false);
                    mSubscribe.setVisibility(View.INVISIBLE);
                    mUnsubscribe.setEnabled(true);
                    mUnsubscribe.setVisibility(View.VISIBLE);
                }
                else{
                    mUnsubscribe.setEnabled(false);
                    mUnsubscribe.setVisibility(View.INVISIBLE);
                    mSubscribe.setEnabled(true);
                    mSubscribe.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = FirebaseDatabase.getInstance().getReference("Users").child(OtherUsersProfile.profilekey);
                mSubscribe.setVisibility(View.INVISIBLE);
                mSubscribe.setEnabled(false);
                mUnsubscribe.setVisibility(View.VISIBLE);
                mUnsubscribe.setEnabled(true);
                db.child("followers").child(currentUser.getUid()).setValue("Subbed");
                db = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
                db.child("following").child(OtherUsersProfile.profilekey).setValue("followed");


            }
        });
        mUnsubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db= FirebaseDatabase.getInstance().getReference("Users").child(OtherUsersProfile.profilekey);
                mUnsubscribe.setVisibility(View.INVISIBLE);
                mUnsubscribe.setEnabled(false);
                mSubscribe.setVisibility(View.VISIBLE);
                mSubscribe.setEnabled(true);
                db.child("followers").child(currentUser.getUid()).removeValue();
                db= FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
                db.child("following").child(OtherUsersProfile.profilekey).removeValue();


            }
        });


        return ProfileView;
    }
}
