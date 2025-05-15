package com.social.messenger.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.social.messenger.R;
import com.social.messenger.utils.SharedPrefManager;

public class WelcomeActivity extends AppCompatActivity {


    // Hàm khởi tạo giao diện
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome); // giao diện chào

        // Kiểm tra nếu người dùng đã đăng nhập rồi
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            // Nếu đã đăng nhập, chờ 2 giây rồi chuyển đến MainActivity
            new Handler().postDelayed(() -> {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                finish();
            }, 2000); // 2000ms = 2 giây
        } else {
            // Nếu chưa đăng nhập, chờ 2 giây rồi chuyển đến LoginActivity
            new Handler().postDelayed(() -> {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                finish();
            }, 2000); // 2000ms = 2 giây
        }
    }
}
