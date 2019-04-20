package com.dright;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class EditProfileFragment extends Fragment {

    View ProfileView;
    TextInputLayout newfullname;
    TextInputLayout newaddress;
    TextInputLayout newphone;
    TextInputLayout newfacebook;

    TextView tvname;
    TextView tvaddress;

    TextView currentfollowers;
    TextView currentfollowing;
    FloatingActionButton fab1;
    public static String fullname= null;
    public static String address= null;
    public static String phone= null;
    public static String facebookaccount= null;
    public static String followers = null;
    public static String following = null;

    private DatabaseReference db;
    private FirebaseAuth currentUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileView = inflater.inflate(R.layout.edit_myprofile,container,false);
        currentUser = FirebaseAuth.getInstance();
        db= FirebaseDatabase.getInstance().getReference("Users/"+currentUser.getUid());
        newfullname = ProfileView.findViewById(R.id.txt_input_fullname);
        newaddress = ProfileView.findViewById(R.id.txt_input_address);
        newphone = ProfileView.findViewById(R.id.txt_input_phone);
        newfacebook = ProfileView.findViewById(R.id.txt_input_facebook);

        tvname = ProfileView.findViewById(R.id.tv_name);
        tvaddress = ProfileView.findViewById(R.id.tv_address);

        currentfollowers = ProfileView.findViewById(R.id.current_followers);
        currentfollowing = ProfileView.findViewById(R.id.current_following);


        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fullname = dataSnapshot.child("name").getValue().toString();
                address = dataSnapshot.child("address").getValue().toString();
                phone = dataSnapshot.child("phone").getValue().toString();
                facebookaccount = dataSnapshot.child("facebook").getValue().toString();
                followers = dataSnapshot.child("followers").getValue().toString();
                following = dataSnapshot.child("following").getValue().toString();
                tvname.setText(fullname);
                tvaddress.setText(address);
                currentfollowers.setText(followers);
                currentfollowing.setText(following);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






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
                    db.child("name").setValue(fullname);
                    tvname.setText(fullname);
                }

                if(address.isEmpty())
                {
                    newaddress.setError("Please fill the Address field");
                }
                else
                {
                    db.child("address").setValue(address);
                    newaddress.setError(null);
                    tvaddress.setText(address);
                }
                db.child("phone").setValue(phone);
                db.child("facebook").setValue(facebookaccount);


            }
        });


        return ProfileView;

    }
}
