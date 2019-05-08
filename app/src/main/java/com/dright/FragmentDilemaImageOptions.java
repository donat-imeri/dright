package com.dright;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;


public class FragmentDilemaImageOptions extends Fragment  implements Serializable{
    private TextView txtPostedBy;
    private EditText txtComment;
    private TextView txtTitle;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private String dilemaId;
    private DatabaseReference mDatabase;
    private static String rdbText = null;
    private boolean check = false;
    private static int counter = 0;
    private List<String> allVoters;
    private Dilema objDilema;
    private List<String> objDilemaOptions;
    private List<Integer> objDilemaOptionsResults;
    private String dilemaAsker;
    LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    public static FragmentDilemaImageOptions newInstance(Dilema objDilema, String dilemaId) {
        FragmentDilemaImageOptions fragment = new FragmentDilemaImageOptions();
        Bundle args = new Bundle();
        args.putSerializable("objectDilema", objDilema);
        args.putString("dilemaId",dilemaId);
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileView = inflater.inflate(R.layout.fragment_image_dilema_options,container,false);
        linearLayout = ProfileView.findViewById(R.id.insideLinear);
        objDilemaOptions = objDilema.getDilemaOptions();
        objDilemaOptionsResults = objDilema.getOptionsResults();
        relativeLayout = ProfileView.findViewById(R.id.relLayout);
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









        /*btnVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: rdbText= "+rdbText);
                if(check ){
                    if(rdbText != null){
                        updateOptionsResult(rdbText);
                        Toast.makeText(getActivity(), "opResult happened op1 res= "+objDilemaOptionsResults.get(0), Toast.LENGTH_SHORT).show();
                    }

                }

                Toast.makeText(getActivity(),"Click happened rdbText "+rdbText,Toast.LENGTH_LONG).show();

                Toast.makeText(getActivity(),"Click happened",Toast.LENGTH_LONG).show();
            }
        });*/
        Log.d(TAG, "onCreateView: Is calling return ProfileView");
        return ProfileView;

    }
    private void addStuff(){
        txtTitle.setText(objDilema.getDilemaDescription());
        dilemaAsker = objDilema.getDilemaAsker();
        Log.d(TAG, "onCreateView: Dilema Asker: "+dilemaAsker);
        //txtPostedBy.setText(objDilema.getDilemaAsker());
        addTextViews(objDilema);
        Log.d(TAG, "onCreateView: Dilema Asker from txt: "+txtPostedBy.getText().toString());
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

    private void updateOptionsResult(int id){

                int resOption = objDilemaOptionsResults.get(id);
                resOption += 1;
                objDilemaOptionsResults.set(id,resOption);
                objDilema.setOptionsResults(objDilemaOptionsResults);
                updateDb(objDilemaOptionsResults);
        Log.d(TAG, "updateOptionsResult: results Updated!");

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
                final ImageView tv = new ImageView(getContext());
                tv.setLayoutParams(lparams);
                Glide.with(getActivity())
                        .load(objDil.getDilemaOptions().get(i))
                        .override(400,300)
                        .into(tv);
                tv.setId(i);
                tv.setPadding(20,20,20,20);
                counter++;
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        updateOptionsResult(tv.getId());
                        Toast.makeText(getActivity(),"Click in imageVIew with id: "+tv.getId()+", just happened",Toast.LENGTH_LONG).show();
                        relativeLayout.removeAllViews();
                        showAnswers();
                    }

                });

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