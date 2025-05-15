package com.social.messenger.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.social.messenger.R;
import com.social.messenger.dto.userlogin;
import com.social.messenger.models.User;
import com.social.messenger.repository.AuthRepository;
import com.social.messenger.repository.UserRepository;
import com.social.messenger.utils.SharedPrefManager;
import com.google.gson.Gson;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private TextView btnSignUp;
    private EditText edtEmail, edtPassword;
    private AuthRepository authRepository;
    private UserRepository userRepository;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPrefManager = SharedPrefManager.getInstance(this);
        if (sharedPrefManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.tvSignUp);
        edtEmail = findViewById(R.id.edit_Email);
        edtPassword = findViewById(R.id.edit_Password);

        authRepository = new AuthRepository();

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            userlogin user = new userlogin(email, password);

            authRepository.loginUser(user).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            String responseBodyString = response.body().string();
                            Gson gson = new Gson();
                            userlogin loginResponse = gson.fromJson(responseBodyString, userlogin.class);
                            String token = loginResponse.getToken();

                            // Kiểm tra xem token có bị null không
                            if (token != null) {
                                sharedPrefManager.saveUser(loginResponse);
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Token bị null", Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {
                            Log.e("LoginActivity", "Lỗi đọc response body", e);
                            Toast.makeText(LoginActivity.this, "Lỗi xử lý phản hồi từ server", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("LoginActivity", "Lỗi kết nối server", t);
                    Toast.makeText(LoginActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnSignUp.setOnClickListener(view -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
    }

    // Thêm phương thức chuyển đổi này
    private User convertUserLoginToUser(userlogin loginResponse) {
        User user = new User();
        user.setId(loginResponse.getId());
        user.setEmail(loginResponse.getEmail());
        // Ánh xạ các trường khác từ loginResponse sang User
        // Ví dụ:
        // user.setFullName(loginResponse.getFullName()); // Nếu có
        // user.setAvatarUrl(loginResponse.getAvatarUrl());
        // ...
        return user;
    }
}
