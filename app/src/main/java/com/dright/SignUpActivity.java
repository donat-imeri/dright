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

    public static String fullname = null;
    public static String email = null;



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

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.createUserWithEmailAndPassword(txtEmail.getText().toString(), txtPassword.getText().toString())
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    fullname = txtFullName.getText().toString();
                                    email = txtEmail.getText().toString();
                                    Log.d("Success", "createUserWithEmail:success");
                                    Intent intent = new Intent(getBaseContext(),LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                    //FirebaseUser user = mAuth.getCurrentUser();
                                    //updateUI(user);
                                    //db.child("User1").setValue("useri1");

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }

                                // ...
                            }
                        });
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }


}
