package com.sakib.uconnect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.sakib.uconnect.auth.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    private Context mContext = SplashActivity.this;
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            super.onCreate(savedInstanceState);
            Intent intent = new Intent(mContext, MainActivity.class);
            Log.d(TAG, "onCreate: user id "+FirebaseAuth.getInstance().getCurrentUser().getUid());
           // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
          //  overridePendingTransition(0, 0);
        } else {


            super.onCreate(savedInstanceState);
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);

            finish();
           // overridePendingTransition(0, 0);
        }


    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//            startActivity(intent);
//        }
//    }
}
