package com.sakib.uconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
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
    private MaterialSearchView searchView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);

        searchView = findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);
        searchView.setEllipsize(true);

        recyclerView = findViewById(R.id.recycler_view_contact);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        userList = new ArrayList<>();

        readUsers();



        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                Log.d(TAG, "onQueryTextChange:submit "+query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //Do some magic
                Log.d(TAG, "onQueryTextChange: "+s);
                searchUsers(s);
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic

            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });

    }

    private void searchUsers(String s){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }
}
