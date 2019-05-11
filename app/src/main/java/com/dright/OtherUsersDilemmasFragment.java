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

public class OtherUsersDilemmasFragment extends Fragment {

    View ProfileView;
    private DatabaseReference db;
    private FirebaseAuth currentUser;
    RecyclerView recyclerView;
    public ArrayList<DilemmaModel> mDilemma = new ArrayList<>();
    public  ArrayList<String> keys = new ArrayList<>();

    public  ArrayList<String> commentsNumber = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileView = inflater.inflate(R.layout.other_users_dilemmas_in_progress,container,false);
        recyclerView = ProfileView.findViewById(R.id.other_users_dilemmas_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProfileView.getContext()));
        currentUser = FirebaseAuth.getInstance();
        db= FirebaseDatabase.getInstance().getReference("Users/"+ OtherUsersProfile.profilekey+"/dilemasInProgress");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mDilemma.clear();
                keys.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    keys.add(ds.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        db= FirebaseDatabase.getInstance().getReference("DilemaComments");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsNumber.clear();
                for(int i=0;i<keys.size();i++)
                {
                    commentsNumber.add(String.valueOf(dataSnapshot.child(keys.get(i)).getChildrenCount()));
                    Log.d("edyte","ok");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        db= FirebaseDatabase.getInstance().getReference("Dilema");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(keys.size() > 0) {
                    for (int i = 0; i < keys.size(); i++) {
                        String dilemaId = keys.get(i);
                        String dilemaDesc = dataSnapshot.child(keys.get(i)).child("dilemaDescription").getValue(String.class);
                        Long dilemaTime = dataSnapshot.child(keys.get(i)).child("dilemaTimeOut").getValue(Long.class);
                        //komentet duhet me i mar ktu numrin;
                        DilemmaModel dilemmaModel = new DilemmaModel(dilemaDesc, "10", String.valueOf(dilemaTime), dilemaId);
                        mDilemma.add(i, dilemmaModel);
                        Log.d("newdatachange", dilemaId + " . " + dilemaDesc + " . " + dilemaTime);
                        final DilemmaRecyclerViewAdapter adapter = new DilemmaRecyclerViewAdapter(ProfileView.getContext(), mDilemma);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        return ProfileView;    }
}


