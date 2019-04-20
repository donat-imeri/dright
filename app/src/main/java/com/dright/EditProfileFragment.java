package com.dright;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EditProfileFragment extends Fragment {

    View ProfileView;
    TextInputLayout newfullname;
    TextInputLayout newaddress;
    TextInputLayout newphone;
    TextInputLayout newfacebook;

    TextView tvname;
    TextView tvaddress;
    FloatingActionButton fab1;
    public static String fullname= null;
    public static String address= null;
    public static String phone= null;
    public static String facebookaccount= null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileView = inflater.inflate(R.layout.edit_myprofile,container,false);

        newfullname = ProfileView.findViewById(R.id.txt_input_fullname);
        newaddress = ProfileView.findViewById(R.id.txt_input_address);
        newphone = ProfileView.findViewById(R.id.txt_input_phone);
        newfacebook = ProfileView.findViewById(R.id.txt_input_facebook);

        tvname = ProfileView.findViewById(R.id.tv_name);
        tvaddress = ProfileView.findViewById(R.id.tv_address);


        if(fullname != null)
        {
            tvname.setText(fullname);
        }
        if(address != null)
        {
            tvaddress.setText(address);
        }






        fab1 = ProfileView.findViewById(R.id.fabsave_profile);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullname = newfullname.getEditText().getText().toString();
                address = newaddress.getEditText().getText().toString();
                phone = newphone.getEditText().getText().toString();
                facebookaccount = newfacebook.getEditText().getText().toString();

                if(fullname.isEmpty())
                {

                    newfullname.setError("Please fill the Full Name field");
                }
                else
                {
                    newfullname.setError(null);
                    fullname = newfullname.getEditText().getText().toString();
                    tvname.setText(fullname);
                }

                if(address.isEmpty())
                {
                    newaddress.setError("Please fill the Address field");
                }
                else
                {
                    newaddress.setError(null);
                    tvaddress.setText(address);
                }



            }
        });


        return ProfileView;

    }
}
