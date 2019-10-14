package com.sakib.uconnect.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.sakib.uconnect.ContactActivity;
import com.sakib.uconnect.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private Context mContext = LoginActivity.this;

    private TextInputLayout loginTextIlMobileNumber;
    private Button loginBtnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginActivity.this.overridePendingTransition(0, 0);
        setContentView(R.layout.activity_login);

        loginTextIlMobileNumber = findViewById(R.id.login_txt_il_mobile_number);
        loginBtnSend = findViewById(R.id.login_btn_send_code);

        loginBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobileNumber = null;
                if(loginTextIlMobileNumber.getEditText()!=null){
                    mobileNumber = "+88" + loginTextIlMobileNumber.getEditText().getText().toString();
                }

                Intent intent = new Intent(mContext, OtpActivity.class);
                intent.putExtra("mobile_number", mobileNumber);
                startActivity(intent);
            }
        });


    }

}
