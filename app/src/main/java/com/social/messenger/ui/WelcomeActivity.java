package com.social.messenger.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.social.messenger.R;

public class WelcomeActivity extends AppCompatActivity {
    private Button btnLogin,btnSignUp;

    // Hàm khởi tạo giao diện
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome); // giao diện chào

        btnLogin = findViewById(R.id.button_login);
        btnSignUp = findViewById(R.id.button_signup);

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
        btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
    }
}
