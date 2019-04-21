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
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchUsersFragment extends Fragment {

    View ProfileView;
    RecyclerView recyclerView;
    public ArrayList<ProfileModel> mUser = new ArrayList<>();
    SearchView sv;

    private DatabaseReference db;
    private FirebaseAuth currentUser;
    public  ArrayList<String> keys = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileView = inflater.inflate(R.layout.search_users,container,false);


        currentUser = FirebaseAuth.getInstance();
        db= FirebaseDatabase.getInstance().getReference("Users");


        sv = ProfileView.findViewById(R.id.mSearchText);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String userkey = ds.getKey().toString();
                    keys.add(userkey);


                }
                for (int i = 0; i < keys.size(); i++) {
                    String hash = keys.get(i);
                    String name = dataSnapshot.child(keys.get(i)).child("name").getValue(String.class);
                    ProfileModel profileModel = new ProfileModel(name, hash);
                    mUser.add(i,profileModel);
                    Log.d("hash", hash);
                    Log.d("name", name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        recyclerView = ProfileView.findViewById(R.id.recycler_view);
        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(ProfileView.getContext(),mUser);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProfileView.getContext()));
        recyclerView.setVisibility(View.INVISIBLE);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                recyclerView.setVisibility(View.VISIBLE);
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return false;
            }
        });

        return ProfileView;
    }

}
