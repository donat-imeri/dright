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
    TextView currentfollowers;
    TextView currentfollowing;
    TextView currentemail;
    TextView currenttwitter;


    public static String fulltvname = null;
    public static String fulltvaddress = null;
    public static String email = null;
    public static String facebook = null;
    public static String followers = null;
    public static String following = null;
    public static String twitter = null;
    public static String phone = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileView = inflater.inflate(R.layout.myprofile,container,false);
        currentfullname = ProfileView.findViewById(R.id.tv_name);
        currentaddress  = ProfileView.findViewById(R.id.tv_address);
        currentphone    = ProfileView.findViewById(R.id.txt_current_phone);
        currentfacebook = ProfileView.findViewById(R.id.current_facebook);
        currentemail = ProfileView.findViewById(R.id.txt_current_email);
        currenttwitter = ProfileView.findViewById(R.id.current_twitter);
        currentfollowers = ProfileView.findViewById(R.id.followers);
        currentfollowing = ProfileView.findViewById(R.id.following);



        currentfullname.setText(fulltvname);
        currentaddress.setText(fulltvaddress);
        currentphone.setText(phone);
        currentfacebook.setText(facebook);
        currentemail.setText(email);
        currentfollowers.setText(followers);
        currentfollowing.setText(following);
        currenttwitter.setText(twitter);



        //if(bundle.getString("fullname") != null)
        //currentfullname.setText(bundle.getString("fullname"));
        //currentaddress.setText(bundle.getString("address"));
        //currentemail.setText(bundle.getString("email"));
        //currentfacebook.setText(bundle.getString("facebookaccount"));





        return ProfileView;

    }
}
