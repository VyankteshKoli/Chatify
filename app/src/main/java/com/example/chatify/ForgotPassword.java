package com.example.chatify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import javax.sql.StatementEvent;

public class ForgotPassword extends AppCompatActivity {

    TextView resetbutton;
    TextInputEditText emailforgot;

    TextInputLayout EmailLayout;

    FirebaseAuth authProfile;
    ProgressBar progressBar;

    private final static String TAG = "ForgotPassword";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //getSupportActionBar().setTitle("Forgot Password");

        resetbutton = findViewById(R.id.resetbutton);
        emailforgot = findViewById(R.id.emailforgot);
        progressBar = findViewById(R.id.progressbar);
        EmailLayout = findViewById(R.id.EmailLayout);


        resetbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailforgot.getText().toString();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(ForgotPassword.this,"Please enter your registered email",Toast.LENGTH_LONG).show();
                    EmailLayout.setError("Email is required");
                    EmailLayout.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher (email).matches()) {
                    Toast.makeText(ForgotPassword.this, "Please enter valid email", Toast.LENGTH_SHORT).show();
                    EmailLayout.setError("Valid email is required");
                    EmailLayout.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    resetpassword(email);
                }
            }
        });

    }

    private void resetpassword(String email) {

        authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(ForgotPassword.this,"Please Check Your Mailbox For Password Reset Link",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ForgotPassword.this,LoginPage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    try{
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidUserException e)
                    {
                        EmailLayout.setError("User does not Exist or is No Longer Valid,Please Register Again.");
                    }
                    catch(Exception e)
                    {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(ForgotPassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}