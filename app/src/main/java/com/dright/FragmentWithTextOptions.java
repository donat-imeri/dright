package com.dright;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.List;


public class FragmentWithTextOptions extends Fragment implements Serializable {
    private TextView txtPostedBy;
    private RadioGroup radioGroup;
    private EditText txtComment;
    private TextView txtTitle;
    private RelativeLayout relativeLayout;
    private Button btnVote;
    private DatabaseReference mDatabase;
    private boolean checkImage = true;
    private String rdbText = null;
    private boolean check = false;
    private Dilema objDilema;
    private List<String> objDilemaOptions;
    private String dilemaAsker;

    public static FragmentWithTextOptions newInstance(Dilema objDilema, boolean checkImage) {
        FragmentWithTextOptions fragment = new FragmentWithTextOptions();
        Bundle args = new Bundle();
        args.putSerializable("objectDilema", (Serializable) objDilema);
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
        ProfileView = inflater.inflate(R.layout.fragment_text_options,container,false);
        relativeLayout = ProfileView.findViewById(R.id.insideRelative);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        radioGroup = ProfileView.findViewById(R.id.radiogroup);
        btnVote = ProfileView.findViewById(R.id.btnVote);
        objDilemaOptions = objDilema.getDilemaOptions();



        txtPostedBy = ProfileView.findViewById(R.id.txtPostedBy);
        txtComment = ProfileView.findViewById(R.id.txtComment);
        txtTitle = ProfileView.findViewById(R.id.txtTitle);
        txtTitle.setText(objDilema.getDilemaDescription());
        if(!objDilema.isStayAnonymous())
        {
           dilemaAsker = objDilema.getDilemaAsker();
            txtPostedBy.setText(dilemaAsker);
        }
        else {
            txtPostedBy.setText("Anonymous");
        }


        //mDatabase = FirebaseDatabase.getInstance().getReference("dilema");
        addRadioButtons(objDilemaOptions.size());

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for(int i=0; i < group.getChildCount(); i++){
                    RadioButton rdBtn = (RadioButton) group.getChildAt(i);
                    if(rdBtn.getId() == checkedId){
                        check = true;
                        if(!checkImage) {
                            rdbText = rdBtn.getText().toString();
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
                if(check ){

                }

            }
        });
        return ProfileView;

    }

    private void addRadioButtons(int size){
        for (int row = 0; row < 1; row++) {
            RadioGroup ll = new RadioGroup(getActivity());
            ll.setOrientation(LinearLayout.HORIZONTAL);

            for (int i = 1; i <= size; i++) {
                RadioButton rdbtn = new RadioButton(getActivity());
                rdbtn.setId(View.generateViewId());
                rdbtn.setText(objDilemaOptions.get(i));
                ll.addView(rdbtn);
            }
            ((ViewGroup) ProfileView.findViewById(R.id.radiogroup)).addView(ll);
        }

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
}
