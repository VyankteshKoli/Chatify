package com.example.chatify;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/** @noinspection ALL*/
public class ChatPage extends AppCompatActivity {

    private static final String TAG = "ChatPage";

    String ReceiverImage, ReceiverUID, ReceiverName, SenderUID;
    public static String sImage, rImage;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    TextView receiverName;
    private CircleImageView profileImage;
    private EditText edtMessage;
    CardView sendBtn;
    String senderRoom,receiverRoom;
    RecyclerView messageAdapter;
    ArrayList<Messages> messagesArrayList;

    MessagesAdapter adapter;
    ImageView back;

    Button speechToTextBtn;
    private SpeechRecognizer speechRecognizer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        ReceiverName = getIntent().getStringExtra("name");
        ReceiverImage = getIntent().getStringExtra("ReceiverImage");
        ReceiverUID = getIntent().getStringExtra("uid");

        speechToTextBtn = findViewById(R.id.speechToTextBtn);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechToTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechToText();
            }

            private void startSpeechToText() {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");

                try {
                    startActivityForResult(intent, 1);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(ChatPage.this, "Speech to text not supported on this device", Toast.LENGTH_SHORT).show();
                }
            }
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatPage.this, HomePage.class);
                startActivity(intent);
            }
        });

        messagesArrayList = new ArrayList<>();


        profileImage = findViewById(R.id.profileimage);
        receiverName = findViewById(R.id.receiverName);


        messageAdapter = findViewById(R.id.messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdapter.setLayoutManager(linearLayoutManager);
        adapter = new MessagesAdapter(ChatPage.this, messagesArrayList);
        messageAdapter.setAdapter(adapter);

        sendBtn = findViewById(R.id.sendBtn);
        edtMessage = findViewById(R.id.edtMessage);

        Picasso.get().load(ReceiverImage).into(profileImage);
        receiverName.setText("" + ReceiverName);


        SenderUID = firebaseAuth.getUid();

        senderRoom = SenderUID + ReceiverUID;
        receiverRoom = ReceiverUID + SenderUID;


        DatabaseReference reference = database.getReference().child("user").child(firebaseAuth.getUid());
        DatabaseReference chatReference = database.getReference().child("chats").child(senderRoom).child("messages");


        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesArrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                sImage = snapshot.child("imageUri").getValue().toString();
                rImage = ReceiverImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = edtMessage.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(ChatPage.this, "Please Enter Message", Toast.LENGTH_SHORT).show();
                    return;
                }
                edtMessage.setText("");
                Date date = new Date();

                Messages messages = new Messages(message, SenderUID, date.getTime());

                database = FirebaseDatabase.getInstance();
                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                database.getReference().child("chats")
                                        .child(receiverRoom)
                                        .child("messages")
                                        .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            }
                        });

            }
        });

    }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 1) {
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (result != null && !result.isEmpty()) {
                        String recognizedText = result.get(0);
                        edtMessage.setText(recognizedText);
                    }
                }
            }
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            if (speechRecognizer != null) {
                speechRecognizer.destroy();
            }
        }

}
