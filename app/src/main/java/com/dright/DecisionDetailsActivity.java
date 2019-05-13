package com.dright;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mancj.slideup.SlideUp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DecisionDetailsActivity extends AppCompatActivity {

    private Dilema myDilema;
    private LinearLayout layoutOptionResults;
    private FirebaseDatabase fb;
    private FirebaseStorage fs;
    private DatabaseReference dilemaRef;

    private PieChart graphResults;
    private Button btnSpinTheWheel;
    private TextView txtDilemaDescriptionResult;
    private ImageView imgSpinWheelArrow;
    private CollapsingToolbarLayout toolbarTitle;

    //Swipe up
    public static SlideUp slideUp;
    private View dim;
    public static View slideView;
    private ImageView txtSwipe;
    public static RelativeLayout swipelayout;

    ArrayList<String> Users = new ArrayList<>();
    ArrayList<String> Comments = new ArrayList<>();

    ArrayList<CommentsModel> UserComments = new ArrayList<>();

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decision_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fb=FirebaseDatabase.getInstance();
        fs=FirebaseStorage.getInstance();

        toolbarTitle=findViewById(R.id.toolbar_layout);
        btnSpinTheWheel=(Button) findViewById(R.id.btn_spin_the_wheel);
        txtDilemaDescriptionResult=(TextView) findViewById(R.id.txt_dilema_description_result);
        imgSpinWheelArrow=(ImageView) findViewById(R.id.img_spin_wheel_arrow);
        graphResults=(PieChart)findViewById(R.id.graph_results);
        graphResults.setUsePercentValues(true);
        graphResults.setDrawHoleEnabled(true);
        graphResults.setHoleRadius(15);
        graphResults.setTransparentCircleRadius(20);
        graphResults.setRotationEnabled(true);
        Legend l=graphResults.getLegend();
        l.setEnabled(false);
        final Description d=graphResults.getDescription();
        d.setEnabled(false);

        //intent from which this is called sends the key to access this dilema
        //Now accessing manualy a dilema
        Intent intent=getIntent();


        String dilemaKey=intent.getStringExtra("dilema_hash");
        dilemaRef=fb.getReference("Dilema/"+dilemaKey);

        dilemaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDilema = dataSnapshot.getValue(Dilema.class);
                toolbarTitle.setTitle("Decision Details");
                txtDilemaDescriptionResult.setText(myDilema.getDilemaDescription());
                int max = 0;
                int numberOfVotes=0;
                if (myDilema.getOptionsResults() != null) {
                    if (myDilema.getOptionsResults().size() > 0){
                        max = calculateMax(myDilema.getOptionsResults());
                        for (int j=0; j<myDilema.getOptionsResults().size();j++){
                            numberOfVotes+=myDilema.getOptionsResults().get(j);
                        }
                    }


                    if (max==0){
                        btnSpinTheWheel.setEnabled(false);
                        graphResults.setVisibility(View.GONE);
                    }

                    layoutOptionResults.removeAllViews();
                    List<PieEntry> entries = new ArrayList<>();
                    List<Integer> colorsArray = new ArrayList<>();

                    for (int i = 0; i < myDilema.getDilemaOptions().size(); i++) {
                        View textOptionResult = getLayoutInflater().inflate(R.layout.text_options_result_layout, null);
                        TextView optionTextResult = textOptionResult.findViewById(R.id.txt_text_option_result);
                        ProgressBar optionTextResultSeekBar = textOptionResult.findViewById(R.id.skb_text_option_result);
                        TextView optionText = textOptionResult.findViewById(R.id.txt_option_description);
                        ImageView optionImage = textOptionResult.findViewById(R.id.img_option_result);

                        optionTextResult.setText(myDilema.getOptionsResults().get(i) + " votes");
                        optionTextResultSeekBar.setProgress((int)((myDilema.getOptionsResults().get(i)*100)/numberOfVotes));

                        if (myDilema.isDilemaText()) {
                            optionText.setText(myDilema.getDilemaOptions().get(i));
                            optionImage.setVisibility(View.GONE);
                        } else {
                            String s = myDilema.getDilemaOptions().get(i);
                            Glide.with(optionText.getContext()).
                                    load(s).override(400, 400).
                                    into(optionImage);
                            optionText.setVisibility(View.GONE);
                        }

                        if (max == myDilema.getOptionsResults().get(i)) {
                            ConstraintLayout cl = textOptionResult.findViewById(R.id.layout_text_option_results);
                            cl.setBackground(getResources().getDrawable(R.drawable.rounded_border_green));
                            entries.add(new PieEntry((float) myDilema.getOptionsResults().get(i), "Most voted"));
                            colorsArray.add(i, getResources().getColor(R.color.colorGreen));
                        } else {
                            if (myDilema.getOptionsResults().get(i)!=0){
                                entries.add(new PieEntry((float) myDilema.getOptionsResults().get(i), "Option " + (i + 1)));
                            }else{
                                entries.add(new PieEntry((float) myDilema.getOptionsResults().get(i), ""));
                            }
                            colorsArray.add(i, getResources().getIntArray(R.array.piechartcolors)[i]);
                        }

                        layoutOptionResults.addView(textOptionResult);

                    }
                    PieDataSet set = new PieDataSet(entries, "");
                    set.setSliceSpace(3);
                    set.setSelectionShift(5);

                    set.setColors(colorsArray);
                    PieData data = new PieData(set);

                    graphResults.setData(data);
                    graphResults.invalidate();
                }
                else{
                    btnSpinTheWheel.setEnabled(false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        layoutOptionResults=(LinearLayout) findViewById(R.id.layout_option_results);



        btnSpinTheWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //graphResults.animateX(1000);
                graphResults.animateY(1000);
                Random random=new Random();
                graphResults.setRotation((float)random.nextInt(361));
                imgSpinWheelArrow.setVisibility(View.VISIBLE);
            }
        });

        dilemaRef = fb.getReference("DilemaComments/"+dilemaKey);

        dilemaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users.clear();
                Comments.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Users.add(ds.getKey().toString());
                    Log.d("wow",Users.size()+"");
                    Comments.add(ds.getValue().toString());
                    Log.d("wow",Comments.size()+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dilemaRef = fb.getReference("Users");
        dilemaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(int i=0;i<Users.size();i++)
                {
                    String username = dataSnapshot.child(Users.get(i)).child("name").getValue().toString();
                    String imageUrl = dataSnapshot.child(Users.get(i)).child("imageURL").getValue().toString();
                    String userhash = Users.get(i);
                    String comment = Comments.get(i);
                    CommentsModel commentsModel = new CommentsModel(username,imageUrl,comment,userhash);
                    UserComments.add(i,commentsModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Swipe up
        swipelayout = findViewById(R.id.swipe_layout_results);
        slideView = findViewById(R.id.slideView);
        dim = findViewById(R.id.dim_results);
        txtSwipe = findViewById(R.id.txt_swipeup_results);
        slideUp = new SlideUp(slideView);
        slideUp.hideImmediately();

        recyclerView = findViewById(R.id.dilemma_options_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        final CommentsRecyclerViewAdapter adapter = new CommentsRecyclerViewAdapter(recyclerView.getContext(),UserComments);
        recyclerView.setAdapter(adapter);


        swipelayout.setOnTouchListener(new OnSwipeTouchListener(DecisionDetailsActivity.this)
        {
            public void onSwipeTop()
            {
                swipelayout.setVisibility(View.INVISIBLE);
                slideUp.animateIn();

            }
        });

        slideUp.setSlideListener(new SlideUp.SlideListener() {

            @Override
            public void onSlideDown(float v)
            {

                dim.setAlpha(1 - (v / 100));
            }

            @Override
            public void onVisibilityChanged(int i) {
                if (i == View.GONE)
                {
                    swipelayout.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    private int calculateMax(List<Integer> listInt){
        int max=0;
        for (int i: listInt) {
            if (i>max){
                max=i;
            }
        }
        return max;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_decision_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent helpIntent=new Intent(DecisionDetailsActivity.this, HelpItemListActivity.class);
            startActivity(helpIntent);
        }
        else
        {
            Intent intent=new Intent(DecisionDetailsActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
