package com.dright;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MyDecisionsTab extends Fragment {
    private Button btnSubmit;
    private CheckBox chbAnonymous;
    private LinearLayout addOption;
    private EditText dilemaDescription;
    private SeekBar sbPriority, sbTimeout;
    private TextView lblDocents, txtPriority, txtTimeout;


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
        btnSubmit=(Button)activity.findViewById(R.id.btn_submit);
        chbAnonymous=(CheckBox)activity.findViewById(R.id.chb_stay_anonymous);
        addOption=(LinearLayout) activity.findViewById(R.id.layout_add_option);
        dilemaDescription=(EditText) activity.findViewById(R.id.txt_dilema_description);
        sbPriority=(SeekBar) activity.findViewById(R.id.sb_priority);
        sbTimeout=(SeekBar) activity.findViewById(R.id.sb_timeout);
        lblDocents=(TextView) activity.findViewById(R.id.lbl_total_docent);
        txtPriority=(TextView) activity.findViewById(R.id.lbl_priority_value);
        txtTimeout=(TextView) activity.findViewById(R.id.lbl_timout_type);

        sbPriority.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtPriority.setText(priorityText(progress));
                lblDocents.setText("Total docents: " +String.valueOf(calculateDocents(progress))+"$");
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
        addOption.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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
}