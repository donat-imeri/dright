package com.dright;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.List;


public class FragmentWithTextOptions extends Fragment implements Serializable {
    private TextView txtPostedBy;
    private TextView txt_option_1;
    private TextView txt_option_2;
    private EditText txtComment;
    private RelativeLayout relativeLayout;
    private DatabaseReference mDatabase;
    private boolean check = false;
    private Dilema objDilema;
    private List<String> objDilemaOptions;

    public static FragmentWithTextOptions newInstance(Dilema objDilema) {
        FragmentWithTextOptions fragment = new FragmentWithTextOptions();
        Bundle args = new Bundle();
        args.putSerializable("objectDilema", (Serializable) objDilema);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        objDilema = (Dilema) getArguments().getSerializable("objectDilema");

    }


    View ProfileView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileView = inflater.inflate(R.layout.fragment_two_options,container,false);
        relativeLayout = ProfileView.findViewById(R.id.insideRelative);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        objDilemaOptions = objDilema.getDilemaOptions();
        for(int i=0;i < objDilemaOptions.size();i++){
            TextView txtView = new TextView(getActivity());
            txtView.setId(i+1);
            txtView.setPadding(30,30,30,30);
            txtView.setClickable(true);
            txtView.setText(objDilemaOptions.get(i));
            txtView.setTextSize(getResources().getDimension(R.dimen.textsize));


        }
        txtPostedBy = ProfileView.findViewById(R.id.txtPostedBy);
        txt_option_1 = ProfileView.findViewById(R.id.txt_option_1);
        txt_option_2 = ProfileView.findViewById(R.id.txt_option_2);
        txtComment = ProfileView.findViewById(R.id.txtComment);

        mDatabase = FirebaseDatabase.getInstance().getReference("dilema");
        return ProfileView;

    }


/*
    public void readFromDatabase(){
        try{
            objDilema = new ArrayList<>();

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        if(ds.hasChild("numOptions")) {
                            int numOptions = ds.child("numOptions").getValue(int.class);
                            if(numOptions == 2){
                                check = true;
                            }
                            else{
                                check = false;
                            }


                        }
                        if(check){
                            Dilema objD = (Dilema) ds.getValue();
                            objDilema.add(objD);
                            check = false;
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
    */
}
