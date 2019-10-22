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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sakib.uconnect.notification.Client;
import com.sakib.uconnect.notification.Data;
import com.sakib.uconnect.notification.MyResponse;
import com.sakib.uconnect.notification.Sender;
import com.sakib.uconnect.notification.Token;
import com.sakib.uconnect.adapter.ChatAdapter;
import com.sakib.uconnect.interfaces.APIService;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private SimpleDateFormat currentDate,currentTime;

    ChatAdapter adapter;
    RecyclerView recyclerView;
    List<Chat> chatList;

    ValueEventListener seenListener;

    APIService apiService;
    private boolean isNotify = false ;


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
                startActivity(new Intent(mContext,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView = findViewById(R.id.reyclerview_message_list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        currentDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        currentTime = new SimpleDateFormat("hh:mm a",Locale.getDefault());

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

                    if(user.getStatus().equals("online")){
                        tvLastSeen.setText(user.getStatus());
                    }else{
                        Log.d(TAG, "onDataChange: "+user.getLastSeenTime());
                        tvLastSeen.setText(user.getLastSeenTime());
                    }
                    if(user.getImageUrl().equals("default")){
                        ivProPic.setImageResource(R.drawable.blank_pro_pic);
                    }
                    else {
                        Glide.with(mContext.getApplicationContext()).load(user.getImageUrl()).into(ivProPic);
                    }


                    readMessage(firebaseUser.getUid(),userId,user.getImageUrl());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userId);

        btnChatBoxSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = etChatBox.getText().toString();

                isNotify = true ;
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


    private void seenMessage(final String userId){
        reference =  FirebaseDatabase.getInstance().getReference("Chats");

        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    Log.d(TAG, "onDataChange: "+firebaseUser.getUid()+" user ID : "+userId);

                    if (chat != null && chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void sendMessage(String sender , final String receiver , String message){

        reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message",message);
        hashMap.put("isSeen", false);
        hashMap.put("createdAt", SimpleDateFormat.getDateTimeInstance().format(new Date()));

        reference.child("Chats").push().setValue(hashMap);

        final DatabaseReference chatSenderReference =  FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userId);

        chatSenderReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatSenderReference.child("id").setValue(userId);


                }else{
                    chatSenderReference.child("lastSeenTime").setValue(currentTime.format(Calendar.getInstance().getTime()));
                    chatSenderReference.child("lastSeenDate").setValue(currentDate.format(Calendar.getInstance().getTime()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatReceiverReference =  FirebaseDatabase.getInstance().getReference("ChatList")
                .child(userId)
                .child(firebaseUser.getUid());

        chatReceiverReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    chatSenderReference.child("lastSeenTime").setValue(currentTime.format(Calendar.getInstance().getTime()));
                    chatSenderReference.child("lastSeenDate").setValue(currentDate.format(Calendar.getInstance().getTime()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        final String msg = message;

        reference =  FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                sendNotification(receiver,user.getName(),msg);
                isNotify = false ;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sendNotification(String receiver , final String userName , final String message){
        DatabaseReference tokenReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokenReference.orderByKey().equalTo(receiver);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);

                    Data data = new Data(firebaseUser.getUid(),R.mipmap.ic_launcher,userName+": "+message,"New message : ",userId);

                    Sender sender = new Sender(data,token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        Log.d(TAG, "sendNotification: success");
                                        if (response.body().success != 1){
                                            Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        hashMap.put("lastSeenTime",currentTime.format(Calendar.getInstance().getTime()) );
        hashMap.put("lastSeenDate",currentDate.format(Calendar.getInstance().getTime()) );
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
        reference.removeEventListener(seenListener);
    }

}
