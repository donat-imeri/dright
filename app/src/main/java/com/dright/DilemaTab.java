package com.dright;

import android.app.Activity;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DilemaTab extends Fragment {
    private static final String TAG = "DilemaTab";
    private TextView txtCategory;
    private View view;
    private static List<Dilema> listDilema = new ArrayList<>();
    private static List<Boolean> listDilemaCheck = new ArrayList<>();
    public static String currUser = "";
    private static List<String> listDilemaId = new ArrayList<>();
    private static List<String> listUserFollowing;
    private List<String> listDilemaVoters = new ArrayList<>();

    private List<String> mListDilemaVoters;

    public FragmentStatePagerAdapter adapterViewPager;
    private Spinner spinner;
    private ViewPager vpPager;
    private DatabaseReference mDatabase;
    private static boolean check = false;
    private String[] itemsCategory = {"All", "Food","Sport", "Clothes"};


    //donat
    public static List<String> lastDilemaIdList;
    public static List<String> newDilemaList;
    public static int counter, priority;
    //donat


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.dilema_tab, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();

        vpPager = (ViewPager) activity.findViewById(R.id.vpPager);
        spinner = activity.findViewById(R.id.spinner);

        spinner.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, itemsCategory));
        currUser = FirebaseAuth.getInstance().getUid();
        readUserFollowing();
        hasVoted();


        //donat
        counter=0;
        priority=5;
        lastDilemaIdList=new ArrayList<>();
        newDilemaList=new ArrayList<>();
        lastDilemaIdList.add("-LeAxgnILiW_Ai9ieWq7");
        lastDilemaIdList.add("-Ld624Fk0XH1e8VHfVNU");
        lastDilemaIdList.add("-Ld624Fk0XH1e8VHfVNU");
        lastDilemaIdList.add("-Ld624Fk0XH1e8VHfVNU");
        lastDilemaIdList.add("-LeBHskAh6I1vEXuCTAO");
        lastDilemaIdList.add("-LeC6CagoscdIVkOicRg");

        //donat



        Log.d(TAG, "onCreateView: after viewpager vpPager = " + vpPager);

        Log.d(TAG, "onCreateView: after pagetransformer");

    }

    public static class MyPagerAdapter extends FragmentStatePagerAdapter {
        private List<Dilema> mListDilema;
        private List<Boolean> mListDilemaCheck;
        private List<String> mListDilemaId;

        public MyPagerAdapter(FragmentManager fragmentManager, List<Dilema> mListDilema, List<Boolean> mListDilemaCheck, List<String> mListDilemaId) {
            super(fragmentManager);
            this.mListDilema = mListDilema;
            this.mListDilemaCheck = mListDilemaCheck;
            this.mListDilemaId = mListDilemaId;
            Log.d(TAG, "MyPagerAdapter: Inside fragmentpageradapter");
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

            Log.d(TAG, "getItem: Inside getItem check = "+check);
            if(mListDilemaCheck.get(position)) {
                return FragmentDilemaOptions.newInstance(mListDilema.get(position),mListDilemaId.get(position));
            }else{
                return FragmentDilemaImageOptions.newInstance(mListDilema.get(position),mListDilemaId.get(position));
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void hasVoted(){
        final List<String> voters = new ArrayList<>();
        DatabaseReference mDb1 = FirebaseDatabase.getInstance().getReference("DilemaVoters");
        mDb1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if(ds.hasChild(currUser)){
                        voters.add(ds.getKey());
                    }
                }

                readFromDb(voters);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void readFromDb(final List<String> votedDilema){
        listDilema = new ArrayList<>();
        listDilemaCheck = new ArrayList<>();
        listDilemaId = new ArrayList<>();

        Log.d(TAG, "readFromDatabase: inside ");

        mDatabase = FirebaseDatabase.getInstance().getReference("Dilema/");
        Log.d(TAG, "readFromDatabase: " + mDatabase.getKey());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: inside for loop" + ds.getValue(Dilema.class).getDilemaDescription());
                    Log.d(TAG, "onDataChange: has Child: "+ ds.hasChild("dilemaText"));
                    Log.d(TAG, "onDataChange: listUserFollowing.size = "+listUserFollowing.size());
                    Log.d(TAG, "onDataChange: currentUser: "+currUser);
                    //hasVoted(ds.getKey());
                    Log.d(TAG, "onDataChange: listDIlemaVoters size: "+listDilemaVoters.size());
                    for(int i=0; i <listUserFollowing.size();i++)
                    {
                        Log.d(TAG, "onDataChange: inside for loop");
                        Log.d(TAG, "onDataChange: dilemaAsker");
                        Log.d(TAG, "onDataChange: dilemaASker equals : "+ds.child("dilemaAsker").getValue(String.class).equals(listUserFollowing.get(i)));
                        if(ds.child("dilemaAsker").getValue(String.class).equals(listUserFollowing.get(i))){
                            int counter = 0;
                            for(int j=0;j<votedDilema.size();j++)
                            {
                                if(ds.getKey().equals(votedDilema.get(j))){
                                    counter++;
                                }
                            }
                            if(counter == 0){
                                if(ds.child("dilemaText").getValue(Boolean.class).toString().equalsIgnoreCase("true")) {
                                    check = true;
                                    Log.d(TAG, "onDataChange: inside has Child(dilemaText: "+check);
                                }
                                else{
                                    Log.d(TAG, "onDataChange: inside has Child(dilemaText: "+check);
                                    check = false;
                                }
                                listDilemaId.add(ds.getKey());
                                Dilema objD = ds.getValue(Dilema.class);
                                listDilema.add(objD);
                                listDilemaCheck.add(check);
                            }







                        }
                    }


                }

                adapterViewPager = new MyPagerAdapter(getChildFragmentManager(),listDilema, listDilemaCheck,listDilemaId);
                Log.d(TAG, "onCreateView: Adapter " + adapterViewPager);
                adapterViewPager.notifyDataSetChanged();
                vpPager.setAdapter(adapterViewPager);
                Log.d(TAG, "onCreateView: after setting adapter");
                vpPager.setPageTransformer(true, new RotateUpTransformer());


            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public static void readNext10(int c, final int p){
        counter=c;
        priority=p;
        newDilemaList.clear();

        Log.d("PRiority", priority +"aaaa");
        Log.d("Counter", counter +"aaaa");
        Query db=FirebaseDatabase.getInstance().getReference("DilemaPriorities").child(String.valueOf(priority))
                .orderByKey()
                .startAt(lastDilemaIdList.get(priority)).limitToFirst(10);

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s: dataSnapshot.getChildren()){
                    Log.d("Dilma id aaaa", s.getKey()+"aaaa");

                    lastDilemaIdList.set(priority,s.getKey());
                    newDilemaList.add(s.getKey());
                    counter++;
                }
                if (counter<10){
                    priority--;
                    if (priority>=0)
                    readNext10(counter, priority);
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
