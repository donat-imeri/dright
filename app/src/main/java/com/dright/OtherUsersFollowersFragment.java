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

public class OtherUsersFollowersFragment extends Fragment {

    View ProfileView;
    public ArrayList<ProfileModel> mOtherFollowers = new ArrayList<>();
    public  ArrayList<String> keys = new ArrayList<>();
    private DatabaseReference db;
    private FirebaseAuth currentUser;
    RecyclerView recyclerView;
    String hash = null;
    String fullname = null;
    String profilepic = null;
    boolean once = true;
    int count=0;
    int currentcount=0;
    String address = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileView = inflater.inflate(R.layout.other_followers,container,false);
        //OtherUsersProfile.toolbar3.setTitle("Followers");
        currentUser = FirebaseAuth.getInstance();
        recyclerView = ProfileView.findViewById(R.id.other_followers_recycler_view);
        Log.d("otherusersfragment","thirret");
        db= FirebaseDatabase.getInstance().getReference("Users").child(OtherUsersProfile.profilekey).child("followers");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                keys.clear();
                mOtherFollowers.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    keys.add(ds.getKey().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        db= FirebaseDatabase.getInstance().getReference("Users");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mOtherFollowers.clear();
                if(keys.size() > 0) {
                    for (int i = 0; i < keys.size(); i++) {
                        Log.d("nedatachange2", keys.size() + "");
                        fullname = dataSnapshot.child(keys.get(i)).child("name").getValue().toString();
                        profilepic = dataSnapshot.child(keys.get(i)).child("imageURL").getValue().toString();
                        address = dataSnapshot.child(keys.get(i)).child("address").getValue().toString();
                        hash = dataSnapshot.child(keys.get(i)).getKey();
                        ProfileModel profileModel = new ProfileModel(fullname, profilepic, address, hash);
                        mOtherFollowers.add(profileModel);
                        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(recyclerView.getContext(), mOtherFollowers);
                        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                        recyclerView.setAdapter(adapter);
                    }
                }
                else
                {
                    final RecyclerViewAdapter adapter = new RecyclerViewAdapter(recyclerView.getContext(), mOtherFollowers);
                    recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Log.d("modeli2","sa");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final RecyclerViewAdapter adapter = new RecyclerViewAdapter(recyclerView.getContext(),mOtherFollowers);
                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                recyclerView.setAdapter(adapter);


            }
        },500);



        return ProfileView;
    }
}
