package com.dright;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        Description d=graphResults.getDescription();
        d.setEnabled(false);

        //intent from which this is called sends the key to access this dilema
        //Now accessing manualy a dilema

        String dilemaKey="-LeBHskAh6I1vEXuCTAO";
        dilemaRef=fb.getReference("Dilema/"+dilemaKey);

        dilemaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDilema=dataSnapshot.getValue(Dilema.class);
                toolbarTitle.setTitle("Decision Details");
                txtDilemaDescriptionResult.setText(myDilema.getDilemaDescription());
                int max=calculateMax(myDilema.getOptionsResults());

                layoutOptionResults.removeAllViews();
                List<PieEntry> entries = new ArrayList<>();
                for (int i=0;i<myDilema.getDilemaOptions().size(); i++) {
                    View textOptionResult= getLayoutInflater().inflate(R.layout.text_options_result_layout,null);
                    TextView optionTextResult=textOptionResult.findViewById(R.id.txt_text_option_result);
                    SeekBar optionTextResultSeekBar=textOptionResult.findViewById(R.id.skb_text_option_result);
                    TextView optionText=textOptionResult.findViewById(R.id.txt_option_description);
                    ImageView optionImage=textOptionResult.findViewById(R.id.img_option_result);

                    optionTextResult.setText(myDilema.getOptionsResults().get(i)+"%");
                    optionTextResultSeekBar.setProgress(myDilema.getOptionsResults().get(i));

                    Log.d("Aaaa", "ketu eshte"+"aaaa");

                    if(!myDilema.isDilemaText()){
                        optionText.setText(myDilema.getDilemaOptions().get(i));
                        optionImage.setVisibility(View.GONE);
                    }
                    else{
                        String s=myDilema.getDilemaOptions().get(i);
                        Log.d("Shperhaj","aaaa"+s);
                        Glide.with(optionText.getContext()).
                        load(s).into(optionImage);
                        optionText.setVisibility(View.GONE);
                    }

                    if (max==myDilema.getOptionsResults().get(i)){
                        ConstraintLayout cl=textOptionResult.findViewById(R.id.layout_text_option_results);
                        cl.setBackground(getResources().getDrawable(R.drawable.rounded_border_edittext));
                    }

                    layoutOptionResults.addView(textOptionResult);

                    entries.add(new PieEntry((float)myDilema.getOptionsResults().get(i), myDilema.getDilemaOptions().get(i)));
                }
                PieDataSet set = new PieDataSet(entries, "");
                set.setSliceSpace(3);
                set.setSelectionShift(5);

                set.setColors(new int[] { R.color.colorWhite, R.color.colorPrimary, R.color.colorRed, R.color.colorRed }, getBaseContext());
                PieData data = new PieData(set);

                graphResults.setData(data);
                graphResults.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        layoutOptionResults=(LinearLayout) findViewById(R.id.layout_option_results);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


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
}
