package com.dright;

import android.content.Intent;
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

//loginActivity
public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button btnLogin;
    private EditText txtEmail;
    private EditText txtPassword;
    private TextView createAccount;
    private TextView forgotPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail=findViewById(R.id.txt_login_email);
        txtPassword=findViewById(R.id.txt_login_password);
        btnLogin=findViewById(R.id.btn_login);
        createAccount=findViewById(R.id.lbl_create_account);
        forgotPassword=findViewById(R.id.lbl_forgot_password);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email="a";
                String password="a";
                if (!txtEmail.getText().toString().isEmpty()){
                    email=txtEmail.getText().toString();
                }
                if (!txtPassword.getText().toString().isEmpty()){
                    password=txtPassword.getText().toString();
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Intent intent = new Intent(getBaseContext(),DecisionsAcitivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToLogin=new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(goToLogin);
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Feature not yet implemented!",
                        Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            updateUI(currentUser);
        }


    }

    public void updateUI(FirebaseUser fbUser){
        if (fbUser!=null){
            Intent goToDecisions=new Intent(LoginActivity.this, DecisionsAcitivity.class);
            goToDecisions.putExtra("fbUser",fbUser);
            startActivity(goToDecisions);
        }
    }


}
