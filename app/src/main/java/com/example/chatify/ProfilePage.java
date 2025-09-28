package com.example.chatify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePage extends AppCompatActivity {

    private TextView NAME,EMAIL,DATE,PHONE,GENDER,STATUS;
    private ProgressBar progressBar;
    private CircleImageView PROFILEIMAGE;
    FirebaseStorage storage;
    FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    private static final String TAG = "ProfilePage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        NAME = findViewById(R.id.name);
        EMAIL = findViewById(R.id.email);
        GENDER = findViewById(R.id.gender);
        PHONE = findViewById(R.id.phone);
        DATE = findViewById(R.id.date);
        STATUS = findViewById(R.id.status);
        PROFILEIMAGE = findViewById(R.id.profileimage);

        progressBar = findViewById(R.id.progressbar);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        firebaseUser = auth.getCurrentUser();


        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String dob = snapshot.child("dob").getValue().toString();
                String email = snapshot.child("email").getValue().toString();
                String name = snapshot.child("name").getValue().toString();
                String gender = snapshot.child("gender").getValue().toString();
                String phone = snapshot.child("phone").getValue().toString();
                String status = snapshot.child("status").getValue().toString();
                String image = snapshot.child("imageUri").getValue().toString();

                NAME.setText(name);
                DATE.setText(dob);
                EMAIL.setText(email);
                GENDER.setText(gender);
                PHONE.setText(phone);
                STATUS.setText(status);
                Picasso.get().load(image).into(PROFILEIMAGE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        if (firebaseUser != null) {
            checkIfEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            showToast("Something went Wrong! User's Details are not Available at the Moment");

        }
    }

    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        if (!firebaseUser.isEmailVerified()) {
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfilePage.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your Email now. You cannot log in without Email Verification.");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showToast(String message) {
        Toast.makeText(ProfilePage.this, message, Toast.LENGTH_LONG).show();
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, HomePage.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
