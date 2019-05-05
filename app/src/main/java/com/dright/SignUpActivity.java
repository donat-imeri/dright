package com.dright;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText txtEmail;
    private EditText txtFullName;
    private EditText txtPassword;
    private EditText txtPasswordConfirm;
    private Button signUp;
    private TextView goToLogin;
    private DatabaseReference fb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        txtEmail=findViewById(R.id.txt_email);
        txtFullName=findViewById(R.id.txt_name);
        txtPassword=findViewById(R.id.txt_password);
        txtPasswordConfirm=findViewById(R.id.txt_password_confirm);
        goToLogin=findViewById(R.id.lbl_have_account);
        signUp=findViewById(R.id.btn_signup);
        fb=FirebaseDatabase.getInstance().getReference("Users");

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.createUserWithEmailAndPassword(txtEmail.getText().toString(), txtPassword.getText().toString())
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("Success", "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    putUserOnDatabase(mAuth.getUid(), txtFullName.getText().toString(), txtEmail.getText().toString());
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }

                                // ...
                            }
                        });
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null)
        updateUI(currentUser);
    }

    public void updateUI(FirebaseUser fbUser){
        if (fbUser!=null){
            Intent goToLogin=new Intent(SignUpActivity.this, DecisionsAcitivity.class);
            goToLogin.putExtra("fbUser",fbUser);
            finish();
        }
    }

    public void putUserOnDatabase(String userId, String name, String email){
        Log.d("userID", userId);
        Docent d=new Docent(0);
        UserProfile user=new UserProfile(email, name, 0, d);
        DatabaseReference newUser=fb.child(userId);
        newUser.setValue(user);
        newUser.child("address").setValue("");
        newUser.child("phone").setValue("");
        newUser.child("twitter").setValue("");
        newUser.child("facebook").setValue("");
        newUser.child("followers");
        newUser.child("following");
        newUser.child("imageURL").setValue("");
    }
}
