package com.sakib.uconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sakib.uconnect.adapter.UserListAdapter;
import com.sakib.uconnect.model.User;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {

    private Context mContext = ContactActivity.this;
    private static final String TAG = "ContactActivity";

    private RecyclerView recyclerView;
    private UserListAdapter userListAdapter;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_contact);


        recyclerView = findViewById(R.id.recycler_view_contact);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        userList = new ArrayList<>();

        readUsers();

    }

    private void readUsers(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        if (firebaseUser != null) {
            Log.d(TAG, "readUsers: "+firebaseUser.getUid());
        }

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();



                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    Log.d(TAG, "onDataChange: "+user.getName());
                    assert user!= null;
                    assert firebaseUser!= null;
                    if(!user.getId().equals(firebaseUser.getUid())){
                        userList.add(user);
                    }
                }

                Log.d(TAG, "onDataChange: "+userList.size()+userList);
                userListAdapter = new UserListAdapter(mContext,userList);
                recyclerView.setAdapter(userListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
