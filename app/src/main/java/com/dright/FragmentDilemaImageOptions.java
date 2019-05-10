package com.dright;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private boolean check = false;
    private static int counter = 0;
    private List<String> allVoters;
    private List<Dilema> mDilemaList = new ArrayList<>();
    private List<String> mDilemaId = new ArrayList<>();
    private List<Boolean> mDilemaCheck = new ArrayList<>();
    private Dilema objDilema;
    private ImageButton btnComment;
    private List<Integer> objDilemaOptionsResults = new ArrayList<>();
    private String dilemaAsker;
    LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


    private SlideUp slideUpImage;
    private View dimImage;
    private View slideViewImage;
    RelativeLayout swipelayoutImage;

    RecyclerView recyclerView;

    ArrayList<String> Users = new ArrayList<>();
    ArrayList<String> Comments = new ArrayList<>();

    ArrayList<CommentsModel> UserComments = new ArrayList<>();
    Handler handler = new Handler();

    public static FragmentDilemaImageOptions newInstance(Dilema objDilema, String dilemaId, List<Dilema> objDilLista, List<String> objDilIdList, List<Boolean> objDilCheck) {
        FragmentDilemaImageOptions fragment = new FragmentDilemaImageOptions();
        Bundle args = new Bundle();
        args.putSerializable("objectDilema", objDilema);
        args.putSerializable("objDilList", (Serializable) objDilLista);
        args.putSerializable("objDilIdList", (Serializable) objDilIdList);
        args.putSerializable("objDilCheckList", (Serializable) objDilCheck);
        args.putString("dilemaId",dilemaId);
        Log.d("teargumentiimage",dilemaId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        objDilema = (Dilema) getArguments().getSerializable("objectDilema");

        dilemaId = getArguments().getString("dilemaId");

        mDilemaList = (List<Dilema>) getArguments().getSerializable("objDilList");
        mDilemaCheck = (List<Boolean>) getArguments().getSerializable("objDilCheckList");
        mDilemaId = (List<String>) getArguments().getSerializable("objDilIdList");

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("state", state);
    }

    View ProfileView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileView = inflater.inflate(R.layout.fragment_image_dilema_options,container,false);
        linearLayout = ProfileView.findViewById(R.id.insideLinear);
        objDilemaOptionsResults = objDilema.getOptionsResults();
        txtPostedBy = ProfileView.findViewById(R.id.txtPostedBy);
        txtComment = ProfileView.findViewById(R.id.txtComment);
        txtTitle = ProfileView.findViewById(R.id.txtTitle);
        btnComment = ProfileView.findViewById(R.id.btnComment);
        recyclerView = ProfileView.findViewById(R.id.dilemma_options_results_image_recycler_view);
        scrollView = ProfileView.findViewById(R.id.scrollView);
        if (savedInstanceState != null) {
            //probably orientation change
            state = savedInstanceState.getBoolean("state");
        }

        btnComment.setEnabled(state);
        txtComment.setEnabled(state);
        mDatabase = FirebaseDatabase.getInstance().getReference("DilemaVoters").child(DilemaTab.currUser);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
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
                        Log.d("mestuffimage",dilemaId);
                    }
                    else{
                        addStuffWithoutOptions();
                        Log.d("pastuffimage",dilemaId);
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
                    Log.d("iparihyri",Users.size()+"");
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
                            if(ds.getKey().equals(DilemaTab.currUser)){
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

        swipelayoutImage = ProfileView.findViewById(R.id.swipe_up_layout_dilema_image_options);
        slideViewImage = ProfileView.findViewById(R.id.slideView_image_fragment);
        dimImage = ProfileView.findViewById(R.id.dim_dilema_image_options);
        slideUpImage = new SlideUp(slideViewImage);
        slideUpImage.hideImmediately();





        swipelayoutImage.setOnTouchListener(new OnSwipeTouchListener(ProfileView.getContext())
        {
            public void onSwipeTop()
            {
                swipelayoutImage.setVisibility(View.INVISIBLE);
                slideUpImage.animateIn();

            }
        });

        slideUpImage.setSlideListener(new SlideUp.SlideListener() {

            @Override
            public void onSlideDown(float v)
            {

                dimImage.setAlpha(1 - (v / 100));
            }

            @Override
            public void onVisibilityChanged(int i) {
                if (i == View.GONE)
                {
                    swipelayoutImage.setVisibility(View.VISIBLE);
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

            dataB1.child(DilemaTab.currUser).setValue(txtComment.getText().toString());
            btnComment.setEnabled(false);
            txtComment.setEnabled(false);
            state = false;
            txtComment.setText("");
            Toast.makeText(getActivity(), "Please also vote on the dilema, if you haven't done it yet!", Toast.LENGTH_SHORT).show();
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
            DatabaseReference db1 = FirebaseDatabase.getInstance().getReference("DilemaVoters");
            DatabaseReference db2 = db1.child(DilemaTab.currUser);
            db2.child(dilemaId).setValue("Voted");
            DatabaseReference dbRef3 = FirebaseDatabase.getInstance().getReference("Users");
            DatabaseReference dbRef4 = dbRef3.child(DilemaTab.currUser).child("docents");
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
        DatabaseReference dbRef6 = dbRef5.child(DilemaTab.currUser).child("docents");
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