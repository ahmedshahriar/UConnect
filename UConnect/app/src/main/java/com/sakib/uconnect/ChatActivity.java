package com.sakib.uconnect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sakib.uconnect.adapter.ChatAdapter;
import com.sakib.uconnect.model.Chat;
import com.sakib.uconnect.model.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private Context mContext = ChatActivity.this;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    Intent intent;
    private String userId;
    private TextView tvLastSeen , tvUserName ;
    private ImageView ivProPic ;
    private Button btnChatBoxSend;
    private EditText etChatBox;

    ChatAdapter adapter;
    RecyclerView recyclerView;
    List<Chat> chatList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.reyclerview_message_list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);



        tvLastSeen = findViewById(R.id.chat_tv_last_seen);
        tvUserName = findViewById(R.id.chat_tv_username);
        ivProPic = findViewById(R.id.chat_iv_pro_pic);
        btnChatBoxSend = findViewById(R.id.btn_chatbox_send);
        etChatBox = findViewById(R.id.et_chatbox);

        intent = getIntent();
        if(intent.hasExtra("userId")){
            userId = intent.getStringExtra("userId");
        }
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    tvUserName.setText(user.getName());
                    if(user.getImageUrl().equals("default")){
                        ivProPic.setImageResource(R.drawable.blank_pro_pic);
                    }
                    else {
                        Glide.with(mContext).load(user.getImageUrl()).into(ivProPic);
                    }


                    readMessage(firebaseUser.getUid(),userId,user.getImageUrl());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnChatBoxSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = etChatBox.getText().toString();
                if(!msg.equals("")){
                    sendMessage(firebaseUser.getUid(),userId,msg);
                    etChatBox.setText("");
//                    Log.d(TAG, "onClick: date"+SimpleDateFormat.getDateTimeInstance().format(new Date()));
                }else {
                    Toast.makeText(mContext,"Please type something",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void sendMessage(String sender , String receiver , String message){

        reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message",message);
        hashMap.put("createdAt", SimpleDateFormat.getDateTimeInstance().format(new Date()));

        reference.child("Chats").push().setValue(hashMap);

    }

    private void readMessage(final String myId, final String userId , final String imageUrl){
        chatList = new ArrayList<>();

        reference =  FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();


                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);


                    if(chat.getReceiver().equals(myId) && chat.getSender().equals(userId)  || chat.getReceiver().equals(userId) && chat.getSender().equals(myId)){
                        chatList.add(chat);
                    }
                }

                adapter = new ChatAdapter(mContext,chatList,imageUrl);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
