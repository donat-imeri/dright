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
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DilemaTab extends Fragment {
    private static final String TAG = "DilemaTab";
    private View view;
    private  List<Dilema> listDilema = new ArrayList<>();
    private List<Boolean> listDilemaCheck = new ArrayList<>();
    private String currUser = "";
    private List<String> listDilemaId = new ArrayList<>();
    private List<String> listUserFollowing;
    private List<String> listDilemaVoters = new ArrayList<>();
    private List<Integer> listDilemaPriority = new ArrayList<>();
    private SwipeRefreshLayout mSwipeLayout ;
    private List<String> dilematFollowing;
    private static int count = 0;
    private static int count1 = 0;

    private List<String> mListDilemaVoters;

    public static FragmentStatePagerAdapter adapterViewPager;
    private ViewPager vpPager;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
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

        mSwipeLayout = (SwipeRefreshLayout) activity.findViewById(R.id.swipe_refresh_layout);
        auth=FirebaseAuth.getInstance();

        currUser = auth.getUid();
        readUserFollowing();

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
               readUserFollowing();
                Log.d(TAG, "onRefresh: refreshing");

               new Handler().postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       mSwipeLayout.setRefreshing(false);
                   }
               },500);
            }
        });


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
        private List<String> mListDilemaId;

        public MyPagerAdapter(FragmentManager fragmentManager, List<Dilema> mListDilema,  List<String> mListDilemaId) {
            super(fragmentManager);
            this.mListDilema = mListDilema;
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

            Log.d(TAG, "getItem: Inside getItem check = "+mListDilema.get(position).isDilemaText());
            Log.d(TAG, "getItem: inside getItem size: "+mListDilema.size());
            Log.d(TAG, "getItem: position: "+position);
            if(mListDilema.get(position).isDilemaText()) {
                Log.d(TAG, "getItem: inside getItem descr: "+mListDilema.get(position).getDilemaDescription());
                return FragmentDilemaOptions.newInstance(mListDilema.get(position), mListDilemaId.get(position));
            }else{
                Log.d(TAG, "getItem: inside getItem descr: "+mListDilema.get(position).getDilemaDescription());
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
                        if(count==userFollowing.size()){
                            Log.d(TAG, "onDataChange: u thirr: "+dilematFollowing.size());

                            hasVoted(userFollowing,dilematFollowing);
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
            }
        }

        Log.d(TAG, "filterDilematVoted: neededDilema: "+neededDilema.size());
        getDilemas(neededDilema);


    }


    //qitu duhet ndryshime
    private void getDilemas(final List<String> neededDilema){
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
                        if(count1 == neededDilema.size()){
                            Log.d(TAG, "onDataChange: u thirr edhe kjo :"+insideDilemaList.size());
                            //addToAdapter(insideDilemaList,neededDilema);
                            timeSort(insideDilemaList, neededDilema);
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
    private void timeSort(List<Dilema> currListDilema, List<String> idDilemas){
        List<Dilema> timeSortedDilema = new ArrayList<>();
        List<String> timeSortedId = new ArrayList<>();


        while(currListDilema.size() != 0){

            int k = 0;
            long max = currListDilema.get(k).getTimeCreate();
            Dilema objD = currListDilema.get(k);
            String id = "";
            Log.d(TAG, "timeSort: timeMax: "+max);
            for(int j=1;j< currListDilema.size();j++)
            {
                if(currListDilema.get(j).getTimeCreate()> max){
                    objD = currListDilema.get(j);
                    max = objD.getTimeCreate();
                    id = idDilemas.get(j);
                    k=j;
                    Log.d(TAG, "timeSort: k:"+k+", j:"+j);
                }
            }
            Log.d(TAG, "timeSort: max after sort: "+max);
            Log.d(TAG, "timeSort: objDesc"+objD.getDilemaDescription());
            timeSortedDilema.add(objD);
            timeSortedId.add(id);
            currListDilema.remove(k);
            idDilemas.remove(k);
            Log.d(TAG, "timeSort: currListDil size: "+currListDilema.size());
        }
        Log.d(TAG, "timeSort: timeSortedDilemas: " +timeSortedDilema.size());
        Log.d(TAG, "timeSort: timeSOrtedId: "+ timeSortedId.size());
        prioritySort(timeSortedDilema, timeSortedId);

    }
    private void prioritySort(List<Dilema> currListDilema, List<String> currListId){
        List<Dilema> prioritySortedDilema = new ArrayList<>();
        List<String> prioritySortedId = new ArrayList<>();

        while(currListDilema.size() != 0)
        {
            int k=0;
            Dilema objD = currListDilema.get(k);
            String id = "";
            int max = currListDilema.get(k).getDilemaPriority();
            Log.d(TAG, "prioritySort: priorityMax: "+max);
            for(int j=1;j<currListDilema.size();j++)
            {
                if(currListDilema.get(j).getDilemaPriority() > max){
                    objD = currListDilema.get(j);
                    id = currListId.get(j);
                    max = objD.getDilemaPriority();
                    k=j;
                    Log.d(TAG, "prioritySort: k:"+k+", j:"+j);
                }
            }
            Log.d(TAG, "prioritySort: prioritymax: "+max);
            Log.d(TAG, "prioritySort: objDesc: "+objD.getDilemaDescription());

            prioritySortedDilema.add(objD);
            prioritySortedId.add(id);
            currListDilema.remove(k);
            currListId.remove(k);
            Log.d(TAG, "prioritySort: currListDil size: "+currListDilema.size());
        }
        Log.d(TAG, "prioritySort: prioritySortedDilema: "+ prioritySortedDilema.size());
        Log.d(TAG, "prioritySort: prioritySortedId: " + prioritySortedId.size());
        addToAdapter(prioritySortedDilema, prioritySortedId);
    }
    private void addToAdapter(List<Dilema> finalDilemaList, List<String> finalIdList){
        if(finalDilemaList.size() != 0){
            adapterViewPager = new MyPagerAdapter(getChildFragmentManager(), finalDilemaList, finalIdList);
            Log.d(TAG, "onCreateView: Adapter " + adapterViewPager);
            adapterViewPager.notifyDataSetChanged();
            vpPager.setAdapter(adapterViewPager);
            Log.d(TAG, "onCreateView: after setting adapter");
            vpPager.setPageTransformer(true, new RotateUpTransformer());
        }

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
