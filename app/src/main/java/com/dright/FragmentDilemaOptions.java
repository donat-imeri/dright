package com.dright;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;


public class FragmentDilemaOptions extends Fragment  implements Serializable{
    private TextView txtPostedBy;
    private EditText txtComment;
    private TextView txtTitle;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private DatabaseReference mDatabase;
    private static String rdbText = null;
    private String dilemaId;
    private boolean check = false;
    private List<String> allVoters;
    private static int counter = 0;
    private Dilema objDilema;
    private List<String> objDilemaOptions;
    private List<Integer> objDilemaOptionsResults;
    private String dilemaAsker;
    LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    public static FragmentDilemaOptions newInstance(Dilema objDilema, String dilemaId) {
        FragmentDilemaOptions fragment = new FragmentDilemaOptions();
        Bundle args = new Bundle();
        args.putSerializable("objectDilema", objDilema);
        args.putString("dilemaId", dilemaId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        objDilema = (Dilema) getArguments().getSerializable("objectDilema");
        dilemaId = getArguments().getString("dilemaId");
    }


    View ProfileView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileView = inflater.inflate(R.layout.fragment_dilema_options,container,false);
        linearLayout = ProfileView.findViewById(R.id.insideLinear);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);


        relativeLayout = ProfileView.findViewById(R.id.relativeLayout);
        txtPostedBy = ProfileView.findViewById(R.id.txtPostedBy);
        txtComment = ProfileView.findViewById(R.id.txtComment);
        txtTitle = ProfileView.findViewById(R.id.txtTitle);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("DilemaVoters");
        DatabaseReference dbRef1 = dbRef.child(dilemaId);
        allVoters = new ArrayList<>();
        dbRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    allVoters.add(ds.getKey());
                }
                int counter = 0;
                for(int i=0; i<allVoters.size();i++){
                    if (DilemaTab.currUser.equals(allVoters.get(i))){
                        counter++;
                    }
                }
                if(counter == 0){
                    addStuff();
                }
                else{
                    showAnswers();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });











        Log.d(TAG, "onCreateView: Is calling return ProfileView");
        return ProfileView;

    }
    private void addStuff(){
        txtTitle.setText(objDilema.getDilemaDescription());
        dilemaAsker = objDilema.getDilemaAsker();
        Log.d(TAG, "onCreateView: Dilema Asker: "+dilemaAsker);
        objDilemaOptions = objDilema.getDilemaOptions();
        Log.d(TAG, "onCreateView: dilema options size: "+objDilemaOptions.size());
        objDilemaOptionsResults = objDilema.getOptionsResults();
        //txtPostedBy.setText(objDilema.getDilemaAsker());
        //addRadioButtons(objDilemaOptions.size(),radioGroup);
        addTextViews(objDilema);
        Log.d(TAG, "onCreateView: Dilema Asker from txt: "+txtPostedBy.getText().toString());
        Log.d(TAG, "onCreateView: Dilema Asker is Stay Anonymous: "+ objDilema.isStayAnonymous());
        if(!objDilema.isStayAnonymous())
        {
            dilemaAsker = objDilema.getDilemaAsker();
            mDatabase = FirebaseDatabase.getInstance().getReference("Users");
            DatabaseReference mDatabase1 = mDatabase.child(dilemaAsker);
            DatabaseReference mDatabase2 = mDatabase1.child("name");
            Log.d(TAG, "onCreateView: onDataChange mDatabase users/ "+mDatabase);
            mDatabase2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    /*Log.d(TAG, "onDataChange: Inside users/dilemaAsker/");
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        Log.d(TAG, "onDataChange: dilemaAsker: "+dilemaAsker);
                        Log.d(TAG, "onDataChange: hasCHild dilemaAsker: "+ds.hasChild(""+dilemaAsker));
                        if(ds.hasChild(dilemaAsker)){
                            txtPostedBy.setText(ds.child(dilemaAsker).child("name").getValue(String.class));
                            Log.d(TAG, "onDataChange: txtPostedBy: "+txtPostedBy.getText().toString());
                        }
                    }*/
                    txtPostedBy.setText(dataSnapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else {
            txtPostedBy.setText("Anonymous");
        }

    }
    private void showAnswers(){
        Toast.makeText(getActivity(),"There should display the results",Toast.LENGTH_LONG).show();

    }

    private void updateOptionsResult(String rdbText){


        for(int i=0; i <objDilemaOptions.size(); i++)
        {
            if(rdbText.equalsIgnoreCase(objDilemaOptions.get(i)))
            {
                int resOption = objDilemaOptionsResults.get(i);
                resOption += 1;
                objDilemaOptionsResults.set(i,resOption);
                objDilema.setOptionsResults(objDilemaOptionsResults);
            }
        }

        Log.d(TAG, "updateOptionsResult: Results updated!");
        updateDb(objDilemaOptionsResults);

    }

    private void addRadioButtons(int size,RadioGroup rdGroup){
       /* for (int row = 0; row < 1; row++) {
            RadioGroup ll = new RadioGroup(getActivity());
            ll.setOrientation(LinearLayout.HORIZONTAL);
*/
        for (int i = 0; i < size; i++) {
            RadioButton rdbtn = new RadioButton(getActivity());
            rdbtn.setId(View.generateViewId());

            rdbtn.setText(objDilemaOptions.get(i));
            Log.d(TAG, "addRadioButtons: text: "+rdbtn.getText());
            rdGroup.addView(rdbtn);
        }
        //((ViewGroup) ProfileView.findViewById(R.id.radiogroup)).addView(ll);
        //}

        /*for(int i=0;i < size;i++){
            TextView txtView = new TextView(getActivity());
            txtView.setId(i+1);
            txtView.setPadding(30,30,30,30);
            txtView.setClickable(true);
            txtView.setText(objDilemaOptions.get(i));
            txtView.setTextSize(getResources().getDimension(R.dimen.textsize));
        }
*/
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

    private void addTextViews(Dilema objDil){
            for(int i=0; i<objDil.getDilemaOptions().size();i++){
                final TextView tv = new TextView(getContext());
                tv.setLayoutParams(lparams);
                tv.setText(objDil.getDilemaOptions().get(i));
                tv.setId(i);
                tv.setPadding(20,20,20,20);
                tv.setTextSize(18);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        updateOptionsResult(tv.getText().toString());
                        relativeLayout.removeAllViews();
                        showAnswers();
                    }
                });
                counter++;
                this.linearLayout.addView(tv);
            }
        }

        private void updateDb(List<Integer> optionsResults){

            mDatabase = FirebaseDatabase.getInstance().getReference("Dilema");
            DatabaseReference mDatabase1 = mDatabase.child(dilemaId);
            mDatabase1.child("optionsResults").setValue(optionsResults);
            if(!txtComment.getText().equals("")){
                mDatabase1.child("comment").setValue(txtComment.getText().toString());
            }
            DatabaseReference db1 = FirebaseDatabase.getInstance().getReference("DilemaVoters");
            DatabaseReference db2 = db1.child(dilemaId);
            db2.child(DilemaTab.currUser).setValue("Voted");

        }

}