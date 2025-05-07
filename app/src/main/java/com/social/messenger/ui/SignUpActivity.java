package com.social.messenger.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.social.messenger.R;

public class SignUpActivity extends AppCompatActivity {
//    Button btnBack, btnSignUp;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

//        btnBack = findViewById(R.id.button_back_signup);
        btnSignUp = findViewById(R.id.button_finish_signup);
//        btnBack.setOnClickListener(v -> {
//            startActivity(new Intent(this, LoginActivity.class));
//        });
        btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });


    }
}