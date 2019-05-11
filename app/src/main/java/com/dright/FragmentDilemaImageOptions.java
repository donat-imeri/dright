package com.dright;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static android.support.constraint.Constraints.TAG;


public class FragmentDilemaImageOptions extends Fragment  implements Serializable{
    private TextView txtPostedBy;
    private EditText txtComment;
    private TextView txtTitle;
    private LinearLayout linearLayout;
    private ScrollView scrollView;
    private String dilemaId;
    private DatabaseReference mDatabase;
    private static String rdbText = null;
    private boolean state = true;
    private String currUser= "";
    private FirebaseAuth auth;
    private boolean check = false;
    private static int counter = 0;
    private List<String> allVoters;
    private Dilema objDilema;
    private ImageButton btnComment;
    private List<Integer> objDilemaOptionsResults = new ArrayList<>();
    private String dilemaAsker;
    LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    //Follow user
    private Button btnFollowUser;
    private ImageView imgReportDilema;
    private FirebaseAuth fa;
    private boolean isFollowing, hasReported;
    private String reportText;
    private int myPosition;
    private int priority;

    public static FragmentDilemaImageOptions newInstance(Dilema objDilema, String dilemaId, int dilemaPosition, int priority) {
        FragmentDilemaImageOptions fragment = new FragmentDilemaImageOptions();
        Bundle args = new Bundle();
        args.putSerializable("objectDilema", objDilema);
        args.putString("dilemaId",dilemaId);
        args.putInt("dilemaPosition", dilemaPosition);
        args.putInt("dilemaPriority", priority);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        objDilema = (Dilema) getArguments().getSerializable("objectDilema");
        dilemaId = getArguments().getString("dilemaId");
        myPosition=getArguments().getInt("dilemaPosition");
        priority=getArguments().getInt("dilemaPriority");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("state", state);
    }

    View ProfileView;
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileView = inflater.inflate(R.layout.fragment_image_dilema_options,container,false);
        linearLayout = ProfileView.findViewById(R.id.insideLinear);
        objDilemaOptionsResults = objDilema.getOptionsResults();
        txtPostedBy = ProfileView.findViewById(R.id.txtPostedBy);
        txtComment = ProfileView.findViewById(R.id.txtComment);
        auth = FirebaseAuth.getInstance();
        currUser = auth.getUid();
        txtTitle = ProfileView.findViewById(R.id.txtTitle);
        btnComment = ProfileView.findViewById(R.id.btnComment);
        scrollView = ProfileView.findViewById(R.id.scrollView);
        if (savedInstanceState != null) {
            //probably orientation change
            state = savedInstanceState.getBoolean("state");
        }

        btnComment.setEnabled(state);
        txtComment.setEnabled(state);

        //Follow user
        btnFollowUser=ProfileView.findViewById(R.id.btn_follow_user);
        imgReportDilema=ProfileView.findViewById(R.id.img_report_dilema);
        fa=FirebaseAuth.getInstance();
        isFollowing=false;
        hasReported=false;


        if (!objDilema.isStayAnonymous()) {

            DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference("Users").child(fa.getUid()).child("following");
            dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot s : dataSnapshot.getChildren()
                    ) {
                        if (objDilema.getDilemaAsker().equals(s.getKey()) && !isFollowing) {
                            btnFollowUser.setBackground(getResources().getDrawable(R.drawable.rounded_border_no_color));
                            btnFollowUser.setBackgroundColor(getResources().getColor(R.color.grey));
                            btnFollowUser.setText("Following");
                            isFollowing = true;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            btnFollowUser.setBackground(getResources().getDrawable(R.drawable.rounded_border_no_color));
            btnFollowUser.setBackgroundColor(getResources().getColor(R.color.grey));
            btnFollowUser.setText("Anonymous");
            isFollowing = true;
        }


        btnFollowUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFollowing){
                    isFollowing=true;
                    btnFollowUser.setEnabled(false);
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(objDilema.getDilemaAsker());
                    db.child("followers").child(fa.getUid()).setValue("Subbed");
                    db = FirebaseDatabase.getInstance().getReference("Users").child(fa.getUid());
                    db.child("following").child(objDilema.getDilemaAsker()).setValue("followed");

                    btnFollowUser.setBackground(getResources().getDrawable(R.drawable.rounded_border_no_color));
                    btnFollowUser.setBackgroundColor(getResources().getColor(R.color.grey));
                    btnFollowUser.setText("Following");

                    Toast.makeText(getActivity(), "Follow added", Toast.LENGTH_SHORT).show();
                }

            }
        });

        DatabaseReference dbReport = FirebaseDatabase.getInstance().getReference("DilemaReported").child(dilemaId);
        dbReport.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (fa.getUid().equals(ds.getKey())){
                        hasReported=true;
                        imgReportDilema.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imgReportDilema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasReported){
                    reportText="";
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Why are you reporting this?");

                    View reportView=inflater.inflate(R.layout.report_layout, null);
                    final TextInputEditText txtReport=reportView.findViewById(R.id.txt_report_description);
                    builder.setView(reportView);

                    // Set up the buttons
                    builder.setPositiveButton("Report", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            reportText = txtReport.getText().toString();
                            DatabaseReference dbReport = FirebaseDatabase.getInstance().getReference("DilemaReported").child(dilemaId);
                            dbReport.child(fa.getUid()).setValue(reportText);
                            imgReportDilema.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Dilemma Reported. Thank you!", Toast.LENGTH_SHORT).show();
                            hasReported=true;
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
                else{
                    Toast.makeText(getActivity(), "Already reported this dilemma", Toast.LENGTH_SHORT).show();
                }

            }
        });



        DatabaseReference dbRef10;
        DatabaseReference dbRef11;

        if (myPosition<DilemaTab.dilematFollowing.size()){
            dbRef10= FirebaseDatabase.getInstance().getReference("DilemaVoters");
        }
        else{
            dbRef10= FirebaseDatabase.getInstance().getReference("DilemaVotersTemporary");
        }
        dbRef11 = dbRef10.child(currUser);
        dbRef11.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter = 0;
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if(ds.getKey().equals(dilemaId)){
                        counter++;
                    }
                }
                Log.d(TAG, "onDataChange: counter: "+counter);

                if(counter == 0){
                    if(objDilema.getDilemaOptions()!= null) {
                        addStuff();
                    }
                    else{
                        addStuffWithoutOptions();
                    }
                }
                else{
                    if(objDilema.getDilemaOptions()!= null) {
                        showResults();
                    }
                    else{
                        //If we want to display comments
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //addStuff();




        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dataBRef = FirebaseDatabase.getInstance().getReference("DilemaComments");
                DatabaseReference dataBRef1 = dataBRef.child(dilemaId);
                dataBRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int counter = 0;
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            if(ds.getKey().equals(currUser)){
                                counter++;
                            }
                        }
                        if(counter != 0){
                            btnComment.setEnabled(false);
                            state = false;
                            txtComment.setEnabled(false);
                            txtComment.setText("");
                            Toast.makeText(getContext(), "You have already voted in this dilema!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            vote();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


        Log.d(TAG, "onCreateView: Is calling return ProfileView");
        return ProfileView;

    }
    private void vote(){
        String pattern = "^[a-zA-Z0-9\\.\\,\\-\\_\\!\\?\\(\\)\\s]+$";
        if(Pattern.matches(pattern,txtComment.getText().toString())){

            DatabaseReference dataB = FirebaseDatabase.getInstance().getReference("DilemaComments");
            DatabaseReference dataB1 = dataB.child(dilemaId);

            dataB1.child(currUser).setValue(txtComment.getText().toString());
            btnComment.setEnabled(false);
            txtComment.setEnabled(false);
            state = false;
            txtComment.setText("");

            //donat
            if (myPosition<DilemaTab.dilematFollowing.size()){
                DatabaseReference db1 = FirebaseDatabase.getInstance().getReference("DilemaVoters/"+currUser);
                db1.child(dilemaId).setValue("Voted");
            }
            else{
                DatabaseReference getLastIds=FirebaseDatabase.getInstance().getReference("UserLastId").child(auth.getUid());
                getLastIds.child(String.valueOf(priority)).setValue(dilemaId);

                DatabaseReference db1 = FirebaseDatabase.getInstance().getReference("DilemaVotersTemporary/"+currUser);
                db1.child(dilemaId).setValue("Voted");
            }
            //donat
            Toast.makeText(getActivity(), "Thanks for helping the community!", Toast.LENGTH_SHORT).show();

        }
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


    private void updateOptionsResult(int id){

                int resOption = objDilemaOptionsResults.get(id);
                resOption += 1;
                objDilemaOptionsResults.set(id,resOption);
                objDilema.setOptionsResults(objDilemaOptionsResults);
                updateDb(objDilemaOptionsResults);
        Log.d(TAG, "updateOptionsResult: results Updated!");

    }




    private void addTextViews(Dilema objDil){
            for(int i=0; i<objDil.getDilemaOptions().size();i++){
                final ImageView tv = new ImageView(getContext());
                tv.setLayoutParams(lparams);
                Glide.with(getActivity())
                        .load(objDil.getDilemaOptions().get(i))
                        .override(400,300)
                        .centerCrop()
                        .into(tv);
                tv.setId(i);
                tv.setPadding(20,20,20,20);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            updateOptionsResult(tv.getId());

                        Toast.makeText(getActivity(),"Click in imageVIew with id: "+tv.getId()+", just happened",Toast.LENGTH_LONG).show();

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

            //donat
            if (myPosition<DilemaTab.dilematFollowing.size()){
                DatabaseReference db1 = FirebaseDatabase.getInstance().getReference("DilemaVoters/"+currUser);
                db1.child(dilemaId).setValue("Voted");
            }
            else{
                DatabaseReference getLastIds=FirebaseDatabase.getInstance().getReference("UserLastId").child(auth.getUid());
                getLastIds.child(String.valueOf(priority)).setValue(dilemaId);

                DatabaseReference db1 = FirebaseDatabase.getInstance().getReference("DilemaVotersTemporary/"+currUser);
                db1.child(dilemaId).setValue("Voted");
            }


            DatabaseReference dbRef3 = FirebaseDatabase.getInstance().getReference("Users");
            DatabaseReference dbRef4 = dbRef3.child(currUser).child("docents");
            dbRef4.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long amount = 0L;
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        Long objD = ds.getValue(Long.class);
                        amount = objD;
                        Log.d(TAG, "onDataChange: amount = "+amount);
                    }
                    amount += 1;
                    increaseAmount(amount);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            linearLayout.removeAllViews();
            showResults();
        }

    private void increaseAmount(Long amount){

        DatabaseReference dbRef5 = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference dbRef6 = dbRef5.child(currUser).child("docents");
        dbRef6.child("amount").setValue(amount);

    }
    private void addStuffWithoutOptions(){

        txtTitle.setText(objDilema.getDilemaDescription());
        dilemaAsker = objDilema.getDilemaAsker();
        Log.d(TAG, "onCreateView: Dilema Asker: "+dilemaAsker);
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
    private void showResults(){
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
        txtTitle.setText(objDilema.getDilemaDescription());
        long numVoters = 0;
        for(int i=0;i<objDilema.getOptionsResults().size();i++){
            numVoters += objDilema.getOptionsResults().get(i);
        }

        for(int i=0;i<objDilema.getOptionsResults().size();i++){
            Log.d(TAG, "showResults: numVoters: "+numVoters);

            LinearLayout layoutInside = new LinearLayout(getContext());
            layoutInside.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            layoutInside.setOrientation(LinearLayout.VERTICAL);
            int opResult = objDilema.getOptionsResults().get(i);
            double opResDouble = opResult*1.0;
            double numVotersDouble = numVoters*1.0;
            double percentage = (opResDouble/numVotersDouble)*100;
            Log.d(TAG, "showResults: percentage before: "+((opResult/numVoters)*1.0));
            Log.d(TAG, "showResults: opRes: "+opResult);
            Log.d(TAG, "showResults: percentage: "+percentage);
            TextView textV = new TextView(getContext());
            textV.setLayoutParams(lparams);
            textV.setPadding(20,20,20,20);
            textV.setTextSize(18);
            String percentageStr = String.format("%.2f",percentage);
            textV.setText(percentageStr+ " %");
            ImageView tv = new ImageView(getContext());
            tv.setLayoutParams(lparams);
            Glide.with(getActivity())
                    .load(objDilema.getDilemaOptions().get(i))
                    .override(400,300)
                    .centerCrop()
                    .into(tv);
            tv.setId(i);
            tv.setPadding(20,20,20,20);
            ProgressBar pg = new ProgressBar(getContext(),null,android.R.attr.progressBarStyleHorizontal);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,50);
            pg.setLayoutParams(params);
            pg.setPadding(20,20,20,20);
            pg.setScaleY(10);
            pg.setProgress((int)percentage);

            layoutInside.addView(tv);
            layoutInside.addView(textV);
            layoutInside.addView(pg);
            linearLayout.addView(layoutInside);
        }
    }
}