package com.dright;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyDecisionsTab extends Fragment {
    public final static int RC_IMAGE=1;

    private FloatingActionButton btnSubmit;
    private CheckBox chbAnonymous;
    private LinearLayout addOption, optionsLayout;
    private ExpandableHeightGridView imageOptionsLayout;
    private EditText dilemaDescription;
    private SeekBar sbPriority;
    private Switch isImageOption;
    private TextView lblDocents, txtPriority, lblError, lblError2, lblError3;
    private NumberPicker pckMinutes, pckHours, pckDays;
    private int optionCounter;
    private boolean reseted;
    private Docent userDocents;
    public static List optionsList;
    private List<Uri> imageUriList;
    private FirebaseDatabase fb;
    private FirebaseAuth auth;
    private FirebaseStorage fbStorage;
    private StorageReference imageRef;

    ImageOptionAdapter imageAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.my_decision_tab, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        optionCounter = 0;
        chbAnonymous = (CheckBox) activity.findViewById(R.id.chb_stay_anonymous);
        addOption = (LinearLayout) activity.findViewById(R.id.layout_add_option);
        optionsLayout = (LinearLayout) activity.findViewById(R.id.options_layout);
        imageOptionsLayout = (ExpandableHeightGridView) activity.findViewById(R.id.image_options_layout);
        dilemaDescription = (EditText) activity.findViewById(R.id.txt_dilema_description);
        sbPriority = (SeekBar) activity.findViewById(R.id.sb_priority);
        isImageOption = (Switch) activity.findViewById(R.id.switch_is_image);
        lblDocents = (TextView) activity.findViewById(R.id.lbl_total_docent);
        txtPriority = (TextView) activity.findViewById(R.id.lbl_priority_value);
        lblError = (TextView) activity.findViewById(R.id.lbl_dilema_error);
        lblError2 = (TextView) activity.findViewById(R.id.lbl_dilema_error2);
        lblError3 = (TextView) activity.findViewById(R.id.lbl_dilema_error3);
        pckMinutes = (NumberPicker) activity.findViewById(R.id.pick_minutes);
        pckMinutes.setMinValue(0);
        pckMinutes.setMaxValue(59);
        pckMinutes.setValue(30);
        pckHours = (NumberPicker) activity.findViewById(R.id.pick_hours);
        pckHours.setMinValue(0);
        pckHours.setMaxValue(23);
        pckDays = (NumberPicker) activity.findViewById(R.id.pick_days);
        pckDays.setMinValue(0);
        pckDays.setMaxValue(30);
        btnSubmit = (FloatingActionButton) activity.findViewById(R.id.btn_submit);
        userDocents = null;
        reseted = false;
        imageUriList = new ArrayList<>();
        optionsList = new ArrayList<>();
        fb = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        fbStorage = FirebaseStorage.getInstance();
        imageRef = fbStorage.getReference("dilema_photos");

        imageAdapter = new ImageOptionAdapter(MyDecisionsTab.this.getContext(), imageUriList);
        imageOptionsLayout.setAdapter(imageAdapter);
        imageOptionsLayout.setExpanded(true);


        //Get users docents
        final DatabaseReference userDocentsReference = fb.getReference("Users/" + auth.getUid() + "/docents");
        userDocentsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userDocents = dataSnapshot.getValue(Docent.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Dilema not described
        dilemaDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (dilemaValid(dilemaDescription.getText().toString())){
                    btnSubmit.setEnabled(true);
                    btnSubmit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                    lblError3.setVisibility(View.INVISIBLE);
                }
                else{
                    btnSubmit.setEnabled(false);
                    btnSubmit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));
                    lblError3.setVisibility(View.VISIBLE);
                }
            }
        });

        sbPriority.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtPriority.setText(priorityText(progress));
                lblDocents.setText("Total docents: " + String.valueOf(calculateDocents(progress)) + "$");

                if (userDocents.checkRemove(progress)) {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                    lblError.setVisibility(View.INVISIBLE);
                } else {
                    btnSubmit.setEnabled(false);
                    btnSubmit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));
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

        pckMinutes.setOnValueChangedListener(new TimeoutChangeListener());
        pckHours.setOnValueChangedListener(new TimeoutChangeListener());
        pckDays.setOnValueChangedListener(new TimeoutChangeListener());




        //Image or text switch listener
        isImageOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                if ((listOptions(optionsLayout).size() > 0 || imageUriList.size() > 0) && !reseted) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    if (isChecked) {
                                        optionsLayout.removeAllViews();
                                    } else {
                                        imageUriList.clear();
                                        imageAdapter.notifyDataSetChanged();
                                    }
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    reseted = true;
                                    if (isChecked) isImageOption.setChecked(false);
                                    else isImageOption.setChecked(true);

                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(buttonView.getContext());
                    builder.setTitle("Are you sure? ");
                    builder.setMessage("Current options will be deleted").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                } else {
                    reseted = false;
                }


            }
        });

        addOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOption(optionsLayout,"Option "+optionCounter);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Change user docents
                btnSubmit.setEnabled(false);
                userDocents.removeDocent(sbPriority.getProgress());
                userDocentsReference.setValue(userDocents);

                //Fill in the options list
                if (!checkTextImage()){
                    optionsList=listOptions(optionsLayout);
                }
                pushChanges();

            }
        });

    }

    public void pushChanges(){
        //Fill in the results - Initialize them with 0 values
        List<Integer> listOptionResults=new ArrayList<>();
        for (int j=0;j<optionsList.size();j++){
            listOptionResults.add(0);
        }


        long actualTime=Calendar.getInstance().getTimeInMillis();
        Dilema newDilema=new Dilema(String.valueOf(dilemaDescription.getText()), optionsList,
                null, auth.getUid(), sbPriority.getProgress(), calculateTimeout(),
                checkAnonimity(), listOptionResults, !checkTextImage(), actualTime, false);

        DatabaseReference table=fb.getReference("Dilema");
        DatabaseReference newRow=table.push();
        newRow.setValue(newDilema);
        Toast.makeText(getActivity(), "You'll make a decision soon", Toast.LENGTH_SHORT).show();

        DatabaseReference addDilemaToUser=fb.getReference("Users/"+auth.getUid());
        addDilemaToUser.child("dilemasInProgress").push().setValue(newRow.getKey());

        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RC_IMAGE && resultCode==getActivity().RESULT_OK){
            if (checkTextImage()){
                btnSubmit.setEnabled(false);
                btnSubmit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));

                Uri imageData=data.getData();
                imageUriList.add(imageData);
                imageAdapter.notifyDataSetChanged();

                //Add image to storage
                final StorageReference newImage=imageRef.child(imageData.getLastPathSegment()+auth.getUid());
                newImage.putFile(imageData).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return newImage.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                            optionsList.add(downloadUri.toString());
                            btnSubmit.setEnabled(true);
                            btnSubmit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));

                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });
            }
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
    private long calculateTimeout(){
        int days=pckDays.getValue();
        int hours=pckHours.getValue();
        int minutes=pckMinutes.getValue();

        int totalTimeInMinutes=((24*60)*days)+(hours*60)+minutes;
        return totalTimeInMinutes;
    }
    private boolean timeoutValid(long timeMilis){
        if (timeMilis>=3) return true;
        else return false;
    }
    private boolean checkTextImage(){
        return isImageOption.isChecked();
    }
    private boolean checkAnonimity(){
       return chbAnonymous.isChecked();
    }
    private void addOption(View layout, String text){

        //If image
        if (checkTextImage()){
            Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(imageIntent, MyDecisionsTab.RC_IMAGE);
        }
        //If text
        else {
            final View option = getLayoutInflater().inflate(R.layout.options_layout, null);
            EditText optionText = option.findViewById(R.id.txt_option_1);
            ImageView removeOption = option.findViewById(R.id.icon_remove);
            optionText.setHint(text);
            removeOption.setOnClickListener(new RemoveOption(optionCounter,(LinearLayout) layout));
            option.setId(optionCounter);
            ((LinearLayout)layout).addView(option);
            optionCounter++;
        }
    }
    private List<String> listOptions(LinearLayout layout){
        optionsList=new ArrayList<>();

        for (int i=0;i<layout.getChildCount();i++){
            EditText option=(EditText)layout.getChildAt(i).findViewById(R.id.txt_option_1);
            optionsList.add(String.valueOf(option.getText()));
        }
        return optionsList;
    }
    private boolean dilemaValid(String dilemaText){
        if (dilemaText.length()>5){
            return true;
        }
        else return false;
    }

    class TimeoutChangeListener implements NumberPicker.OnValueChangeListener{

        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            if (timeoutValid(calculateTimeout())){
                lblError2.setVisibility(View.INVISIBLE);
                btnSubmit.setEnabled(true);
                btnSubmit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));            }
            else{
                lblError2.setVisibility(View.VISIBLE);
                btnSubmit.setEnabled(false);
                btnSubmit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));
            }
        }
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
