package com.dright;

import android.os.Bundle;
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
    public  ArrayList<String> keys;
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
        currentUser = FirebaseAuth.getInstance();
        keys = new ArrayList<>();
        Log.d("otherusersfragment","thirret");
        db= FirebaseDatabase.getInstance().getReference("Users").child(OtherUsersProfile.profilekey).child("followers");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentcount = 0;
                count=0;
                mOtherFollowers.clear();
                keys.clear();
                Log.d("otherusersfragment","hyri ne datachange");
               if(dataSnapshot.hasChildren()) {
                   Log.d("otherusersfragment", "hyri ne if");

                   for (DataSnapshot ds : dataSnapshot.getChildren()) {
                       Log.d("otherusersfragment", "hyri ne for");
                       String follower = ds.getKey().toString();
                       Log.d("follower", follower);
                       Log.d("keyssize",String.valueOf(keys.size()));
                       keys.add(follower);
                   }

                   for(int i=0;i<keys.size();i++)
                   {
                       db = FirebaseDatabase.getInstance().getReference("Users").child(keys.get(i));
                       db.addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               fullname = dataSnapshot.child("name").getValue(String.class);
                               profilepic = dataSnapshot.child("imageURL").getValue().toString();
                               address = dataSnapshot.child("address").getValue().toString();
                               hash = dataSnapshot.getKey();
                               ProfileModel profileModel = new ProfileModel(fullname,profilepic,address,hash);
                               mOtherFollowers.add(profileModel);
                               recyclerView = ProfileView.findViewById(R.id.other_followers_recycler_view);
                               final RecyclerViewAdapter adapter = new RecyclerViewAdapter(recyclerView.getContext(), mOtherFollowers);
                               recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                               recyclerView.setAdapter(adapter);
                           }
                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }


                       });
                   }

               }

               else{
                   recyclerView = ProfileView.findViewById(R.id.other_followers_recycler_view);
                   final RecyclerViewAdapter adapter = new RecyclerViewAdapter(recyclerView.getContext(),mOtherFollowers);
                   recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                   recyclerView.setAdapter(adapter);

               }





            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


        return ProfileView;
    }
}
