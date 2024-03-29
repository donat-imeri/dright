package com.dright;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FollowingProfileFragment extends Fragment {

    View ProfileView;
    ArrayList<ProfileModel> mFollowing = new ArrayList<>();
    private DatabaseReference db;
    private FirebaseAuth currentUser;
    RecyclerView recyclerView;
    String hash = null;
    String fullname = null;
    String profilepic = null;
    String address = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileView = inflater.inflate(R.layout.following_myprofile,container,false);
        getActivity().setTitle("Following");
        currentUser = FirebaseAuth.getInstance();
        db= FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid()).child("following");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String following = ds.getKey().toString();
                    db= FirebaseDatabase.getInstance().getReference("Users").child(following);
                    db.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            fullname = dataSnapshot.child("name").getValue().toString();
                            profilepic = dataSnapshot.child("imageURL").getValue().toString();
                            address = dataSnapshot.child("address").getValue().toString();
                            hash = dataSnapshot.getKey().toString();
                            ProfileModel profileModel = new ProfileModel(fullname,profilepic,address,hash);
                            mFollowing.add(profileModel);
                            Log.d("modeli","sa");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Log.d("follower",following);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView = ProfileView.findViewById(R.id.following_recycler_view);
                final RecyclerViewAdapter adapter = new RecyclerViewAdapter(recyclerView.getContext(),mFollowing);
                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                recyclerView.setAdapter(adapter);


            }
        },500);

        return ProfileView;

    }
}
