package com.dright;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.slideup.SlideUp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static android.support.constraint.Constraints.TAG;


public class FragmentDilemaOptions extends Fragment  implements Serializable{
    private TextView txtPostedBy;
    private EditText txtComment;
    private TextView txtTitle;
    private LinearLayout linearLayout;
    private FirebaseAuth auth;
    private ScrollView scrollViewFr;
    private DatabaseReference mDatabase;
    private static String rdbText = null;
    private String dilemaId;
    private boolean state = true;
    private ImageButton btnCmn;
    private String currUser = "";
    private Dilema objDilema;
    private List<String> objDilemaOptions;
    private List<Integer> objDilemaOptionsResults;
    private String dilemaAsker;
    LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


    //Follow user
    private Button btnFollowUser;
    private ImageView imgReportDilema;
    private FirebaseAuth fa;
    private boolean isFollowing, hasReported;
    private String reportText;
    private int myPosition;
    private int priority;

    private SlideUp slideUp123;
    private View dim123;
    private View slideView123;
    RelativeLayout swipelayout123;
    RecyclerView recyclerView;


    ArrayList<String> Users = new ArrayList<>();
    ArrayList<String> Comments = new ArrayList<>();

    ArrayList<CommentsModel> UserComments = new ArrayList<>();
    Handler handler = new Handler();



    public static FragmentDilemaOptions newInstance(Dilema objDilema, String dilemaId, int dilemaPosition, int priority) {
        FragmentDilemaOptions fragment = new FragmentDilemaOptions();
        Bundle args = new Bundle();
        args.putSerializable("objectDilema", objDilema);
        args.putString("dilemaId", dilemaId);
        args.putInt("dilemaPosition", dilemaPosition);
        args.putInt("dilemaPriority", priority);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("state", state);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        objDilema = (Dilema) getArguments().getSerializable("objectDilema");
        dilemaId = getArguments().getString("dilemaId");
        myPosition=getArguments().getInt("dilemaPosition");
        priority=getArguments().getInt("dilemaPriority");

        Log.d("Dilema id eshte",dilemaId+"aaaaaa");
    }


    View ProfileView;
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileView = inflater.inflate(R.layout.fragment_dilema_options,container,false);
        linearLayout = ProfileView.findViewById(R.id.insideLinear);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        scrollViewFr = ProfileView.findViewById(R.id.scrollViewFragmentOptions);
        recyclerView = ProfileView.findViewById(R.id.dilemma_options_results_recycler_view);

        auth = FirebaseAuth.getInstance();
        currUser = auth.getUid();
        txtPostedBy = ProfileView.findViewById(R.id.txtPostedBy);
        txtComment = ProfileView.findViewById(R.id.txtComment);
        txtTitle = ProfileView.findViewById(R.id.txtTitle);
        btnCmn = ProfileView.findViewById(R.id.btnCmn);
        if (savedInstanceState != null) {
            //probably orientation change
            state = savedInstanceState.getBoolean("state");
        }

        btnCmn.setEnabled(state);
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

        mDatabase = FirebaseDatabase.getInstance().getReference("DilemaComments").child(dilemaId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users.clear();
                Comments.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Users.add(ds.getKey().toString());
                    Comments.add(ds.getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDatabase = FirebaseDatabase.getInstance().getReference("Users");
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(Users.size() > 0)
                        {
                            for(int i=0;i<Users.size();i++)
                            {
                                Log.d("hahahha",Users.size()+"");
                                String username = dataSnapshot.child(Users.get(i)).child("name").getValue().toString();
                                String imageUrl = dataSnapshot.child(Users.get(i)).child("imageURL").getValue().toString();
                                String userhash = Users.get(i);
                                String comment = Comments.get(i);
                                CommentsModel commentsModel = new CommentsModel(username,imageUrl,comment,userhash);
                                UserComments.add(i,commentsModel);
                                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                                final CommentsRecyclerViewAdapter adapter = new CommentsRecyclerViewAdapter(recyclerView.getContext(),UserComments);
                                recyclerView.setAdapter(adapter);
                            }
                        }
                        else
                        {
                            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                            final CommentsRecyclerViewAdapter adapter = new CommentsRecyclerViewAdapter(recyclerView.getContext(),UserComments);
                            recyclerView.setAdapter(adapter);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        },600);


        btnCmn.setOnClickListener(new View.OnClickListener() {
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
                            btnCmn.setEnabled(false);
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

        swipelayout123 = ProfileView.findViewById(R.id.swipe_up_layout_dilema_options);
        slideView123 = ProfileView.findViewById(R.id.slideView_fragment123);
        dim123 = ProfileView.findViewById(R.id.dim_dilema_options);
        slideUp123 = new SlideUp(slideView123);
        slideUp123.hideImmediately();


        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        final CommentsRecyclerViewAdapter adapter = new CommentsRecyclerViewAdapter(recyclerView.getContext(),UserComments);
        recyclerView.setAdapter(adapter);




        swipelayout123.setOnTouchListener(new OnSwipeTouchListener(ProfileView.getContext())
        {
            public void onSwipeTop()
            {
                swipelayout123.setVisibility(View.INVISIBLE);
                slideUp123.animateIn();

            }
        });

        slideUp123.setSlideListener(new SlideUp.SlideListener() {

            @Override
            public void onSlideDown(float v)
            {

                dim123.setAlpha(1 - (v / 100));
            }

            @Override
            public void onVisibilityChanged(int i) {
                if (i == View.GONE)
                {
                    swipelayout123.setVisibility(View.VISIBLE);
                }

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
            btnCmn.setEnabled(false);
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
        objDilemaOptionsResults = new ArrayList<>();
        objDilemaOptions = new ArrayList<>();
        txtTitle.setText(objDilema.getDilemaDescription());
        dilemaAsker = objDilema.getDilemaAsker();
        Log.d(TAG, "onCreateView: Dilema Asker: "+dilemaAsker);
        objDilemaOptions = objDilema.getDilemaOptions();
        Log.d(TAG, "onCreateView: dilema options size: "+objDilema.getDilemaOptions().size());
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
        //donat

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

            String option = objDilema.getDilemaOptions().get(i);
            int opResult = objDilema.getOptionsResults().get(i);
            double opResDouble = opResult*1.0;
            double numVotersDouble = numVoters*1.0;
            double percentage = (opResDouble/numVotersDouble)*100;
            Log.d(TAG, "showResults: percentage before: "+((opResult/numVoters)*1.0));
            Log.d(TAG, "showResults: opRes: "+opResult);
            Log.d(TAG, "showResults: percentage: "+percentage);
            TextView tv = new TextView(getContext());
            tv.setLayoutParams(lparams);
            String percentageStr = String.format("%.2f",percentage);
            tv.setText(option+": "+percentageStr+ " %");
            tv.setId(i+100);
            tv.setPadding(20,20,20,20);
            tv.setTextSize(18);
            ProgressBar pg = new ProgressBar(getContext(),null,android.R.attr.progressBarStyleHorizontal);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,50);
            pg.setLayoutParams(params);
            pg.setPadding(20,20,20,20);
            pg.setScaleY(10);
            pg.setProgress((int)percentage);
            linearLayout.addView(tv);
            linearLayout.addView(pg);
        }
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
}
