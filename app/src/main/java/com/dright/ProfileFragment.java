package com.dright;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

    View ProfileView;
    TextView currentfullname;
    TextView currentaddress;
    TextView currentphone;
    TextView currentfacebook;
    public static String fulltvname = null;
    public static String fulltvaddress = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileView = inflater.inflate(R.layout.myprofile,container,false);
        currentfullname = ProfileView.findViewById(R.id.tv_name);
        currentaddress  = ProfileView.findViewById(R.id.tv_address);
        currentphone    = ProfileView.findViewById(R.id.txt_current_phone);
        currentfacebook = ProfileView.findViewById(R.id.current_facebook);

        if(EditProfileFragment.fullname != null) {
            currentfullname.setText(EditProfileFragment.fullname.toString());
            fulltvname = currentfullname.getText().toString();
        }
        if(EditProfileFragment.address != null) {
            currentaddress.setText(EditProfileFragment.address.toString());
            fulltvaddress = currentaddress.getText().toString();
        }
        if(EditProfileFragment.phone != null) {
            currentphone.setText(EditProfileFragment.phone.toString());
        }
        if(EditProfileFragment.facebookaccount != null) {
            currentfacebook.setText(EditProfileFragment.facebookaccount.toString());
        }



        //if(bundle.getString("fullname") != null)
        //currentfullname.setText(bundle.getString("fullname"));
        //currentaddress.setText(bundle.getString("address"));
        //currentemail.setText(bundle.getString("email"));
        //currentfacebook.setText(bundle.getString("facebookaccount"));





        return ProfileView;

    }
}
