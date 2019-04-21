package com.dright;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyDecisionsTab extends Fragment {
    public final static int RC_IMAGE=1;

    private Button btnSubmit;
    private CheckBox chbAnonymous;
    private LinearLayout addOption, optionsLayout;
    private EditText dilemaDescription;
    private SeekBar sbPriority, sbTimeout;
    private TextView lblDocents, txtPriority, txtTimeout, lblError;
    private int optionCounter;
    public Uri imageUri;
    private View currentView;
    private Docent userDocents;
    private FirebaseDatabase fb;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.my_decision_tab, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity=getActivity();
        optionCounter=0;
        imageUri=null;
        currentView=null;
        btnSubmit=(Button)activity.findViewById(R.id.btn_submit);
        chbAnonymous=(CheckBox)activity.findViewById(R.id.chb_stay_anonymous);
        addOption=(LinearLayout) activity.findViewById(R.id.layout_add_option);
        optionsLayout=(LinearLayout) activity.findViewById(R.id.options_layout);
        dilemaDescription=(EditText) activity.findViewById(R.id.txt_dilema_description);
        sbPriority=(SeekBar) activity.findViewById(R.id.sb_priority);
        sbTimeout=(SeekBar) activity.findViewById(R.id.sb_timeout);
        lblDocents=(TextView) activity.findViewById(R.id.lbl_total_docent);
        txtPriority=(TextView) activity.findViewById(R.id.lbl_priority_value);
        txtTimeout=(TextView) activity.findViewById(R.id.lbl_timout_type);
        lblError=(TextView) activity.findViewById(R.id.lbl_dilema_error);
        fb=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        userDocents=null;

        addOption(optionsLayout, "Option "+optionCounter, imageUri);


        //Get users docents
        final DatabaseReference userDocentsReference=fb.getReference("Users/"+auth.getUid()+"/docents");
        Log.d("userId","aaaa"+auth.getUid());
        userDocentsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userDocents=dataSnapshot.getValue(Docent.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        sbPriority.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtPriority.setText(priorityText(progress));
                lblDocents.setText("Total docents: " +String.valueOf(calculateDocents(progress))+"$");

                if (userDocents.checkRemove(progress)){
                    btnSubmit.setEnabled(true);
                    btnSubmit.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    lblError.setVisibility(View.INVISIBLE);
                }
                else{
                    btnSubmit.setEnabled(false);
                    btnSubmit.setBackgroundColor(getResources().getColor(R.color.solidCircle));
                    lblError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbTimeout.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtTimeout.setText(timeoutText(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        addOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOption(optionsLayout,"Option "+optionCounter, imageUri);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDocents.removeDocent(sbPriority.getProgress());
                userDocentsReference.setValue(userDocents);

                List<Integer> listOptionResults=new ArrayList<>();
                for (int j=0;j<listOptions(optionsLayout).size();j++){
                    listOptionResults.add(0);
                }

                Dilema newDilema=new Dilema(String.valueOf(dilemaDescription.getText()), listOptions(optionsLayout),
                        null, auth.getUid(), sbPriority.getProgress(), sbTimeout.getProgress(),
                        checkAnonimity(), listOptionResults);

                DatabaseReference table=fb.getReference("Dilema");
                DatabaseReference newRow=table.push();
                newRow.setValue(newDilema);
                Toast.makeText(getActivity(), "You'll make a decision soon", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RC_IMAGE && resultCode==getActivity().RESULT_OK){
            int currentIndex=currentView.getId();
            RemoveOption r=new RemoveOption(currentIndex, optionsLayout);
            r.onClick(null);
            Uri imageData=data.getData();
            imageUri=imageData;
            addOption(optionsLayout,"Option "+currentIndex, imageUri);
            imageUri=null;
        }
    }
    private int calculateDocents(int priority){
        return priority;
    }
    private String priorityText(int progress){
        switch (progress){
            case 0: return "None";
            case 1: return "Very low";
            case 2: return "Low";
            case 3: return "Medium";
            case 4: return "High";
            case 5: return "Very high";
            default: return "None";
        }
    }
    private String timeoutText(int progress){
        switch (progress){
            case 0: return "3 minutes";
            case 1: return "10 minutes";
            case 2: return "1 hour";
            case 3: return "6 hours";
            case 4: return "1 day";
            case 5: return "1 week";
            case 6: return "1 month";
            default: return "None";
        }
    }
    private boolean checkAnonimity(){
       return chbAnonymous.isChecked();
    }
    private void addOption(LinearLayout layout, String text, Uri uri){
        final View option=getLayoutInflater().inflate(R.layout.options_layout, null);
        EditText optionText=option.findViewById(R.id.txt_option_1);
        ImageView insertImage=option.findViewById(R.id.icon_insert_image);
        ImageView removeOption=option.findViewById(R.id.icon_remove);
        optionText.setHint(text);
        insertImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                currentView=option;
                startActivityForResult(imageIntent, MyDecisionsTab.RC_IMAGE);
            }
        });
        if (uri!=null){
            insertImage.setImageURI(uri);
            insertImage.getLayoutParams().height = 400;
            insertImage.getLayoutParams().width = 400;
            optionText.setHeight(420);
        }
        removeOption.setOnClickListener(new RemoveOption(optionCounter,layout));
        option.setId(optionCounter);
        layout.addView(option);
        optionCounter++;
    }
    private List<String> listOptions(LinearLayout layout){
        List optionsList=new ArrayList<>();

        for (int i=0;i<layout.getChildCount();i++){
            EditText option=(EditText)layout.getChildAt(i).findViewById(R.id.txt_option_1);
            optionsList.add(String.valueOf(option.getText()));
        }
        return optionsList;
    }

}

class RemoveOption implements View.OnClickListener {
    private int index;
    LinearLayout layout;

    public RemoveOption(int index, LinearLayout layout){
        this.index=index;
        this.layout=layout;
    }

    @Override
    public void onClick(View v) {
        layout.removeView(layout.findViewById(index));
    }
}