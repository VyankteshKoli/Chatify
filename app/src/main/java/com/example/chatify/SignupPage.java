package com.example.chatify;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupPage extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "SignupPage";

    private int year, month, day;

    private ProgressBar progressBar;
    private RadioGroup gender;
    private RadioButton radioButtonRegisterGenderSelected;
    private TextInputEditText name, email, password, phone, datepicker;
    private TextView signupBtn, loginBtn;
    private TextInputLayout EmailLayout, NameLayout, PhoneLayout, PasswordLayout, DateLayout;
    private CircleImageView profileImage;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Uri imageUri;
    private String imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        // Initialize UI elements
        gender = findViewById(R.id.gender);
        name = findViewById(R.id.name);
        datepicker = findViewById(R.id.date);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);

        EmailLayout = findViewById(R.id.EmailLayout);
        PasswordLayout = findViewById(R.id.PasswordLayout);
        NameLayout = findViewById(R.id.NameLayout);
        DateLayout = findViewById(R.id.DateLayout);
        PhoneLayout = findViewById(R.id.PhoneLayout);
        progressBar = findViewById(R.id.progressbar);
        profileImage = findViewById(R.id.profileimage);

        signupBtn = findViewById(R.id.signupBtn);
        loginBtn = findViewById(R.id.loginBtn);

        // Date picker setup
        final Calendar calendar = Calendar.getInstance();
        datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(SignupPage.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        String date = dayOfMonth + "/" + month + "/" + year;
                        datepicker.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        // Gender selection listener
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedGender = findViewById(checkedId);
                if (selectedGender != null) {
                    Toast.makeText(getApplicationContext(), selectedGender.getText(), Toast.LENGTH_LONG).show();
                }
            }
        });

        // Choosing image to upload
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupPage.this, LoginPage.class);
                startActivity(intent);
            }
        });

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedGenderId = gender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected = findViewById(selectedGenderId);
                String textFullName = name.getText().toString();
                String textEmail = email.getText().toString();
                String textDoB = datepicker.getText().toString();
                String textPwd = password.getText().toString();
                String textPhone = phone.getText().toString();
                String textGender = radioButtonRegisterGenderSelected != null ? radioButtonRegisterGenderSelected.getText().toString() : "";
                String status = "Hey there I'm Using Chatify";

                if (TextUtils.isEmpty(textFullName)) {
                    clearErrors();
                    showValidationError(NameLayout, "Name is Required");
                } else if (TextUtils.isEmpty(textDoB)) {
                    clearErrors();
                    showValidationError(DateLayout, "Date of Birth is required");
                } else if (selectedGenderId == -1) {
                    Toast.makeText(SignupPage.this, "Please Select your Gender", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(textPhone)) {
                    clearErrors();
                    showValidationError(PhoneLayout, "Phone number is required");
                } else if (textPhone.length() != 10) {
                    clearErrors();
                    showValidationError(PhoneLayout, "Phone number must be 10 digits");
                } else if (!textPhone.matches("\\d{10}")) {
                    clearErrors();
                    showValidationError(PhoneLayout, "Phone number must contain only digits");
                } else if (TextUtils.isEmpty(textEmail)) {
                    clearErrors();
                    showValidationError(EmailLayout, "Email is required");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    clearErrors();
                    showValidationError(EmailLayout, "Please enter a valid email");
                } else if (TextUtils.isEmpty(textPwd)) {
                    clearErrors();
                    showValidationError(PasswordLayout, "Password is required");
                } else if (textPwd.length() < 6) {
                    clearErrors();
                    showValidationError(PasswordLayout, "Password should be at least 6 digits");
                    password.clearComposingText();
                } else if (!textPwd.matches("^(?=.*\\d).*$")) {
                    clearErrors();
                    showValidationError(PasswordLayout, "Password should contain at least one digit");
                } else if (imageUri == null) {
                    Toast.makeText(SignupPage.this, "Profile image is required", Toast.LENGTH_LONG).show();
                } else {
                    clearErrors();
                    progressBar.setVisibility(View.VISIBLE);

                    auth.createUserWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(SignupPage.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> verificationTask) {
                                        if (verificationTask.isSuccessful()) {
                                            String uid = user.getUid();
                                            DatabaseReference reference = database.getReference().child("user").child(uid);
                                            storageReference = storage.getReference().child("upload").child(uid);

                                            storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                imageURI = uri.toString();
                                                                Users users = new Users(uid, textFullName, textDoB, textGender, textPhone, textEmail, textPwd, status, imageURI);
                                                                reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        progressBar.setVisibility(View.GONE);
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(SignupPage.this, "Registration successful. Please check your email for verification.", Toast.LENGTH_LONG).show();
                                                                            auth.signOut(); // Sign out the user
                                                                            startActivity(new Intent(SignupPage.this, LoginPage.class));
                                                                            finish();
                                                                        } else {
                                                                            Log.e(TAG, "Error in creating user in database", task.getException());
                                                                            Toast.makeText(SignupPage.this, "Error in Creating New Account", Toast.LENGTH_LONG).show();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                progressBar.setVisibility(View.GONE);
                                                                Log.e(TAG, "Error getting download URL", e);
                                                                Toast.makeText(SignupPage.this, "Error in Creating New Account", Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                                    } else {
                                                        progressBar.setVisibility(View.GONE);
                                                        Log.e(TAG, "Error uploading file", task.getException());
                                                        Toast.makeText(SignupPage.this, "Error in Creating New Account", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            progressBar.setVisibility(View.GONE);
                                            Log.e(TAG, "Error sending verification email", verificationTask.getException());
                                            Toast.makeText(SignupPage.this, "Error in Creating New Account", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Log.e(TAG, "Error in creating user", task.getException());
                                Toast.makeText(SignupPage.this, "Error in Creating New Account", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

        private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
    }

    private void clearErrors() {
        NameLayout.setError(null);
        DateLayout.setError(null);
        PhoneLayout.setError(null);
        EmailLayout.setError(null);
        PasswordLayout.setError(null);
    }

    private void showValidationError(TextInputLayout layout, String error) {
        layout.setError(error);
        layout.requestFocus();
    }
}

