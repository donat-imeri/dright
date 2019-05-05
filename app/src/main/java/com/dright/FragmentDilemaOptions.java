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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.List;

import static android.support.constraint.Constraints.TAG;


public class FragmentDilemaOptions extends Fragment  implements Serializable{
    private TextView txtPostedBy;
    private RadioGroup radioGroup;
    private EditText txtComment;
    private TextView txtTitle;
    private LinearLayout linearLayout;
    private Button btnVote;
    private DatabaseReference mDatabase;
    private boolean checkImage = true;
    private static String rdbText = null;
    private boolean check = false;
    private static int counter = 0;
    private Dilema objDilema;
    private List<String> objDilemaOptions;
    private List<Integer> objDilemaOptionsResults;
    private String dilemaAsker;
    LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    public static FragmentDilemaOptions newInstance(Dilema objDilema, boolean checkImage) {
        FragmentDilemaOptions fragment = new FragmentDilemaOptions();
        Bundle args = new Bundle();
        args.putSerializable("objectDilema", objDilema);
        args.putBoolean("checkImage",checkImage);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        objDilema = (Dilema) getArguments().getSerializable("objectDilema");
        checkImage = getArguments().getBoolean("checkImage");

    }


    View ProfileView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileView = inflater.inflate(R.layout.fragment_dilema_options,container,false);
        linearLayout = ProfileView.findViewById(R.id.insideLinear);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        radioGroup = ProfileView.findViewById(R.id.radiogroup);
        btnVote = ProfileView.findViewById(R.id.btnVote);
        objDilemaOptions = objDilema.getDilemaOptions();
        objDilemaOptionsResults = objDilema.getOptionsResults();

        txtPostedBy = ProfileView.findViewById(R.id.txtPostedBy);
        txtComment = ProfileView.findViewById(R.id.txtComment);
        txtTitle = ProfileView.findViewById(R.id.txtTitle);

        txtTitle.setText(objDilema.getDilemaDescription());
        dilemaAsker = objDilema.getDilemaAsker();
        Log.d(TAG, "onCreateView: Dilema Asker: "+dilemaAsker);
        //txtPostedBy.setText(objDilema.getDilemaAsker());
        addRadioButtons(objDilemaOptions.size(),radioGroup);
        //addTextViews(objDilema);
        Log.d(TAG, "onCreateView: Dilema Asker from txt: "+txtPostedBy.getText().toString());
        if(!objDilema.isStayAnonymous())
        {
            dilemaAsker = objDilema.getDilemaAsker();
            txtPostedBy.setText(dilemaAsker);
        }
        else {
            txtPostedBy.setText("Anonymous");
        }




        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for(int i=0; i < group.getChildCount(); i++){
                    RadioButton rdBtn = (RadioButton) group.getChildAt(i);
                    if(rdBtn.getId() == checkedId){
                        check = true;
                        Log.d(TAG, "onCheckedChanged: checkImage: "+checkImage);
                        if(!checkImage) {
                            rdbText = rdBtn.getText().toString();
                            Log.d(TAG, "onCheckedChanged: rdbText= "+rdbText);
                        }
                        else {
                            //qitu duhet me marr pathin e ikones se radio butonit te selektum!!
                        }
                    }
                }
            }
        });


        btnVote.setOnClickListener(new View.OnClickListener() {
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
        });
        Log.d(TAG, "onCreateView: Is calling return ProfileView");
        return ProfileView;

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
                updateDb(objDilemaOptionsResults);
            }
        }

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
                TextView tv = new TextView(getContext());
                tv.setLayoutParams(lparams);
                tv.setText(objDil.getDilemaOptions().get(i));
                tv.setId(i);
                tv.setPadding(20,8,8,8);
                tv.setTextSize(14);
                counter++;
                this.linearLayout.addView(tv);
            }
        }

        private void updateDb(List<Integer> optionsResults){
            mDatabase = FirebaseDatabase.getInstance().getReference("Dilema/");

        }

}