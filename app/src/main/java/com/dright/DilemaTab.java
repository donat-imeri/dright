package com.dright;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.ToxicBakery.viewpager.transforms.CubeInTransformer;
import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.ToxicBakery.viewpager.transforms.FlipHorizontalTransformer;
import com.ToxicBakery.viewpager.transforms.RotateDownTransformer;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.ToxicBakery.viewpager.transforms.ScaleInOutTransformer;
import com.ToxicBakery.viewpager.transforms.ZoomInTransformer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DilemaTab extends Fragment {
    private static final String TAG = "DilemaTab";
    private View view;
    private static List<Dilema> listDilema = new ArrayList<>();
    private List<Boolean> listDilemaCheck = new ArrayList<>();
    private String currUser = "";
    private static List<String> listDilemaId = new ArrayList<>();
    private List<String> listUserFollowing;
    private List<String> listDilemaVoters = new ArrayList<>();
    private List<Integer> listDilemaPriority = new ArrayList<>();
    public static List<String> dilematFollowing;
    private int count = 0;
    private int count1 = 0;

    private List<String> mListDilemaVoters;

    public static FragmentStatePagerAdapter adapterViewPager;
    private ViewPager vpPager;
    private DatabaseReference mDatabase;
    private static FirebaseAuth auth;
    private static FirebaseDatabase fb;
    private static boolean check = false;

    //donat
    public static List<String> lastDilemaIdList;
    public static List<String> newDilemaList;
    public static List<Integer> priorityList;
    public static List<String> myDilemas;
    public static int counter, priority;
    public static boolean firstItem;

    public static List<Dilema> prioritySortedDilema;
    public static List<String> prioritySortedId;
    //donat


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.dilema_tab, container, false);
        Log.d("Dilema Tab layout","cccc");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();

        Log.d("Dilema Tab","cccc");

        vpPager = (ViewPager) activity.findViewById(R.id.vpPager);

        auth=FirebaseAuth.getInstance();

        currUser = auth.getUid();


        //donat
        fb=FirebaseDatabase.getInstance();
        counter=0;
        priority=5;
        firstItem=true;
        lastDilemaIdList=new ArrayList<>();
        newDilemaList=new ArrayList<>();
        priorityList=new ArrayList<>();
        myDilemas=new ArrayList<>();

        dilematFollowing=new ArrayList<>();

        DatabaseReference db1 =fb.getReference("DilemaVotersTemporary/"+currUser);
        db1.setValue("");


        DatabaseReference getMyDecisions=fb.getReference("Users/"+auth.getUid()+"/dilemasInProgress");
        getMyDecisions.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    myDilemas.add(d.getValue(String.class));
                    Log.d("my dilemas", d.getValue(String.class)+"aaaaaa");
                }

                DatabaseReference getLastIds=fb.getReference("UserLastId").child(auth.getUid());
                getLastIds.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            lastDilemaIdList.add(ds.getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        readUserFollowing();

        Log.d(TAG, "onCreateView: after viewpager vpPager = " + vpPager);

        Log.d(TAG, "onCreateView: after pagetransformer");

    }



    public static class MyPagerAdapter extends FragmentStatePagerAdapter {
        private List<Dilema> mListDilema;
        private List<String> mListDilemaId;

        public MyPagerAdapter(FragmentManager fragmentManager, List<Dilema> mListDilema,  List<String> mListDilemaId) {
            super(fragmentManager);
            this.mListDilema = mListDilema;
            this.mListDilemaId = mListDilemaId;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            Log.d(TAG, "getCount: ListDilema Size: "+mListDilema.size());
            if(mListDilema.size() != 0) {
                return mListDilema.size();
            }
            else
                return  0;
        }


        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {

            if (mListDilema.size()==position+1){
                newDilemaList.clear();
                readNext10(0,5);
            }

            Log.d(TAG, "getItem: Inside getItem check = "+mListDilema.get(position).isDilemaText());
            Log.d(TAG, "getItem: inside getItem size: "+mListDilemaId.size());
            Log.d(TAG, "getItem: position: "+position);
            if(mListDilema.get(position).isDilemaText()) {
                Log.d(TAG, "getItem: inside getItem descr: "+mListDilema.get(position).getDilemaDescription());
                Log.d(TAG, "dilemaId before sending: "+mListDilemaId.get(position)+"bbbbb");

                return FragmentDilemaOptions.newInstance(mListDilema.get(position), mListDilemaId.get(position), position, priorityList.get(position));
            }else{
                Log.d(TAG, "getItem: inside getItem descr: "+mListDilema.get(position).getDilemaDescription());
                return FragmentDilemaImageOptions.newInstance(mListDilema.get(position),mListDilemaId.get(position), position, priorityList.get(position));
            }
           // return FragmentDilemaOptions.newInstance(mListDilema.get(position));


        }


        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

    }



    private  void readUserFollowing(){
        listUserFollowing = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference mDatabase1 = mDatabase.child(currUser);
        DatabaseReference mDatabase2 = mDatabase1.child("following");
        mDatabase2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    listUserFollowing.add(ds.getKey());
                }
                //hasVoted(listUserFollowing);


                Log.d(TAG, "onDataChange: userFollowing: "+listUserFollowing.size());
                dilemasInProgress(listUserFollowing);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //qitu ndryshime
    private void dilemasInProgress(final List<String> userFollowing){
        dilematFollowing = new ArrayList<>();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users");

        for(int i=0;i< userFollowing.size();i++){
            Log.d(TAG, userFollowing.get(i)+"aaaa");
            db.child(userFollowing.get(i)).child("dilemasInProgress").addListenerForSingleValueEvent(new ValueEventListener() {


                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()) {

                            Log.d(TAG, "onDataChange: dataSnapshotChild: "+ ds.getValue(String.class)+"aaaa");
                            dilematFollowing.add(ds.getValue(String.class));

                        }
                        count++;
                        Log.d("userFollowing Size","dddd"+userFollowing.size());
                        Log.d("count Size","dddd"+count);
                        if(count==userFollowing.size()){
                            Log.d(TAG, "onDataChange: u thirr: "+dilematFollowing.size());
                                hasVoted(userFollowing, dilematFollowing);
                            }
                        Log.d(TAG, "onDataChange: dilematFollowing: "+dilematFollowing.size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            Log.d(TAG, "dilemasInProgress: inside for loop tek userFollowing: "+dilematFollowing.size());
        }
        /*
        Log.d(TAG, "dilemasInProgress: outside for loop userFollowing: "+dilematFollowing.size());
        hasVoted(userFollowing,dilematFollowing);*/


    }

    private void hasVoted(final List<String> userFollowing, final List<String> dilFollowing){
        Log.d(TAG, "hasVoted: dilemaFollowingsize: "+ dilFollowing.size());
        final List<String> dilemaVoted = new ArrayList<>();
        DatabaseReference mDb1 = FirebaseDatabase.getInstance().getReference("DilemaVoters");
        DatabaseReference mDb2 = mDb1.child(currUser);
        mDb2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    dilemaVoted.add(ds.getKey());
                }
                Log.d(TAG, "onDataChange: dilemaVoted: " + dilemaVoted.size());

                filterDilematVoted(userFollowing,dilFollowing,dilemaVoted);
                //readFromDb(dilemaVoted,userFollowing);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void filterDilematVoted(final List<String> userFollowing, final List<String> dilFollowing, final List<String> dilemaVoted){
        final List<String> neededDilema = new ArrayList<>();

        for(int i=0; i <dilFollowing.size();i++){
            int counter = 0;
            for(int j=0; j<dilemaVoted.size();j++){
                if(dilFollowing.get(i).equals(dilemaVoted.get(j))){
                    counter++;
                }
            }
            if(counter == 0){
                neededDilema.add(dilFollowing.get(i));
                priorityList.add(0);
            }
        }

        Log.d(TAG, "filterDilematVoted: neededDilema: "+neededDilema.size());
        getDilemas(neededDilema);


    }


    //qitu duhet ndryshime
    private void getDilemas(final List<String> neededDilema){
        listDilemaId=neededDilema;

        final List<Dilema> insideDilemaList = new ArrayList<>();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Dilema/");
        for(int i =0; i<neededDilema.size();i++) {
            databaseRef.child(neededDilema.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.getValue() != null) {
                        Dilema objD = (Dilema) dataSnapshot.getValue(Dilema.class);
                        Log.d(TAG, "onDataChange: dataSnapshot.key: " + dataSnapshot.getKey());
                        Log.d(TAG, "onDataChange: dataSnapshot." + objD.getDilemaAsker());
                        Log.d(TAG, "onDataChange: dataSnapshot. timeCreated: "+objD.getTimeCreate());
                        Log.d(TAG, "onDataChange: dataSnapshot.timeCreated: "+objD.getTimeCreate());
                        insideDilemaList.add(objD);
                        count1++;
                    }

                    Log.d("userFollowing Size","dddd"+neededDilema.size());
                    Log.d("count Size","dddd"+count1);
                        if(count1 == neededDilema.size()){
                            Log.d(TAG, "onDataChange: u thirr edhe kjo asa :"+insideDilemaList.size());
                            listDilema=insideDilemaList;
                            addToAdapter(listDilema, listDilemaId);
                        }


                    Log.d(TAG, "onDataChange: getDilemas: " + insideDilemaList.size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Log.d(TAG, "getDilemas: inside for loop getting dilemas: "+insideDilemaList.size());
        }


        Log.d(TAG, "getDilemas: after getting dilemas: "+insideDilemaList.size());
    }

    private void addToAdapter(List<Dilema> finalDilemaList, List<String> finalIdList){
        if(finalDilemaList.size() != 0){
            adapterViewPager = new MyPagerAdapter(getChildFragmentManager(), finalDilemaList, finalIdList);
            Log.d(TAG, "onCreateView: Adapter " + finalIdList.size()+"bbbb");
            adapterViewPager.notifyDataSetChanged();
            vpPager.setAdapter(adapterViewPager);
            Log.d(TAG, "onCreateView: after setting adapter");
            vpPager.setPageTransformer(true, new RotateUpTransformer());
        }

    }

    public static void readNext10(int c, final int p){
        counter=c;
        priority=p;
        firstItem=true;

        Log.d("PRiority", priority +"aaaa");
        Log.d("Counter", counter +"aaaa");
        Query db=FirebaseDatabase.getInstance().getReference("DilemaPriorities").child(String.valueOf(priority))
                .orderByKey()
                .startAt(lastDilemaIdList.get(priority)).limitToFirst(11);

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s: dataSnapshot.getChildren()){
                    if (firstItem) {
                        firstItem=false;
                        continue;
                    }
                    Log.d("Dilma id aaaa", s.getKey()+"aaaa");

                    lastDilemaIdList.set(priority,s.getKey());


                    if (!dilematFollowing.contains(s.getKey()) && !myDilemas.contains(s.getKey())){
                        Log.d("Dilma put on list", s.getKey()+"aaaa");
                        newDilemaList.add(s.getKey());
                        listDilemaId.add(s.getKey());
                        priorityList.add(priority);
                        counter++;

                        if (counter>=10) break;
                    }
                }

                //donat
                //DatabaseReference getLastIds=fb.getReference("UserLastId").child(auth.getUid());
                //getLastIds.child(String.valueOf(priority)).setValue(lastDilemaIdList.get(priority));

                if (counter<10){
                    priority--;
                    if (priority>=0){
                        Log.d("Brenda if","aaaaa");
                        readNext10(counter, priority);

                    }
                }
                if (counter>=10 || priority<0) {
                    Log.d("Brenda else readNext", "aaaaa");
                    Log.d("New dilema list size", newDilemaList.size()+" aaaaa");
                    for (int j = 0; j < newDilemaList.size(); j++) {
                        DatabaseReference getDilema = FirebaseDatabase.getInstance().getReference("Dilema")
                                .child(newDilemaList.get(j));
                        final int finalJ = j;
                        getDilema.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Dilema d=dataSnapshot.getValue(Dilema.class);
                                listDilema.add(d);
                                if (finalJ == newDilemaList.size() - 1) {
                                    Log.d("Ketu jemi", "aaaaa");
                                        adapterViewPager.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                //Notify data set changed
                //append nweDilemaList to actualDilemaList

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
