package com.sakib.uconnect.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sakib.uconnect.MainActivity;
import com.sakib.uconnect.R;
import com.sakib.uconnect.model.User;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    private static final String TAG = "OtpActivity";
    private Context mContext = OtpActivity.this;


    private boolean isRegistered = false ;
    private String mobileNumber;
    private String verificationId;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private EditText et1,et2,et3,et4,et5,et6;
    private Button otp_btn_verify;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                //progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);


                et1.setText(String.valueOf(code.charAt(0)));
                et2.setText(String.valueOf(code.charAt(1)));
                et3.setText(String.valueOf(code.charAt(2)));
                et4.setText(String.valueOf(code.charAt(3)));
                et5.setText(String.valueOf(code.charAt(4)));
                et6.setText(String.valueOf(code.charAt(5)));

            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        et3 = findViewById(R.id.et3);
        et4 = findViewById(R.id.et4);
        et5 = findViewById(R.id.et5);
        et6 = findViewById(R.id.et6);

        otp_btn_verify = findViewById(R.id.otp_btn_verify);
        mAuth = FirebaseAuth.getInstance();
        if (getIntent().hasExtra("mobile_number")) {
            mobileNumber = getIntent().getStringExtra("mobile_number");
            sendVerificationCode(mobileNumber);
        }

        otp_btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String code = et1.getText().toString() + et2.getText().toString() + et3.getText().toString() + et4.getText().toString()+ et5.getText().toString() + et6.getText().toString();

                if ((code.isEmpty() || code.length() < 6)){

//                    otp_et_code.setError("Enter code...");
//                    otp_et_code.requestFocus();
                    return;
                }
                verifyCode(code);

            }
        });


    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

//                            checkUser();

                            Intent intent = new Intent(mContext, RegisterActivity.class);
                            intent.putExtra("mobile_number", mobileNumber);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);


//                            if(!isRegistered){
//                                Log.d(TAG, "onComplete: not registered");
//                            }


                            // registerUser();

//
//                            Intent intent = new Intent(mContext, MainActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//                            startActivity(intent);

                        } else {
                            Toast.makeText(OtpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                });
    }

    private void checkUser(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d(TAG, "onDataChange: checking"+dataSnapshot.exists()+dataSnapshot.getChildrenCount());

                if(dataSnapshot.getChildrenCount()<1){
                    Log.d(TAG, "onDataChange: not exists");
                    registerUser();
                }else {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                        User user = snapshot.getValue(User.class);
                        assert user != null;
                        assert firebaseUser != null;
                        try {
                            if (user.getId().equals(firebaseUser.getUid())) {

                                Log.d(TAG, "onDataChange: already registered");
                                Intent intent = new Intent(mContext, RegisterActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                isRegistered = true;
                                //registerUser();
                            }
                          /*  else {
                                Log.d(TAG, "onDataChange: already registered");
                                Intent intent = new Intent(mContext, RegisterActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }*/
                        } catch (Exception e) {
                            Log.d(TAG, "onDataChange: " + e);
                        }

                    }


                }
                }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if(!isRegistered){
            Log.d(TAG, "checkUser: not registered");
        }


    }


    private void  registerUser(){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String userId = null;
        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("id", userId);
            hashMap.put("name", "default");
            hashMap.put("imageUrl", "default");
            hashMap.put("mobileNumber",mobileNumber);


            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: registration success");
                        Intent intent = new Intent(mContext, RegisterActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            });

        }
    }

    /*private void registerUser(){
        String email="abc@gmail.com";
        String password="234234234124232";
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                Log.d(TAG, "onComplete: success coming here"+mAuth.getCurrentUser().getUid());

                if (task.isSuccessful()){

                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    Log.d(TAG, "onComplete: success task"+firebaseUser.getUid());
                    String userId = null;
                    if (firebaseUser != null) {
                        userId = firebaseUser.getUid();
                        Log.d(TAG, "onComplete: success task"+userId);

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put("id",userId);
                        hashMap.put("username","default");
                        hashMap.put("imageUrl","default");


                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d(TAG, "onComplete: success reg");
                                }
                            }
                        });
                    }



                }

            }
        });
    }*/

    private void sendVerificationCode(String number) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }
}
