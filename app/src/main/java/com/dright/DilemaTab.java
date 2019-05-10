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
    public static String currUser = "";
    private List<String> listDilemaId = new ArrayList<>();
    private List<String> listUserFollowing;
    private List<String> listDilemaVoters = new ArrayList<>();
    private List<Integer> listDilemaPriority = new ArrayList<>();
    private SwipeRefreshLayout mSwipeLayout ;

    private List<String> mListDilemaVoters;

    public static FragmentStatePagerAdapter adapterViewPager;
    private ViewPager vpPager;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private static boolean check = false;
    private String[] itemsCategory = {"All", "Food","Sport", "Clothes"};
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

               new Handler().postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       mSwipeLayout.setRefreshing(false);
                   }
               },500);
            }
        });




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
                return FragmentDilemaOptions.newInstance(mListDilema.get(position),mListDilemaId.get(position),mListDilema,mListDilemaId,mListDilemaCheck);
            }else{
                return FragmentDilemaImageOptions.newInstance(mListDilema.get(position),mListDilemaId.get(position),mListDilema,mListDilemaId,mListDilemaCheck);
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
                hasVoted(listUserFollowing);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void hasVoted(final List<String> userFollowing){
        final List<String> dilemaVoted = new ArrayList<>();
        DatabaseReference mDb1 = FirebaseDatabase.getInstance().getReference("DilemaVoters");
        DatabaseReference mDb2 = mDb1.child(DilemaTab.currUser);
        mDb2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    dilemaVoted.add(ds.getKey());
                }

                readFromDb(dilemaVoted,userFollowing);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*private void hasVoted(final List<String> userFollowing){
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

                readFromDb(voters,userFollowing);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/
    private void readFromDb(final List<String> votedDilema, final List<String> userFollowing){


        Log.d(TAG, "readFromDatabase: inside ");

        mDatabase = FirebaseDatabase.getInstance().getReference("Dilema/");
        Log.d(TAG, "readFromDatabase: " + mDatabase.getKey());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listDilema = new ArrayList<>();
                listDilemaCheck = new ArrayList<>();
                listDilemaId = new ArrayList<>();
                listDilemaPriority = new ArrayList<>();
                List<Dilema> filteredDilema = new ArrayList<>();
                List<Boolean> filteredCheckDilema = new ArrayList<>();
                List<String> filteredId = new ArrayList<>();
                List<Integer> filteredPriority = new ArrayList<>();
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
                                listDilemaPriority.add(ds.child("dilemaPriority").getValue(Integer.class));
                                Dilema objD = ds.getValue(Dilema.class);

                                listDilema.add(objD);
                                listDilemaCheck.add(check);
                            }







                        }
                    }


                }
                Collections.reverse(listDilema);
                Collections.reverse(listDilemaId);
                Collections.reverse(listDilemaCheck);
                Collections.reverse(listDilemaPriority);

                Log.d(TAG, "onDataChange: listDilema size: "+listDilema.size());
                int i=0;
                while (listDilema.size() != 0){
                    int max = listDilemaPriority.get(i);
                    String dilemaId = listDilemaId.get(i);
                    Dilema objDilema = listDilema.get(i);
                    boolean dilemaCheck = listDilemaCheck.get(i);
                    int k = 0;

                    for(int j=0; j<listDilema.size();j++)
                    {
                        if(listDilemaPriority.get(j)>max){
                            max = listDilemaPriority.get(j);
                            dilemaId = listDilemaId.get(j);
                            dilemaCheck = listDilemaCheck.get(j);
                            objDilema = listDilema.get(j);
                            Log.d(TAG, "onDataChange: listDilemacheck(j): "+dilemaCheck);
                            k=j;
                            Log.d(TAG, "onDataChange: k = "+k+ "\t j="+j);
                            Log.d(TAG, "onDataChange: listDilemaCheck(k): "+listDilemaCheck.get(k));
                        }

                    }

                    listDilemaPriority.remove(k);
                    listDilema.remove(objDilema);
                    listDilemaId.remove(dilemaId);
                    listDilemaCheck.remove(k);

                    filteredCheckDilema.add(dilemaCheck);
                    filteredDilema.add(objDilema);
                    filteredId.add(dilemaId);
                    filteredPriority.add(max);

                }


                if(filteredDilema.size() != 0 && filteredCheckDilema.size() != 0 && filteredId.size() != 0) {
                    adapterViewPager = new MyPagerAdapter(getChildFragmentManager(), filteredDilema, filteredCheckDilema, filteredId);
                    Log.d(TAG, "onCreateView: Adapter " + adapterViewPager);
                    adapterViewPager.notifyDataSetChanged();
                    vpPager.setAdapter(adapterViewPager);
                    Log.d(TAG, "onCreateView: after setting adapter");
                    vpPager.setPageTransformer(true, new RotateUpTransformer());
                }




            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void getNextItemsPriority5(String currIdDilema, final List<Dilema> currDilemaList, final List<Boolean> currDilemaCheck, final List<String> currDilemaIdList){
        final List<String> newIdDilemaList = new ArrayList<>();

        DatabaseReference dbPriority = FirebaseDatabase.getInstance().getReference("DilemaPriorities");
        DatabaseReference dbPriority1 = dbPriority.child("5");
        dbPriority1.startAt(currIdDilema).limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int numOfItemsLeft = 0;
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    int counter = 0;
                    for(int i=0;i<currDilemaIdList.size();i++){
                        if(ds.getKey().equals(currDilemaIdList.get(i))){
                            counter++;
                        }
                    }
                    if(counter==0){
                        newIdDilemaList.add(ds.getKey());
                    }
                    numOfItemsLeft++;

                }
                if(numOfItemsLeft> 0 && numOfItemsLeft < 10){
                    getNextItemsPriority4(newIdDilemaList.get(newIdDilemaList.size()-1), currDilemaList, currDilemaCheck, currDilemaIdList,newIdDilemaList);
                }
                else if(numOfItemsLeft == 10){
                    updateList(currDilemaList, currDilemaCheck, currDilemaIdList,newIdDilemaList);
                }
                else if(numOfItemsLeft == 0){
                    getNextItemsPriority4(newIdDilemaList.get(newIdDilemaList.size()-1), currDilemaList, currDilemaCheck, currDilemaIdList,newIdDilemaList);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    public void getNextItemsPriority4(String currIdDilema, final List<Dilema> currDilemaList, final List<Boolean> currDilemaCheck, final List<String> currDilemaIdList, final List<String> continuedDilemaIdList){

        int limitation = 0;
        if(continuedDilemaIdList.size() == 0){
            limitation = 10;
        }
        else if(continuedDilemaIdList.size()>0 && continuedDilemaIdList.size()<10){
            limitation = 10 - continuedDilemaIdList.size();
        }

        DatabaseReference dbPriority = FirebaseDatabase.getInstance().getReference("DilemaPriorities");
        DatabaseReference dbPriority1 = dbPriority.child("4");
        dbPriority1.startAt(currIdDilema).limitToFirst(limitation).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int numOfItemsLeft = 0;
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    int counter = 0;
                    for(int i=0;i<currDilemaIdList.size();i++){
                        if(ds.getKey().equals(currDilemaIdList.get(i))){
                            counter++;
                        }
                    }
                    if(counter==0){
                        continuedDilemaIdList.add(ds.getKey());
                    }
                    numOfItemsLeft++;

                }
                if(numOfItemsLeft> 0 && numOfItemsLeft < 10){
                    getNextItemsPriority3(continuedDilemaIdList.get(continuedDilemaIdList.size()-1), currDilemaList, currDilemaCheck, currDilemaIdList, continuedDilemaIdList);
                }
                else if(numOfItemsLeft == 10){
                    updateList(currDilemaList, currDilemaCheck, currDilemaIdList,continuedDilemaIdList);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getNextItemsPriority3(String currIdDilema, final List<Dilema> currDilemaList, final List<Boolean> currDilemaCheck, final List<String> currDilemaIdList, final List<String> continuedDilemaIdList){
        int limitation = 0;
        if(continuedDilemaIdList.size() == 0){
            limitation = 10;
        }
        else if(continuedDilemaIdList.size()>0 && continuedDilemaIdList.size()<10){
            limitation = 10 - continuedDilemaIdList.size();
        }

        DatabaseReference dbPriority = FirebaseDatabase.getInstance().getReference("DilemaPriorities");
        DatabaseReference dbPriority1 = dbPriority.child("3");
        dbPriority1.startAt(currIdDilema).limitToFirst(limitation).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int numOfItemsLeft = 0;
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    int counter = 0;
                    for(int i=0;i<currDilemaIdList.size();i++){
                        if(ds.getKey().equals(currDilemaIdList.get(i))){
                            counter++;
                        }
                    }
                    if(counter==0){
                        continuedDilemaIdList.add(ds.getKey());
                    }
                    numOfItemsLeft++;

                }
                if(numOfItemsLeft> 0 && numOfItemsLeft < 10){
                    getNextItemsPriority2(continuedDilemaIdList.get(continuedDilemaIdList.size()-1), currDilemaList, currDilemaCheck, currDilemaIdList, continuedDilemaIdList);
                }
                else if(numOfItemsLeft == 10){
                    updateList(currDilemaList, currDilemaCheck, currDilemaIdList,continuedDilemaIdList);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getNextItemsPriority2(String currIdDilema, final List<Dilema> currDilemaList, final List<Boolean> currDilemaCheck, final List<String> currDilemaIdList, final List<String> continuedDilemaIdList){
        int limitation = 0;
        if(continuedDilemaIdList.size() == 0){
            limitation = 10;
        }
        else if(continuedDilemaIdList.size()>0 && continuedDilemaIdList.size()<10){
            limitation = 10 - continuedDilemaIdList.size();
        }

        DatabaseReference dbPriority = FirebaseDatabase.getInstance().getReference("DilemaPriorities");
        DatabaseReference dbPriority1 = dbPriority.child("2");
        dbPriority1.startAt(currIdDilema).limitToFirst(limitation).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int numOfItemsLeft = 0;
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    int counter = 0;
                    for(int i=0;i<currDilemaIdList.size();i++){
                        if(ds.getKey().equals(currDilemaIdList.get(i))){
                            counter++;
                        }
                    }
                    if(counter==0){
                        continuedDilemaIdList.add(ds.getKey());
                    }
                    numOfItemsLeft++;

                }
                if(numOfItemsLeft> 0 && numOfItemsLeft < 10){
                    getNextItemsPriority1(continuedDilemaIdList.get(continuedDilemaIdList.size()-1), currDilemaList, currDilemaCheck, currDilemaIdList, continuedDilemaIdList);
                }
                else if(numOfItemsLeft == 10){
                    updateList(currDilemaList, currDilemaCheck, currDilemaIdList,continuedDilemaIdList);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void getNextItemsPriority1(String currIdDilema, final List<Dilema> currDilemaList, final List<Boolean> currDilemaCheck, final List<String> currDilemaIdList, final List<String> continuedDilemaIdList){
        int limitation = 0;
        if(continuedDilemaIdList.size() == 0){
            limitation = 10;
        }
        else if(continuedDilemaIdList.size()>0 && continuedDilemaIdList.size()<10){
            limitation = 10 - continuedDilemaIdList.size();
        }

        DatabaseReference dbPriority = FirebaseDatabase.getInstance().getReference("DilemaPriorities");
        DatabaseReference dbPriority1 = dbPriority.child("1");
        dbPriority1.startAt(currIdDilema).limitToFirst(limitation).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int numOfItemsLeft = 0;
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    int counter = 0;
                    for(int i=0;i<currDilemaIdList.size();i++){
                        if(ds.getKey().equals(currDilemaIdList.get(i))){
                            counter++;
                        }
                    }
                    if(counter==0){
                        continuedDilemaIdList.add(ds.getKey());
                    }
                    numOfItemsLeft++;

                }
                if(numOfItemsLeft> 0 && numOfItemsLeft < 10){
                    getNextItemsPriority0(continuedDilemaIdList.get(continuedDilemaIdList.size()-1), currDilemaList, currDilemaCheck, currDilemaIdList, continuedDilemaIdList);
                }
                else if(numOfItemsLeft == 10){
                    updateList(currDilemaList, currDilemaCheck, currDilemaIdList,continuedDilemaIdList);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void getNextItemsPriority0(String currIdDilema, final List<Dilema> currDilemaList, final List<Boolean> currDilemaCheck, final List<String> currDilemaIdList, final List<String> continuedDilemaIdList){
        int limitation = 0;
        if(continuedDilemaIdList.size() == 0){
            limitation = 10;
        }
        else if(continuedDilemaIdList.size()>0 && continuedDilemaIdList.size()<10){
            limitation = 10 - continuedDilemaIdList.size();
        }

        DatabaseReference dbPriority = FirebaseDatabase.getInstance().getReference("DilemaPriorities");
        DatabaseReference dbPriority1 = dbPriority.child("0");
        dbPriority1.startAt(currIdDilema).limitToFirst(limitation).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int numOfItemsLeft = 0;
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    int counter = 0;
                    for(int i=0;i<currDilemaIdList.size();i++){
                        if(ds.getKey().equals(currDilemaIdList.get(i))){
                            counter++;
                        }
                    }
                    if(counter==0){
                        continuedDilemaIdList.add(ds.getKey());
                    }
                    numOfItemsLeft++;

                }
                if(numOfItemsLeft> 0){

                    updateList(currDilemaList, currDilemaCheck, currDilemaIdList,continuedDilemaIdList);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateList(final List<Dilema> currDilemaList, final List<Boolean> currDilemaCheck, final List<String> currDilemaIdList, final List<String> continuedDilemaIdList){

    }

}
