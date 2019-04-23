package com.dright;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DilemaTab extends Fragment {
    private static final String TAG = "DilemaTab";
    private TextView txtCategory;
    private View view;
    private List<Dilema> listDilema ;
    private FragmentPagerAdapter adapterViewPager;
    private Spinner spinner;
    private DatabaseReference mDatabase;
    private static boolean check = false;
    private String[] itemsCategory = {"All", "Food","Sport", "Clothes"};
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.dilema_tab, container, false);
        spinner = view.findViewById(R.id.spinner);

        spinner.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, itemsCategory));
        mDatabase = FirebaseDatabase.getInstance().getReference("Dilema/");
        //readFromDatabase();
        readFromDb();

        ViewPager vpPager = (ViewPager) view.findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getChildFragmentManager(),listDilema);
        vpPager.setAdapter(adapterViewPager);
        vpPager.setPageTransformer(true, new RotateUpTransformer());
        return view;
    }


    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private List<Dilema> mListDilema;

        public MyPagerAdapter(FragmentManager fragmentManager, List<Dilema> mListDilema) {
            super(fragmentManager);
            this.mListDilema = mListDilema;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return mListDilema.size();
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {


            if(check) {
                check = false;
                return FragmentDilemaOptions.newInstance(mListDilema.get(position),false);
            }else{
                return FragmentDilemaOptions.newInstance(mListDilema.get(position),true);
            }

        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

    }


    public void readFromDatabase(){
        try{
            listDilema = new ArrayList<>();

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        if(ds.hasChild("dilemaText")) {
                            if(ds.child("dilemaText").getValue(Boolean.class)) {
                                check = true;
                            }
                            else{
                                check = false;
                            }
                        }
                        if(ds.hasChild("dilemaCategory")){
                            if(spinner.getSelectedItem().toString().equalsIgnoreCase(ds.child("dilemaCategory").getValue(String.class))){
                                Dilema objD = (Dilema) ds.getValue();
                                listDilema.add(objD);
                            }
                            else  if(spinner.getSelectedItem().toString().equalsIgnoreCase("All")){
                                Dilema objD = (Dilema) ds.getValue();
                                listDilema.add(objD);
                            }
                        }
                    }

                }



                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private  void readFromDb(){
        try{
            listDilema = new ArrayList<>();

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        /*if(ds.hasChild("dilemaText")) {
                            if(ds.child("dilemaText").getValue(Boolean.class)) {
                                check = true;
                            }
                            else{
                                check = false;
                            }
                        }*/
                        Log.d(TAG, "onDataChange: Inside readFromDB");
                        Dilema objD =  ds.getValue(Dilema.class);
                        Log.d(TAG, "onDataChange: description " + objD.getDilemaDescription());
                        Log.d(TAG, "onDataChange: optionsSize " + objD.getDilemaOptions().size());
                        Log.d(TAG, "onDataChange: optionsResult "+ objD.getOptionsResults().size());
                        Log.d(TAG, "onDataChange: dilemaText " + objD.isDilemaText());
                        Log.d(TAG, "onDataChange: dilemaPriority " + objD.getDilemaPriority());
                        Log.d(TAG, "onDataChange: dilemaOption "+objD.getDilemaOptions().get(0));


                        listDilema.add(objD);
                    }

                }



                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }

    }
}