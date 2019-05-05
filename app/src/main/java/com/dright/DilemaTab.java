package com.dright;

import android.app.Activity;
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
    private ViewPager vpPager;
    private DatabaseReference mDatabase;
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
        spinner = activity.findViewById(R.id.spinner);

        spinner.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, itemsCategory));
        readFromDatabase();
        //readFromDb();



        Log.d(TAG, "onCreateView: after viewpager vpPager = " + vpPager);

        Log.d(TAG, "onCreateView: after pagetransformer");

    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private List<Dilema> mListDilema;

        public MyPagerAdapter(FragmentManager fragmentManager, List<Dilema> mListDilema) {
            super(fragmentManager);
            this.mListDilema = mListDilema;
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
            if(check) {
                check = false;
                return FragmentDilemaOptions.newInstance(mListDilema.get(position),false);
            }else{
                return FragmentDilemaOptions.newInstance(mListDilema.get(position),true);
            }
           // return FragmentDilemaOptions.newInstance(mListDilema.get(position));

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

            Log.d(TAG, "readFromDatabase: inside ");

            mDatabase = FirebaseDatabase.getInstance().getReference("Dilema/");
            Log.d(TAG, "readFromDatabase: " + mDatabase.getKey());
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        Log.d(TAG, "onDataChange: inside for loop" + ds.getValue(Dilema.class).getDilemaDescription());
                        if(ds.hasChild("dilemaText")) {
                            Log.d(TAG, "onDataChange: inside hasChild(dilemaText)");
                            Log.d(TAG, "onDataChange: inside has child dilemaText = "+ ds.child("dilemaText").getValue(Boolean.class));
                            if(ds.child("dilemaText").getValue(Boolean.class).toString().equalsIgnoreCase("true")) {
                                check = true;
                                Log.d(TAG, "onDataChange: inside has Child(dilemaText: "+check);
                            }
                            else{
                                check = false;
                            }
                        }
                        if(ds.hasChild("dilemaCategory")){
                            if(spinner.getSelectedItem().toString().equalsIgnoreCase(ds.child("dilemaCategory").getValue(String.class))){
                                Dilema objD =  ds.getValue(Dilema.class);
                                listDilema.add(objD);
                            }
                            else  if(spinner.getSelectedItem().toString().equalsIgnoreCase("All")){
                                Dilema objD = ds.getValue(Dilema.class);
                                listDilema.add(objD);
                            }
                        }
                        else{

                            Dilema objD = ds.getValue(Dilema.class);
                            listDilema.add(objD);
                            Log.d(TAG, "onDataChange: objD " + listDilema.size() );

                        }
                    }
                    adapterViewPager = new MyPagerAdapter(getChildFragmentManager(),listDilema);
                    Log.d(TAG, "onCreateView: Adapter " + adapterViewPager);
                    vpPager.setAdapter(adapterViewPager);
                    Log.d(TAG, "onCreateView: after setting adapter");
                    vpPager.setPageTransformer(true, new RotateUpTransformer());

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
/*private void addTextViews(List<Dilema> objList){
        for(int i=0;i<objList.size();i++){
            for(int j=0; j<objList.get(i).getDilemaOptions().size();j++){
                TextView tv = new TextView(getApplicationContext());
                tv.setLayoutParams(lparams);
                tv.setText(objList.get(i).getDilemaOptions().get(j));
                tv.setId(counter);
                counter++;
                this.relLay.addView(tv);
            }
        }
    }*/