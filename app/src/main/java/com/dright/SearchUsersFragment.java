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
import android.widget.TextView;

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
    TextView searchforusers;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileView = inflater.inflate(R.layout.search_users,container,false);



        currentUser = FirebaseAuth.getInstance();
        db= FirebaseDatabase.getInstance().getReference("Users");

        sv = ProfileView.findViewById(R.id.mSearchText);
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mUser.clear();
                    keys.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        String userkey = ds.getKey().toString();
                        keys.add(userkey);


                    }
                    for (int i = 0; i < keys.size(); i++) {
                        String hash = keys.get(i);
                        String name = dataSnapshot.child(keys.get(i)).child("name").getValue(String.class);
                        String profilepic = dataSnapshot.child(keys.get(i)).child("imageURL").getValue(String.class);
                        String address = dataSnapshot.child(keys.get(i)).child("address").getValue(String.class);
                        ProfileModel profileModel = new ProfileModel(name, profilepic, address, hash);
                        mUser.add(i, profileModel);
                        Log.d("hash", hash);
                        Log.d("name", name);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            recyclerView = ProfileView.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
            final RecyclerViewAdapter adapter = new RecyclerViewAdapter(recyclerView.getContext(),mUser);
            recyclerView.setAdapter(adapter);
            sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {

                    adapter.getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {

                    return false;
                }
            });
            if(sv.isIconified())
            {
                mUser.clear();
                Log.d("hahhaha","op");
            }


        return ProfileView;
    }

}
