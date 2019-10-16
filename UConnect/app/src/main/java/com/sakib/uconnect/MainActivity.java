package com.sakib.uconnect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sakib.uconnect.adapter.ChatUserAdapter;
import com.sakib.uconnect.model.Chat;
import com.sakib.uconnect.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Context mContext = MainActivity.this;
    private static final String TAG = "MainActivity";
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private List<User> mUsers;
    private List<String> userList;
    private ChatUserAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(0, 0);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.main_recycler_view_chat_user);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        userList = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               userList.clear();

               for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                   Chat chat = snapshot.getValue(Chat.class);
                   if(chat.getSender().equals(firebaseUser.getUid())){
                    userList.add(chat.getReceiver());
                   }

                   if(chat.getReceiver().equals(firebaseUser.getUid())){
                       userList.add(chat.getSender());
                   }
               }

               readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,ContactActivity.class);
                startActivity(intent);
            }
        });

    }

    private void readChats(){
        mUsers = new ArrayList<>();
        databaseReference  = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    for(String id : userList){

                        if(user.getId().equals(id)){
                            if(mUsers.size()!=0){
                                for(User user1:mUsers){
                                    if(!user.getId().equals(user1.getId())){
                                        mUsers.add(user);
                                    }
                                }
                            }else {
                                mUsers.add(user);
                            }

                        }

                    }


                }


                adapter = new ChatUserAdapter(mContext,mUsers, true);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.d(TAG, "onOptionsItemSelected: clicked");
            Intent intent = new Intent(mContext, ProfileActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void status(String status){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        databaseReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onStop() {
        super.onStop();
        status("offline");
    }

    //    private void setupToolbar() {
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        //handle action bar null pointer exception
//        //https://stackoverflow.com/questions/29786011/how-to-fix-getactionbar-method-may-produce-java-lang-nullpointerexception
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//        }
//        //toolbar title
//        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
//        mTitle.setText(getString(R.string.label_toolbar_activity_checkout));
//    }
}
