package com.example.chatify;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatify.HomePage;
import com.example.chatify.R;
import com.example.chatify.SignupPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {

    private static final String TAG = "LoginPage";

    private TextInputEditText email, password;
    private TextView loginBtn, signupBtn, forgotBtn;
    private TextInputLayout EmailLayout, PasswordLayout;
    private ProgressBar progressbar;

    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.signupBtn);
        forgotBtn = findViewById(R.id.forgotBtn);
        EmailLayout = findViewById(R.id.EmailLayout);
        PasswordLayout = findViewById(R.id.PasswordLayout);
        progressbar = findViewById(R.id.progressbar);
        
        authProfile = FirebaseAuth.getInstance();

        forgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this,ForgotPassword.class);
                startActivity(intent);
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, SignupPage.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = email.getText().toString();
                String textPwd = password.getText().toString();

                if (TextUtils.isEmpty(textEmail)) {
                    clearErrors();
                    showValidationError(EmailLayout, "Email is Required");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    clearErrors();
                    showValidationError(EmailLayout, "Please Enter a Valid Email");
                } else if (TextUtils.isEmpty(textPwd)) {
                    clearErrors();
                    showValidationError(PasswordLayout, "Password is Required");
                } else if (textPwd.length() < 6) {
                    clearErrors();
                    showValidationError(PasswordLayout, "Password should be at least 6 Characters");
                } else if (!textPwd.matches(".*\\d.*")) {
                    clearErrors();
                    showValidationError(PasswordLayout, "Password should contain at least one Digit");
                } else {
                    clearErrors();
                    progressbar.setVisibility(View.VISIBLE);
                    loginUser(textEmail, textPwd);
                }
            }
        });

        PasswordLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPasswordVisible = password.getInputType() == (android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                if (isPasswordVisible) {
                    password.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    password.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                password.setSelection(password.length());  
            }
        });
    }

    private void loginUser(String email, String password) {
        authProfile.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginPage.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressbar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();
                    if (firebaseUser != null && firebaseUser.isEmailVerified()) {
                        Toast.makeText(LoginPage.this, "Login Successful", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginPage.this, HomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        showAlertDialog();
                    }
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        Log.e(TAG, "No user registered with this email.", e);
                        showValidationError(EmailLayout, "No user registered with this email. Please sign up.");
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Log.e(TAG, "Invalid password.", e);
                        showValidationError(PasswordLayout, "Invalid password. Please try again.");
                    } catch (Exception e) {
                        Log.e(TAG, "Login error: " + e.getMessage(), e);
                        Toast.makeText(LoginPage.this, "Login failed. Please try again.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
    private void showAlertDialog() {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginPage.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please Verify Your Email Now. You Cannot Login Without Email Verification.");
        //Open Email Apps if User clicks/taps Continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
                startActivity(intent);
            }
        });
        //Create the AlertDialog
        AlertDialog alertDialog = builder.create();
        //Show the AlertDialog
        alertDialog.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(authProfile.getCurrentUser() != null){

            startActivity(new Intent(LoginPage.this, HomePage.class));
            finish();
        }
        else{
            Toast.makeText(LoginPage.this, "Login Failed", Toast.LENGTH_LONG).show();
        }
    }

    private void showValidationError(TextInputLayout layout, String errorMessage) {
        layout.setError(errorMessage);
        layout.requestFocus();
    }
    private void clearErrors() {
        EmailLayout.setError(null);
        PasswordLayout.setError(null);
    }
}
